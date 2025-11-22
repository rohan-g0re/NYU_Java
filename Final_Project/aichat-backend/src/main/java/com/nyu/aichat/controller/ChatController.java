package com.nyu.aichat.controller;

import com.nyu.aichat.dto.request.CreateConversationRequest;
import com.nyu.aichat.dto.request.SendMessageRequest;
import com.nyu.aichat.dto.request.UpdateTitleRequest;
import com.nyu.aichat.dto.response.ConversationDto;
import com.nyu.aichat.dto.response.MessageDto;
import com.nyu.aichat.dto.response.SendMessageResponse;
import com.nyu.aichat.service.ChatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/v1/conversations")
public class ChatController {
    private final ChatService chatService;
    
    @Autowired
    public ChatController(ChatService chatService) {
        this.chatService = chatService;
    }
    
    @PostMapping
    public ResponseEntity<ConversationDto> createConversation(
            @RequestHeader("X-User-Id") Long userId,
            @Valid @RequestBody CreateConversationRequest request) {
        ConversationDto conversation = chatService.createConversation(userId, request.getTitle());
        return ResponseEntity.status(HttpStatus.CREATED).body(conversation);
    }
    
    @GetMapping
    public ResponseEntity<List<ConversationDto>> getUserConversations(
            @RequestHeader("X-User-Id") Long userId) {
        List<ConversationDto> conversations = chatService.getUserConversations(userId);
        return ResponseEntity.ok(conversations);
    }
    
    @GetMapping("/{id}/messages")
    public ResponseEntity<List<MessageDto>> getMessages(
            @PathVariable Long id,
            @RequestHeader("X-User-Id") Long userId) {
        List<MessageDto> messages = chatService.getConversationHistory(id, userId);
        return ResponseEntity.ok(messages);
    }
    
    @PostMapping("/{id}/messages")
    public ResponseEntity<SendMessageResponse> sendMessage(
            @PathVariable Long id,
            @RequestHeader("X-User-Id") Long userId,
            @Valid @RequestBody SendMessageRequest request) {
        MessageDto assistantMessage = chatService.sendUserMessageAndGetAiReply(id, userId, request.getText());
        return ResponseEntity.ok(new SendMessageResponse(assistantMessage));
    }
    
    @PutMapping("/{id}/title")
    public ResponseEntity<Void> updateTitle(
            @PathVariable Long id,
            @RequestHeader("X-User-Id") Long userId,
            @Valid @RequestBody UpdateTitleRequest request) {
        chatService.updateConversationTitle(id, userId, request.getTitle());
        return ResponseEntity.ok().build();
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteConversation(
            @PathVariable Long id,
            @RequestHeader("X-User-Id") Long userId) {
        chatService.deleteConversation(id, userId);
        return ResponseEntity.ok().build();
    }
}

