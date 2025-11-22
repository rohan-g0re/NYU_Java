package com.nyu.aichat.client.ui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.function.Consumer;

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
        messageTextArea = new JTextArea(3, 30);
        messageTextArea.setLineWrap(true);
        messageTextArea.setWrapStyleWord(true);
        messageTextArea.setMaximumSize(new Dimension(Integer.MAX_VALUE, 100));
        
        JScrollPane scrollPane = new JScrollPane(messageTextArea);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        
        // Send button
        sendButton = new JButton("Send");
        sendButton.addActionListener(e -> onSendClick());
        
        // Panel for button
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.add(sendButton);
        
        add(scrollPane, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.EAST);
        
        // Enter key support (Ctrl+Enter to send)
        messageTextArea.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                if (evt.isControlDown() && evt.getKeyCode() == java.awt.event.KeyEvent.VK_ENTER) {
                    onSendClick();
                }
            }
        });
    }
    
    private void onSendClick() {
        String text = messageTextArea.getText().trim();
        
        if (text.isEmpty()) {
            return;
        }
        
        if (isWaitingForResponse) {
            // Allow sending multiple messages
            // Just proceed
        }
        
        if (onSendMessage != null) {
            onSendMessage.accept(text);
            messageTextArea.setText("");
            setWaitingForResponse(true);
        }
    }
    
    private void setWaitingForResponse(boolean waiting) {
        this.isWaitingForResponse = waiting;
        sendButton.setEnabled(!waiting);
        if (waiting) {
            sendButton.setText("Sending...");
        } else {
            sendButton.setText("Send");
        }
    }
}

