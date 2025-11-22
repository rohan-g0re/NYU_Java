package com.nyu.aichat.client.ui;

import com.nyu.aichat.client.api.ApiClient;
import com.nyu.aichat.client.api.ApiException;
import com.nyu.aichat.client.model.ConversationView;
import com.nyu.aichat.client.model.UserSession;

import javax.swing.*;
import java.awt.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MainChatFrame extends JFrame {
    private UserSession userSession;
    private ApiClient apiClient;
    private ExecutorService executorService;
    
    private JSplitPane mainSplitPane;
    private ConversationPanel conversationPanel;
    private MessagePanel messagePanel;
    private InputPanel inputPanel;
    
    private Long currentConversationId;
    
    public MainChatFrame(UserSession userSession) {
        this.userSession = userSession;
        this.apiClient = new ApiClient();
        this.executorService = Executors.newFixedThreadPool(4);
        this.currentConversationId = null;
        
        setupUI();
        loadConversations();
    }
    
    private void setupUI() {
        setTitle("AI Chat - " + userSession.getUsername());
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1000, 700);
        setLocationRelativeTo(null);
        
        // Main split pane
        mainSplitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        mainSplitPane.setDividerLocation(250);
        mainSplitPane.setResizeWeight(0.25);
        
        // Left: Conversation panel
        conversationPanel = new ConversationPanel(this::onConversationSelected);
        conversationPanel.setOnCreateNewChatCallback(() -> {
            String title = JOptionPane.showInputDialog(this, "Enter conversation title:", "New Chat");
            if (title != null) {
                createNewConversation(title.trim().isEmpty() ? null : title);
            }
        });
        mainSplitPane.setLeftComponent(conversationPanel);
        
        // Right: Message panel and input
        JPanel rightPanel = new JPanel(new BorderLayout());
        messagePanel = new MessagePanel();
        inputPanel = new InputPanel(this::onSendMessage);
        
        rightPanel.add(messagePanel, BorderLayout.CENTER);
        rightPanel.add(inputPanel, BorderLayout.SOUTH);
        
        mainSplitPane.setRightComponent(rightPanel);
        
        add(mainSplitPane);
    }
    
    private void loadConversations() {
        executorService.submit(() -> {
            try {
                var conversations = apiClient.getConversations(userSession.getUserId());
                SwingUtilities.invokeLater(() -> {
                    conversationPanel.setConversations(conversations);
                });
            } catch (ApiException e) {
                SwingUtilities.invokeLater(() -> {
                    JOptionPane.showMessageDialog(this, "Error loading conversations: " + e.getMessage(),
                            "Error", JOptionPane.ERROR_MESSAGE);
                });
            }
        });
    }
    
    private void onConversationSelected(Long conversationId) {
        currentConversationId = conversationId;
        messagePanel.setCurrentConversation(conversationId);
        messagePanel.clearMessages();
        
        executorService.submit(() -> {
            try {
                var messages = apiClient.getMessages(conversationId, userSession.getUserId());
                SwingUtilities.invokeLater(() -> {
                    if (currentConversationId.equals(conversationId)) {
                        messagePanel.setMessages(messages);
                    }
                });
            } catch (ApiException e) {
                SwingUtilities.invokeLater(() -> {
                    JOptionPane.showMessageDialog(this, "Error loading messages: " + e.getMessage(),
                            "Error", JOptionPane.ERROR_MESSAGE);
                });
            }
        });
    }
    
    private void onSendMessage(String text) {
        if (currentConversationId == null) {
            JOptionPane.showMessageDialog(this, "Please select or create a conversation first",
                    "No Conversation", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        final Long convId = currentConversationId;
        
        // Add user message to UI immediately
        messagePanel.addMessage(new com.nyu.aichat.client.model.MessageView(
                null, "user", text, java.time.Instant.now()), true);
        
        // Send message in background
        executorService.submit(() -> {
            try {
                var assistantMessage = apiClient.sendMessage(
                        convId, userSession.getUserId(), text);
                
                SwingUtilities.invokeLater(() -> {
                    if (currentConversationId != null && currentConversationId.equals(convId)) {
                        messagePanel.addMessage(assistantMessage, false);
                    }
                });
            } catch (ApiException e) {
                SwingUtilities.invokeLater(() -> {
                    JOptionPane.showMessageDialog(this, "Error sending message: " + e.getMessage(),
                            "Error", JOptionPane.ERROR_MESSAGE);
                });
            }
        });
    }
    
    public void createNewConversation(String title) {
        executorService.submit(() -> {
            try {
                var conversation = apiClient.createConversation(userSession.getUserId(), title);
                SwingUtilities.invokeLater(() -> {
                    conversationPanel.addConversation(conversation);
                    onConversationSelected(conversation.getId());
                });
            } catch (ApiException e) {
                SwingUtilities.invokeLater(() -> {
                    JOptionPane.showMessageDialog(this, "Error creating conversation: " + e.getMessage(),
                            "Error", JOptionPane.ERROR_MESSAGE);
                });
            }
        });
    }
}

