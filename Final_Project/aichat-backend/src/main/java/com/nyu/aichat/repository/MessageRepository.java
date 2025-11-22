package com.nyu.aichat.repository;

import com.nyu.aichat.entity.Message;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MessageRepository extends JpaRepository<Message, Long> {
    List<Message> findByConversationIdOrderByTimestampAscIdAsc(Long conversationId);
    List<Message> findTop6ByConversationIdOrderByTimestampDescIdDesc(Long conversationId);
    long countByConversationId(Long conversationId);
}

