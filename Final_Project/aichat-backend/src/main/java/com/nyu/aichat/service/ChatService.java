package com.nyu.aichat.service;

import com.nyu.aichat.dto.response.ConversationDto;
import com.nyu.aichat.dto.response.MessageDto;
import com.nyu.aichat.entity.Conversation;
import com.nyu.aichat.entity.Message;
import com.nyu.aichat.entity.MessageRole;
import com.nyu.aichat.entity.User;
import com.nyu.aichat.exception.ConversationNotFoundException;
import com.nyu.aichat.exception.UnauthorizedException;
import com.nyu.aichat.exception.ValidationException;
import com.nyu.aichat.repository.ConversationRepository;
import com.nyu.aichat.repository.MessageRepository;
import com.nyu.aichat.repository.UserRepository;
import com.nyu.aichat.util.Constants;
import com.nyu.aichat.util.EntityMapper;
import com.nyu.aichat.util.ValidationUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Service for managing conversations and messages.
 * Handles conversation CRUD operations, message persistence, and AI integration.
 */
@Service
public class ChatService {
    private static final Logger logger = LoggerFactory.getLogger(ChatService.class);
    
    private final ConversationRepository conversationRepository;
    private final MessageRepository messageRepository;
    private final UserRepository userRepository;
    private final GeminiService geminiService;
    
    @Autowired
    public ChatService(ConversationRepository conversationRepository,
                      MessageRepository messageRepository,
                      UserRepository userRepository,
                      GeminiService geminiService) {
        this.conversationRepository = conversationRepository;
        this.messageRepository = messageRepository;
        this.userRepository = userRepository;
        this.geminiService = geminiService;
    }
    
    /**
     * Creates a new conversation for the specified user.
     * 
     * @param userId The ID of the user creating the conversation
     * @param title The title of the conversation (can be null or empty)
     * @return ConversationDto containing the created conversation details
     * @throws ValidationException if user doesn't exist or conversation limit exceeded
     */
    @Transactional
    public ConversationDto createConversation(Long userId, String title) {
        if (userId == null) {
            throw new ValidationException(Constants.ERROR_USER_ID_NULL);
        }
        
        // Validate user exists
        User user = userRepository.findById(userId)
                .orElseThrow(() -> {
                    logger.warn("Attempt to create conversation for non-existent user: {}", userId);
                    return new ValidationException(Constants.ERROR_USER_NOT_FOUND);
                });
        
        // Check conversation limit
        long currentCount = conversationRepository.countByUserIdAndIsDeletedFalse(userId);
        ValidationUtil.validateConversationLimit(currentCount);
        
        // Set default title if needed
        if (title == null || title.trim().isEmpty()) {
            title = title == null ? Constants.DEFAULT_TITLE_NEW : Constants.DEFAULT_TITLE_UNTITLED;
        }
        
        // Create conversation
        Conversation conversation = new Conversation(user, title);
        conversation = conversationRepository.save(conversation);
        
        logger.info("Created conversation {} for user {}", conversation.getId(), userId);
        return EntityMapper.toDto(conversation);
    }
    
    /**
     * Retrieves all non-deleted conversations for a user, ordered by creation date (newest first).
     * 
     * @param userId The ID of the user
     * @return List of ConversationDto objects
     */
    @Transactional(readOnly = true)
    public List<ConversationDto> getUserConversations(Long userId) {
        if (userId == null) {
            throw new ValidationException(Constants.ERROR_USER_ID_NULL);
        }
        
        List<Conversation> conversations = conversationRepository
                .findByUserIdAndIsDeletedFalseOrderByCreatedAtDesc(userId);
        
        return conversations.stream()
                .map(EntityMapper::toDto)
                .collect(Collectors.toList());
    }
    
