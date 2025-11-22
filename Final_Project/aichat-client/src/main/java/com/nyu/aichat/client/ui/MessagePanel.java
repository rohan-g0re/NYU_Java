package com.nyu.aichat.client.ui;

import com.nyu.aichat.client.model.MessageView;

import javax.swing.*;
import java.awt.*;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class MessagePanel extends JScrollPane {
    private JPanel contentPanel;
    private BoxLayout boxLayout;
    private Long currentConversationId;
    private static final DateTimeFormatter TIME_FORMATTER = 
            DateTimeFormatter.ofPattern("HH:mm").withZone(ZoneId.systemDefault());
    
    public MessagePanel() {
        this.currentConversationId = null;
        setupUI();
    }
    
    private void setupUI() {
        contentPanel = new JPanel();
        boxLayout = new BoxLayout(contentPanel, BoxLayout.Y_AXIS);
        contentPanel.setLayout(boxLayout);
        contentPanel.setBackground(Color.WHITE);
        
        setViewportView(contentPanel);
        setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        setBorder(BorderFactory.createTitledBorder("Messages"));
    }
    
    public void setMessages(List<MessageView> messages) {
        clearMessages();
        for (MessageView message : messages) {
            boolean isUser = "user".equals(message.getRole());
            addMessage(message, isUser);
        }
        // Scroll to bottom
        SwingUtilities.invokeLater(() -> {
            JScrollBar vertical = getVerticalScrollBar();
            vertical.setValue(vertical.getMaximum());
        });
    }
    
    public void addMessage(MessageView message, boolean isUser) {
        MessageBubble bubble = createMessageBubble(message, isUser);
        contentPanel.add(bubble);
        contentPanel.revalidate();
        contentPanel.repaint();
        
        // Scroll to bottom
        SwingUtilities.invokeLater(() -> {
            JScrollBar vertical = getVerticalScrollBar();
            vertical.setValue(vertical.getMaximum());
        });
    }
    
    public void clearMessages() {
        contentPanel.removeAll();
        contentPanel.revalidate();
        contentPanel.repaint();
    }
    
    public void setCurrentConversation(Long conversationId) {
        this.currentConversationId = conversationId;
    }
    
    private MessageBubble createMessageBubble(MessageView message, boolean isUser) {
        return new MessageBubble(message.getContent(), message.getTs(), isUser);
    }
}

