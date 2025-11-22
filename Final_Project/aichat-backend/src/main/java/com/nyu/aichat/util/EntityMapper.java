package com.nyu.aichat.util;

import com.nyu.aichat.dto.response.ConversationDto;
import com.nyu.aichat.dto.response.LoginResponse;
import com.nyu.aichat.dto.response.MessageDto;
import com.nyu.aichat.entity.Conversation;
import com.nyu.aichat.entity.Message;
import com.nyu.aichat.entity.User;

/**
 * Utility class for mapping entities to DTOs.
 * Centralizes DTO creation logic to follow DRY principles.
 */
public final class EntityMapper {
    private EntityMapper() {
        // Prevent instantiation
    }
    
    /**
     * Maps a Conversation entity to ConversationDto.
     * 
     * @param conversation The conversation entity to map
     * @return ConversationDto containing conversation details
     */
    public static ConversationDto toDto(Conversation conversation) {
        return new ConversationDto(
            conversation.getId(), 
            conversation.getTitle(), 
            conversation.getCreatedAt()
        );
    }
    
    /**
     * Maps a Message entity to MessageDto.
     * 
     * @param message The message entity to map
     * @return MessageDto containing message details
     */
    public static MessageDto toDto(Message message) {
        return new MessageDto(
            message.getId(),
            message.getRole().name().toLowerCase(),
            message.getContent(),
            message.getTimestamp()
        );
    }
    
    /**
     * Maps a User entity to LoginResponse.
     * 
     * @param user The user entity to map
     * @return LoginResponse containing user ID and username
     */
    public static LoginResponse toLoginResponse(User user) {
        return new LoginResponse(user.getId(), user.getUsername());
    }
}