    /**
     * Retrieves the full message history for a conversation.
     * 
     * @param conversationId The ID of the conversation
     * @param userId The ID of the user (for ownership validation)
     * @return List of MessageDto objects in chronological order
     * @throws UnauthorizedException if user doesn't own the conversation
     */
    @Transactional(readOnly = true)
    public List<MessageDto> getConversationHistory(Long conversationId, Long userId) {
        if (conversationId == null) {
            throw new ValidationException(Constants.ERROR_CONVERSATION_ID_NULL);
        }
        if (userId == null) {
            throw new ValidationException(Constants.ERROR_USER_ID_NULL);
        }
        
        // Validate ownership
        validateConversationOwnership(conversationId, userId);
        
        // Get messages ordered by timestamp
        List<Message> messages = messageRepository.findByConversationIdOrderByTimestampAscIdAsc(conversationId);
        
        return messages.stream()
                .map(EntityMapper::toDto)
                .collect(Collectors.toList());
    }
    
    /**
     * Sends a user message, generates an AI reply, and persists both messages.
     * 
     * @param conversationId The ID of the conversation
     * @param userId The ID of the user sending the message
     * @param userText The message text from the user
     * @return MessageDto containing the assistant's reply
     * @throws UnauthorizedException if user doesn't own the conversation
     * @throws ValidationException if message limit exceeded
     */
    @Transactional
    public MessageDto sendUserMessageAndGetAiReply(Long conversationId, Long userId, String userText) {
        if (conversationId == null) {
            throw new ValidationException(Constants.ERROR_CONVERSATION_ID_NULL);
        }
        if (userId == null) {
            throw new ValidationException(Constants.ERROR_USER_ID_NULL);
        }
        if (userText == null || userText.trim().isEmpty()) {
            throw new ValidationException(Constants.ERROR_MESSAGE_TEXT_NULL);
        }
        
        // Validate ownership
        validateConversationOwnership(conversationId, userId);
        
        // Validate message limit
        long messageCount = messageRepository.countByConversationId(conversationId);
        ValidationUtil.validateMessageLimit(messageCount);
        
        // Get context messages BEFORE adding user message (last 6 existing messages)
        // These will be used to provide context to Gemini, then we add the current user message
        List<Message> contextMessages = messageRepository
                .findTop6ByConversationIdOrderByTimestampDescIdDesc(conversationId);
        
        // Add user message
        Message userMessage = addMessage(conversationId, MessageRole.USER, userText);
        
        // Generate AI response
        // Note: contextMessages contains previous messages, userText is the current message
        // buildPrompt will combine them properly
        String aiResponseText;
        try {
            aiResponseText = geminiService.generateResponse(userText, contextMessages);
        } catch (Exception e) {
            logger.error("Gemini API error while generating response for conversation {}", conversationId, e);
            aiResponseText = Constants.ERROR_AI_FALLBACK;
        }
        
        // Add assistant message
        Message assistantMessage = addMessage(conversationId, MessageRole.ASSISTANT, aiResponseText);
        
        logger.info("Message exchange completed for conversation {}", conversationId);
        return EntityMapper.toDto(assistantMessage);
    }
    
    /**
     * Adds a message to a conversation and maintains linked-list integrity.
     * This method is transactional to ensure atomicity of linked-list updates.
     * 
     * @param conversationId The ID of the conversation
     * @param role The role of the message sender (USER or ASSISTANT)
     * @param content The message content
     * @return The persisted Message entity
     * @throws ConversationNotFoundException if conversation doesn't exist
     */
    @Transactional(propagation = Propagation.REQUIRED)
    private Message addMessage(Long conversationId, MessageRole role, String content) {
        // Load conversation
        Conversation conversation = conversationRepository.findById(conversationId)
                .orElseThrow(() -> {
                    logger.error("Attempt to add message to non-existent conversation: {}", conversationId);
                    return new ConversationNotFoundException(Constants.ERROR_CONVERSATION_NOT_FOUND);
                });
        
        // Create new message
        Message newMessage = new Message(conversation, role, content);
        newMessage.setPrevMessageId(conversation.getLastMessageId());
        newMessage.setNextMessageId(null);
        newMessage = messageRepository.save(newMessage);
        
        // Update previous message's next pointer
        if (conversation.getLastMessageId() != null) {
            Message prevMessage = messageRepository.findById(conversation.getLastMessageId())
                    .orElseThrow(() -> {
                        logger.error("Previous message {} not found for conversation {}", 
                                conversation.getLastMessageId(), conversationId);
                        return new RuntimeException(Constants.ERROR_PREVIOUS_MESSAGE_NOT_FOUND);
                    });
            prevMessage.setNextMessageId(newMessage.getId());
            messageRepository.save(prevMessage);
        }
        
        // Update conversation head/tail
        if (conversation.getHeadMessageId() == null) {
            conversation.setHeadMessageId(newMessage.getId());
        }
        conversation.setLastMessageId(newMessage.getId());
        conversationRepository.save(conversation);
        
        return newMessage;
    }
    
