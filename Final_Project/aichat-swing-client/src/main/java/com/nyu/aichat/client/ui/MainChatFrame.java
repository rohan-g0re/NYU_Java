package com.nyu.aichat.client.ui;

import com.nyu.aichat.client.api.ApiClient;
import com.nyu.aichat.client.api.ApiException;
import com.nyu.aichat.client.model.ConversationView;
import com.nyu.aichat.client.model.MessageView;
import com.nyu.aichat.client.model.UserSession;
import com.nyu.aichat.client.util.ConfigLoader;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Main chat interface window.
 * Contains conversation panel (left) and message panel + input panel (right).
 */
public class MainChatFrame extends JFrame {
    private UserSession userSession;
    private ApiClient apiClient;
    private ExecutorService executorService;
    
    private JSplitPane mainSplitPane;
    private ConversationPanel conversationPanel;
    private MessagePanel messagePanel;
    private InputPanel inputPanel;
    
    private Long currentConversationId;
    
    public MainChatFrame(UserSession userSession, ApiClient apiClient) {
        this.userSession = userSession;
        this.apiClient = apiClient;
        this.executorService = Executors.newCachedThreadPool();
        this.currentConversationId = null;
        
        setupUI();
        loadConversations();
        
        // Handle window close
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                shutdown();
                System.exit(0);
            }
        });
    }
    
    private void setupUI() {
        setTitle("AI Chat - " + userSession.getUsername());
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        
        int width = ConfigLoader.getWindowWidth();
        int height = ConfigLoader.getWindowHeight();
        setSize(width, height);
        setLocationRelativeTo(null);
        
        // Main split pane (horizontal)
        mainSplitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        mainSplitPane.setDividerLocation(ConfigLoader.getConversationPanelWidth());
        mainSplitPane.setResizeWeight(0.0); // Left panel fixed width
        
        // Left: Conversation panel
        conversationPanel = new ConversationPanel(
            this::onConversationSelected,
            this::onNewChat,
            this::onDeleteConversation,
            this::onRenameConversation
        );
        mainSplitPane.setLeftComponent(conversationPanel);
        
        // Right: Message area + Input panel
        JPanel rightPanel = new JPanel(new BorderLayout());
        
        messagePanel = new MessagePanel();
        rightPanel.add(messagePanel, BorderLayout.CENTER);
        
        inputPanel = new InputPanel(this::onSendMessage);
        rightPanel.add(inputPanel, BorderLayout.SOUTH);
        
        mainSplitPane.setRightComponent(rightPanel);
        
        add(mainSplitPane);
    }
    
    private void loadConversations() {
        executorService.execute(() -> {
            try {
                List<ConversationView> conversations = apiClient.getConversations(userSession.getUserId());
                SwingUtilities.invokeLater(() -> {
                    conversationPanel.setConversations(conversations);
                });
            } catch (ApiException e) {
                SwingUtilities.invokeLater(() -> {
                    JOptionPane.showMessageDialog(this, 
                        "Failed to load conversations: " + e.getMessage(),
                        "Error", JOptionPane.ERROR_MESSAGE);
                });
            }
        });
    }
    
    private void onConversationSelected(Long conversationId) {
        if (conversationId == null) {
            currentConversationId = null;
            messagePanel.clearMessages();
            inputPanel.setEnabled(false);
            return;
        }
        
        currentConversationId = conversationId;
        inputPanel.setEnabled(true);
        
        executorService.execute(() -> {
            try {
                List<MessageView> messages = apiClient.getMessages(conversationId, userSession.getUserId());
                SwingUtilities.invokeLater(() -> {
                    messagePanel.setMessages(messages);
                });
            } catch (ApiException e) {
                SwingUtilities.invokeLater(() -> {
                    JOptionPane.showMessageDialog(this,
                        "Failed to load messages: " + e.getMessage(),
                        "Error", JOptionPane.ERROR_MESSAGE);
                });
            }
        });
    }
    
    private void onNewChat() {
        executorService.execute(() -> {
            try {
                ConversationView newConversation = apiClient.createConversation(userSession.getUserId(), null);
                SwingUtilities.invokeLater(() -> {
                    conversationPanel.addConversation(newConversation);
                    onConversationSelected(newConversation.getId());
                });
            } catch (ApiException e) {
                SwingUtilities.invokeLater(() -> {
                    JOptionPane.showMessageDialog(this,
                        "Failed to create conversation: " + e.getMessage(),
                        "Error", JOptionPane.ERROR_MESSAGE);
                });
            }
        });
    }
    
    private void onRenameConversation(Long conversationId) {
        // Find current title
        ConversationView conversation = conversationPanel.getConversationById(conversationId);
        if (conversation == null) {
            JOptionPane.showMessageDialog(this,
                "Conversation not found",
                "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        String currentTitle = conversation.getTitle();
        if (currentTitle == null) {
            currentTitle = "";
        }
        
        // Show input dialog
        String inputTitle = JOptionPane.showInputDialog(this,
            "Enter new conversation title:",
            "Rename Conversation",
            JOptionPane.PLAIN_MESSAGE);
        
        if (inputTitle == null) {
            // User cancelled
            return;
        }
        
        final String newTitle = inputTitle.trim();
        if (newTitle.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                "Title cannot be empty",
                "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        if (newTitle.length() > 200) {
            JOptionPane.showMessageDialog(this,
                "Title must be at most 200 characters",
                "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        executorService.execute(() -> {
            try {
                apiClient.updateConversationTitle(userSession.getUserId(), conversationId, newTitle);
                SwingUtilities.invokeLater(() -> {
                    conversationPanel.updateConversationTitle(conversationId, newTitle);
                });
            } catch (ApiException e) {
                SwingUtilities.invokeLater(() -> {
                    JOptionPane.showMessageDialog(this,
                        "Failed to rename conversation: " + e.getMessage(),
                        "Error", JOptionPane.ERROR_MESSAGE);
                });
            }
        });
    }
    
    private void onDeleteConversation(Long conversationId) {
        int confirm = JOptionPane.showConfirmDialog(this,
            "Are you sure you want to delete this conversation?",
            "Confirm Delete", JOptionPane.YES_NO_OPTION);
        
        if (confirm != JOptionPane.YES_OPTION) {
            return;
        }
        
        executorService.execute(() -> {
            try {
                apiClient.deleteConversation(userSession.getUserId(), conversationId);
                SwingUtilities.invokeLater(() -> {
                    conversationPanel.removeConversation(conversationId);
                    if (currentConversationId != null && currentConversationId.equals(conversationId)) {
                        currentConversationId = null;
                        messagePanel.clearMessages();
                        inputPanel.setEnabled(false);
                    }
                });
            } catch (ApiException e) {
                SwingUtilities.invokeLater(() -> {
                    JOptionPane.showMessageDialog(this,
                        "Failed to delete conversation: " + e.getMessage(),
                        "Error", JOptionPane.ERROR_MESSAGE);
                });
            }
        });
    }
    
    private void onSendMessage(String text) {
        if (currentConversationId == null || text.trim().isEmpty()) {
            return;
        }
        
        // Add user message immediately (optimistic update)
        MessageView userMessage = new MessageView();
        userMessage.setRole("user");
        userMessage.setContent(text);
        userMessage.setTs(java.time.Instant.now());
        messagePanel.addMessage(userMessage, true);
        
        inputPanel.setWaitingForResponse(true);
        
        executorService.execute(() -> {
            try {
                MessageView assistantMessage = apiClient.sendMessage(
                    currentConversationId, userSession.getUserId(), text);
                SwingUtilities.invokeLater(() -> {
                    messagePanel.addMessage(assistantMessage, false);
                    inputPanel.setWaitingForResponse(false);
                });
            } catch (ApiException e) {
                SwingUtilities.invokeLater(() -> {
                    JOptionPane.showMessageDialog(this,
                        "Failed to send message: " + e.getMessage(),
                        "Error", JOptionPane.ERROR_MESSAGE);
                    inputPanel.setWaitingForResponse(false);
                });
            }
        });
    }
    
    private void shutdown() {
        executorService.shutdown();
    }
}

