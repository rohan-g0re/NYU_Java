package com.nyu.aichat;

import com.nyu.aichat.entity.Conversation;
import com.nyu.aichat.entity.Message;
import com.nyu.aichat.entity.MessageRole;
import com.nyu.aichat.entity.User;
import com.nyu.aichat.repository.ConversationRepository;
import com.nyu.aichat.repository.MessageRepository;
import com.nyu.aichat.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("test")
public class RepositoryIntegrationTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ConversationRepository conversationRepository;

    @Autowired
    private MessageRepository messageRepository;

    @Test
    public void testUserCRUD() {
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
    }

    @Test
    public void testConversationCRUD() {
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
        List<Conversation> conversations = conversationRepository
                .findByUserIdAndIsDeletedFalseOrderByCreatedAtDesc(user.getId());
        assertEquals(1, conversations.size());
        assertEquals("Test Chat", conversations.get(0).getTitle());
    }

    @Test
    public void testMessageOrdering() {
        // Create user and conversation
        User user = new User("testuser", "hash");
        user = userRepository.save(user);
        Conversation conv = new Conversation(user, "Test Chat");
        conv = conversationRepository.save(conv);

        // Create multiple messages
        Message msg1 = new Message(conv, MessageRole.USER, "First message");
        msg1 = messageRepository.save(msg1);

        Message msg2 = new Message(conv, MessageRole.ASSISTANT, "Second message");
        msg2 = messageRepository.save(msg2);

        Message msg3 = new Message(conv, MessageRole.USER, "Third message");
        msg3 = messageRepository.save(msg3);

        // Verify ordering
        List<Message> messages = messageRepository
                .findByConversationIdOrderByTimestampAscIdAsc(conv.getId());
        assertEquals(3, messages.size());
        assertEquals("First message", messages.get(0).getContent());
        assertEquals("Third message", messages.get(2).getContent());
    }

    @Test
    public void testMessageContextRetrieval() {
        // Create user and conversation
        User user = new User("testuser", "hash");
        user = userRepository.save(user);
        Conversation conv = new Conversation(user, "Test Chat");
        conv = conversationRepository.save(conv);

        // Create 10 messages
        for (int i = 1; i <= 10; i++) {
            Message msg = new Message(conv, MessageRole.USER, "Message " + i);
            messageRepository.save(msg);
        }

        // Get last 6 messages (should be messages 5-10 in DESC order)
        List<Message> recent = messageRepository
                .findTop6ByConversationIdOrderByTimestampDescIdDesc(conv.getId());
        assertEquals(6, recent.size());
        // Most recent should be first
        assertTrue(recent.get(0).getContent().contains("10"));
    }
}

