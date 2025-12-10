package com.nyu.aichat.client.ui;

import com.nyu.aichat.client.model.MessageView;

import javax.swing.*;
import java.awt.*;
import java.util.List;

/**
 * Scrollable panel displaying chat messages.
 */
public class MessagePanel extends JScrollPane {
    private JPanel contentPanel;
    private Long currentConversationId;
    
    public MessagePanel() {
        this.currentConversationId = null;
        setupUI();
    }
    
    private void setupUI() {
        contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        contentPanel.setBackground(Color.WHITE);
        
        setViewportView(contentPanel);
        setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        setBorder(null);
    }
    
    public void setMessages(List<MessageView> messages) {
        contentPanel.removeAll();
        for (MessageView message : messages) {
            addMessage(message, message.isUserMessage());
        }
        revalidate();
        repaint();
        scrollToBottom();
    }
    
    public void addMessage(MessageView message, boolean isUser) {
        MessageBubble bubble = createMessageBubble(message, isUser);
        
        // Wrap bubble in a panel with FlowLayout to control positioning
        // User messages on RIGHT, AI messages on LEFT
        JPanel wrapper = new JPanel(new FlowLayout(isUser ? FlowLayout.RIGHT : FlowLayout.LEFT, 0, 0));
        wrapper.setOpaque(false);
        wrapper.setMaximumSize(new Dimension(Integer.MAX_VALUE, Integer.MAX_VALUE));
        wrapper.add(bubble);
        
        contentPanel.add(wrapper);
        contentPanel.add(Box.createVerticalStrut(10)); // Spacing between messages
        
        revalidate();
        repaint();
        scrollToBottom();
    }
    
    public void clearMessages() {
        contentPanel.removeAll();
        currentConversationId = null;
        revalidate();
        repaint();
    }
    
    public void setCurrentConversation(Long conversationId) {
        this.currentConversationId = conversationId;
    }
    
    private MessageBubble createMessageBubble(MessageView message, boolean isUser) {
        return new MessageBubble(message.getContent(), message.getTs(), isUser);
    }
    
    private void scrollToBottom() {
        SwingUtilities.invokeLater(() -> {
            JScrollBar vertical = getVerticalScrollBar();
            vertical.setValue(vertical.getMaximum());
        });
    }
}