    /**
     * Updates the title of a conversation.
     * 
     * @param conversationId The ID of the conversation
     * @param userId The ID of the user (for ownership validation)
     * @param newTitle The new title
     * @throws ConversationNotFoundException if conversation doesn't exist or user doesn't own it
     */
    @Transactional
    public void updateConversationTitle(Long conversationId, Long userId, String newTitle) {
        if (conversationId == null) {
            throw new ValidationException(Constants.ERROR_CONVERSATION_ID_NULL);
        }
        if (userId == null) {
            throw new ValidationException(Constants.ERROR_USER_ID_NULL);
        }
        
        Conversation conversation = loadConversationWithOwnership(conversationId, userId);
        
        conversation.setTitle(newTitle);
        conversationRepository.save(conversation);
        
        logger.info("Updated title for conversation {}", conversationId);
    }
    
    /**
     * Soft-deletes a conversation (marks as deleted without removing from database).
     * 
     * @param conversationId The ID of the conversation
     * @param userId The ID of the user (for ownership validation)
     * @throws ConversationNotFoundException if conversation doesn't exist or user doesn't own it
     */
    @Transactional
    public void deleteConversation(Long conversationId, Long userId) {
        if (conversationId == null) {
            throw new ValidationException(Constants.ERROR_CONVERSATION_ID_NULL);
        }
        if (userId == null) {
            throw new ValidationException(Constants.ERROR_USER_ID_NULL);
        }
        
        Conversation conversation = loadConversationWithOwnership(conversationId, userId);
        
        // Soft delete
        conversation.setIsDeleted(true);
        conversationRepository.save(conversation);
        
        logger.info("Soft-deleted conversation {} for user {}", conversationId, userId);
    }
    
    /**
     * Validates that a user owns a conversation.
     * 
     * @param conversationId The ID of the conversation
     * @param userId The ID of the user
     * @throws UnauthorizedException if user doesn't own the conversation
     */
    private void validateConversationOwnership(Long conversationId, Long userId) {
        conversationRepository
                .findByIdAndUserIdAndIsDeletedFalse(conversationId, userId)
                .orElseThrow(() -> {
                    logger.warn("User {} attempted to access conversation {} without ownership", userId, conversationId);
                    return new UnauthorizedException(Constants.ERROR_UNAUTHORIZED_CONVERSATION);
                });
    }
    
    /**
     * Loads a conversation and validates ownership.
     * Centralizes the repeated pattern of loading conversations with ownership checks.
     * 
     * @param conversationId The ID of the conversation
     * @param userId The ID of the user
     * @return The Conversation entity
     * @throws ConversationNotFoundException if conversation doesn't exist or user doesn't own it
     */
    private Conversation loadConversationWithOwnership(Long conversationId, Long userId) {
        return conversationRepository
                .findByIdAndUserIdAndIsDeletedFalse(conversationId, userId)
                .orElseThrow(() -> {
                    logger.warn("Conversation {} not found or user {} doesn't own it", conversationId, userId);
                    return new ConversationNotFoundException(Constants.ERROR_CONVERSATION_NOT_FOUND);
                });
    }
}
