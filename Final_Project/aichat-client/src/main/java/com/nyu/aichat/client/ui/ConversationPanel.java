package com.nyu.aichat.client.ui;

import com.nyu.aichat.client.model.ConversationView;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import java.util.function.Consumer;

public class ConversationPanel extends JPanel {
    private JList<ConversationView> conversationList;
    private DefaultListModel<ConversationView> listModel;
    private JButton newChatButton;
    private Consumer<Long> onConversationSelected;
    private Runnable onCreateNewChatCallback;
    
    public ConversationPanel(Consumer<Long> onConversationSelected) {
        this.onConversationSelected = onConversationSelected;
        this.listModel = new DefaultListModel<>();
        setupUI();
    }
    
    public void setOnCreateNewChatCallback(Runnable callback) {
        this.onCreateNewChatCallback = callback;
    }
    
    private void setupUI() {
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createTitledBorder("Conversations"));
        
        // New chat button
        newChatButton = new JButton("+ New Chat");
        newChatButton.addActionListener(e -> onCreateNewChat());
        add(newChatButton, BorderLayout.NORTH);
        
        // Conversation list
        conversationList = new JList<>(listModel);
        conversationList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        conversationList.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                ConversationView selected = conversationList.getSelectedValue();
                if (selected != null && onConversationSelected != null) {
                    onConversationSelected.accept(selected.getId());
                }
            }
        });
        
        JScrollPane scrollPane = new JScrollPane(conversationList);
        add(scrollPane, BorderLayout.CENTER);
        
        // Empty state
        if (listModel.isEmpty()) {
            JLabel emptyLabel = new JLabel("No conversations yet. Click + to start.");
            emptyLabel.setHorizontalAlignment(SwingConstants.CENTER);
            add(emptyLabel, BorderLayout.SOUTH);
        }
    }
    
    private void onCreateNewChat() {
        String title = JOptionPane.showInputDialog(this, "Enter conversation title:", "New Chat");
        if (title != null && !title.trim().isEmpty()) {
            if (onCreateNewChatCallback != null) {
                onCreateNewChatCallback.run();
            }
        } else if (title != null) {
            // Empty title - use default
            if (onCreateNewChatCallback != null) {
                onCreateNewChatCallback.run();
            }
        }
    }
    
    public void setConversations(List<ConversationView> conversations) {
        listModel.clear();
        for (ConversationView conv : conversations) {
            listModel.addElement(conv);
        }
    }
    
    public void addConversation(ConversationView conversation) {
        listModel.addElement(conversation);
    }
    
    public void removeConversation(Long conversationId) {
        for (int i = 0; i < listModel.size(); i++) {
            if (listModel.get(i).getId().equals(conversationId)) {
                listModel.remove(i);
                break;
            }
        }
    }
}

