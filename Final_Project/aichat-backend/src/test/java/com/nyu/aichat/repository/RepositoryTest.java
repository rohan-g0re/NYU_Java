package com.nyu.aichat.repository;

import com.nyu.aichat.entity.Conversation;
import com.nyu.aichat.entity.Message;
import com.nyu.aichat.entity.MessageRole;
import com.nyu.aichat.entity.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.TestPropertySource;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@TestPropertySource(properties = {
    "spring.datasource.url=jdbc:h2:mem:testdb",
    "spring.jpa.hibernate.ddl-auto=create-drop"
})
class RepositoryTest {
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private ConversationRepository conversationRepository;
    
    @Autowired
    private MessageRepository messageRepository;
    
    @Test
    void testUserCRUD() {
        // Create
        User user = new User("testuser", "hashedpassword");
        user = userRepository.save(user);
        assertNotNull(user.getId());
        assertNotNull(user.getCreatedAt());
        
        // Read
        Optional<User> found = userRepository.findByUsername("testuser");
        assertTrue(found.isPresent());
        assertEquals("testuser", found.get().getUsername());
        
        // Check existence
        assertTrue(userRepository.existsByUsername("testuser"));
        assertFalse(userRepository.existsByUsername("nonexistent"));
        
        // Update
        user.setUsername("updateduser");
        userRepository.save(user);
        
        // Verify update
        Optional<User> updated = userRepository.findByUsername("updateduser");
        assertTrue(updated.isPresent());
        
        // Delete
        userRepository.delete(user);
        assertFalse(userRepository.existsByUsername("updateduser"));
    }
    
    @Test
    void testConversationCRUD() {
        // Create user first
        User user = new User("testuser", "hash");
        user = userRepository.save(user);
        
        // Create conversation
        Conversation conv = new Conversation(user, "Test Chat");
        conv = conversationRepository.save(conv);
        assertNotNull(conv.getId());
        assertNotNull(conv.getCreatedAt());
        assertFalse(conv.getIsDeleted());
        
        // Read
        List<Conversation> userConvs = conversationRepository
                .findByUserIdAndIsDeletedFalseOrderByCreatedAtDesc(user.getId());
        assertEquals(1, userConvs.size());
        assertEquals("Test Chat", userConvs.get(0).getTitle());
        
        // Update
        conv.setTitle("Updated Title");
        conversationRepository.save(conv);
        
        // Soft delete
        conv.setIsDeleted(true);
        conversationRepository.save(conv);
        
        // Verify soft delete
        List<Conversation> activeConvs = conversationRepository
                .findByUserIdAndIsDeletedFalseOrderByCreatedAtDesc(user.getId());
        assertEquals(0, activeConvs.size());
    }
    
    @Test
    void testMessageCRUD() {
        // Create user and conversation
        User user = new User("testuser", "hash");
        user = userRepository.save(user);
        
        Conversation conv = new Conversation(user, "Test Chat");
        conv = conversationRepository.save(conv);
        
        // Create messages
        Message msg1 = new Message(conv, MessageRole.USER, "Hello");
        msg1 = messageRepository.save(msg1);
        assertNotNull(msg1.getId());
        assertNotNull(msg1.getTimestamp());
        
        Message msg2 = new Message(conv, MessageRole.ASSISTANT, "Hi there");
        msg2 = messageRepository.save(msg2);
        
        // Read messages ordered by timestamp
        List<Message> messages = messageRepository
                .findByConversationIdOrderByTimestampAscIdAsc(conv.getId());
        assertEquals(2, messages.size());
        assertEquals("Hello", messages.get(0).getContent());
        assertEquals("Hi there", messages.get(1).getContent());
        
        // Test count
        long count = messageRepository.countByConversationId(conv.getId());
        assertEquals(2, count);
    }
    
    @Test
    void testMessageOrdering() {
        // Create user and conversation
        User user = new User("testuser", "hash");
        user = userRepository.save(user);
        
        Conversation conv = new Conversation(user, "Test Chat");
        conv = conversationRepository.save(conv);
        
        // Create multiple messages with slight time differences
        Message msg1 = new Message(conv, MessageRole.USER, "First");
        msg1.setTimestamp(Instant.now().minusSeconds(10));
        msg1 = messageRepository.save(msg1);
        
        Message msg2 = new Message(conv, MessageRole.ASSISTANT, "Second");
        msg2.setTimestamp(Instant.now().minusSeconds(5));
        msg2 = messageRepository.save(msg2);
        
        Message msg3 = new Message(conv, MessageRole.USER, "Third");
        msg3.setTimestamp(Instant.now());
        msg3 = messageRepository.save(msg3);
        
        // Verify ordering
        List<Message> messages = messageRepository
                .findByConversationIdOrderByTimestampAscIdAsc(conv.getId());
        assertEquals(3, messages.size());
        assertEquals("First", messages.get(0).getContent());
        assertEquals("Second", messages.get(1).getContent());
        assertEquals("Third", messages.get(2).getContent());
    }
    
    @Test
    void testGetTop6Messages() {
        // Create user and conversation
        User user = new User("testuser", "hash");
        user = userRepository.save(user);
        
        Conversation conv = new Conversation(user, "Test Chat");
        conv = conversationRepository.save(conv);
        
        // Create 10 messages
        for (int i = 1; i <= 10; i++) {
            Message msg = new Message(conv, MessageRole.USER, "Message " + i);
            msg.setTimestamp(Instant.now().minusSeconds(10 - i));
            messageRepository.save(msg);
        }
        
        // Get top 6 (most recent)
        List<Message> top6 = messageRepository
                .findTop6ByConversationIdOrderByTimestampDescIdDesc(conv.getId());
        assertEquals(6, top6.size());
        assertEquals("Message 10", top6.get(0).getContent()); // Most recent
        assertEquals("Message 5", top6.get(5).getContent());  // 6th most recent
    }
}

