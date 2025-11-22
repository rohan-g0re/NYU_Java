package com.nyu.aichat.client.ui;

import com.nyu.aichat.client.model.ConversationView;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import java.util.function.Consumer;

/**
 * Left sidebar panel showing list of conversations.
 */
public class ConversationPanel extends JPanel {
    private JList<ConversationView> conversationList;
    private DefaultListModel<ConversationView> listModel;
    private JButton newChatButton;
    private Consumer<Long> onConversationSelected;
    private Runnable onNewChat;
    private Consumer<Long> onDeleteConversation;
    
    public ConversationPanel(Consumer<Long> onConversationSelected,
                            Runnable onNewChat,
                            Consumer<Long> onDeleteConversation) {
        this.onConversationSelected = onConversationSelected;
        this.onNewChat = onNewChat;
        this.onDeleteConversation = onDeleteConversation;
        
        setupUI();
    }
    
    private void setupUI() {
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        // New Chat button
        newChatButton = new JButton("+ New Chat");
        newChatButton.addActionListener(e -> {
            if (onNewChat != null) {
                onNewChat.run();
            }
        });
        add(newChatButton, BorderLayout.NORTH);
        
        // Conversation list
        listModel = new DefaultListModel<>();
        conversationList = new JList<>(listModel);
        conversationList.setCellRenderer(new ConversationListCellRenderer());
        conversationList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        
        conversationList.addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                if (!e.getValueIsAdjusting()) {
                    ConversationView selected = conversationList.getSelectedValue();
                    if (selected != null && onConversationSelected != null) {
                        onConversationSelected.accept(selected.getId());
                    }
                }
            }
        });
        
        // Add right-click menu for delete
        JPopupMenu popupMenu = new JPopupMenu();
        JMenuItem deleteItem = new JMenuItem("Delete");
        deleteItem.addActionListener(e -> {
            ConversationView selected = conversationList.getSelectedValue();
            if (selected != null && onDeleteConversation != null) {
                onDeleteConversation.accept(selected.getId());
            }
        });
        popupMenu.add(deleteItem);
        conversationList.setComponentPopupMenu(popupMenu);
        
        JScrollPane scrollPane = new JScrollPane(conversationList);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        add(scrollPane, BorderLayout.CENTER);
    }
    
    public void setConversations(List<ConversationView> conversations) {
        listModel.clear();
        for (ConversationView conv : conversations) {
            listModel.addElement(conv);
        }
    }
    
    public void addConversation(ConversationView conversation) {
        listModel.insertElementAt(conversation, 0);
        conversationList.setSelectedIndex(0);
    }
    
    public void removeConversation(Long conversationId) {
        for (int i = 0; i < listModel.getSize(); i++) {
            ConversationView conv = listModel.getElementAt(i);
            if (conv.getId().equals(conversationId)) {
                listModel.removeElementAt(i);
                break;
            }
        }
    }
    
    /**
     * Custom cell renderer for conversation list items.
     */
    private static class ConversationListCellRenderer extends DefaultListCellRenderer {
        @Override
        public Component getListCellRendererComponent(JList<?> list, Object value,
                                                      int index, boolean isSelected,
                                                      boolean cellHasFocus) {
            super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
            
            if (value instanceof ConversationView) {
                ConversationView conv = (ConversationView) value;
                setText("<html><b>" + conv.getTitle() + "</b><br><small>" + 
                       conv.getFormattedDate() + "</small></html>");
            }
            
            return this;
        }
    }
}

