package com.nyu.aichat.client.ui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.function.Consumer;

/**
 * Input panel for typing and sending messages.
 */
public class InputPanel extends JPanel {
    private JTextArea messageTextArea;
    private JButton sendButton;
    private Consumer<String> onSendMessage;
    private boolean isWaitingForResponse;
    
    public InputPanel(Consumer<String> onSendMessage) {
        this.onSendMessage = onSendMessage;
        this.isWaitingForResponse = false;
        setupUI();
    }
    
    private void setupUI() {
        setLayout(new BorderLayout(5, 5));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        // Text area
        messageTextArea = new PlaceholderTextArea(3, 30);
        messageTextArea.setLineWrap(true);
        messageTextArea.setWrapStyleWord(true);
        messageTextArea.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 14));
        
        // Handle Enter key (Shift+Enter for new line)
        messageTextArea.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER && !e.isShiftDown()) {
                    e.consume();
                    onSendClick();
                }
            }
        });
        
        JScrollPane textScrollPane = new JScrollPane(messageTextArea);
        textScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        add(textScrollPane, BorderLayout.CENTER);
        
        // Send button
        sendButton = new JButton("Send");
        sendButton.setPreferredSize(new Dimension(80, 40));
        sendButton.addActionListener(e -> onSendClick());
        add(sendButton, BorderLayout.EAST);
        
        setEnabled(false); // Disabled until conversation is selected
    }
    
    public void setEnabled(boolean enabled) {
        messageTextArea.setEnabled(enabled);
        sendButton.setEnabled(enabled && !isWaitingForResponse);
        if (!enabled) {
            messageTextArea.setText("");
            ((PlaceholderTextArea) messageTextArea).setPlaceholder("Select a conversation first");
        } else {
            ((PlaceholderTextArea) messageTextArea).setPlaceholder("Type your message...");
        }
    }
    
    public void setWaitingForResponse(boolean waiting) {
        this.isWaitingForResponse = waiting;
        sendButton.setEnabled(!waiting);
        messageTextArea.setEnabled(!waiting);
        
        if (waiting) {
            sendButton.setText("Sending...");
        } else {
            sendButton.setText("Send");
        }
    }
    
    private void onSendClick() {
        String text = messageTextArea.getText().trim();
        if (text.isEmpty() || isWaitingForResponse) {
            return;
        }
        
        if (onSendMessage != null) {
            onSendMessage.accept(text);
            messageTextArea.setText("");
        }
    }
    
    // Helper class for placeholder text (Java 8 compatible)
    private static class PlaceholderTextArea extends JTextArea {
        private String placeholder;
        
        public PlaceholderTextArea(int rows, int columns) {
            super(rows, columns);
        }
        
        public void setPlaceholder(String placeholder) {
            this.placeholder = placeholder;
            repaint();
        }
        
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            if (placeholder != null && getText().isEmpty() && !isFocusOwner()) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(Color.GRAY);
                g2.setFont(getFont().deriveFont(Font.ITALIC));
                g2.drawString(placeholder, 5, getFontMetrics(getFont()).getAscent() + 5);
            }
        }
    }
}

