package com.nyu.aichat.client.ui;

import javax.swing.*;
import java.awt.*;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

public class MessageBubble extends JPanel {
    private JLabel contentLabel;
    private JLabel timestampLabel;
    private boolean isUserMessage;
    private static final DateTimeFormatter TIME_FORMATTER = 
            DateTimeFormatter.ofPattern("HH:mm").withZone(ZoneId.systemDefault());
    private static final int MAX_WIDTH = 400;
    
    public MessageBubble(String content, Instant timestamp, boolean isUserMessage) {
        this.isUserMessage = isUserMessage;
        setupUI(content, timestamp);
    }
    
    private void setupUI(String content, Instant timestamp) {
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setAlignmentX(isUserMessage ? Component.RIGHT_ALIGNMENT : Component.LEFT_ALIGNMENT);
        setOpaque(false);
        
        // Content label
        contentLabel = new JLabel("<html><div style='width:" + MAX_WIDTH + "px;'>" + 
                escapeHtml(content) + "</div></html>");
        contentLabel.setOpaque(true);
        contentLabel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.GRAY, 1),
                BorderFactory.createEmptyBorder(8, 12, 8, 12)));
        
        if (isUserMessage) {
            contentLabel.setBackground(new Color(0, 123, 255)); // Blue
            contentLabel.setForeground(Color.WHITE);
        } else {
            contentLabel.setBackground(new Color(240, 240, 240)); // Gray
            contentLabel.setForeground(Color.BLACK);
        }
        
        // Timestamp label
        timestampLabel = new JLabel(formatTimestamp(timestamp));
        timestampLabel.setFont(timestampLabel.getFont().deriveFont(Font.PLAIN, 10f));
        timestampLabel.setForeground(Color.GRAY);
        timestampLabel.setAlignmentX(isUserMessage ? Component.RIGHT_ALIGNMENT : Component.LEFT_ALIGNMENT);
        
        add(contentLabel);
        add(Box.createVerticalStrut(2));
        add(timestampLabel);
        add(Box.createVerticalStrut(5));
    }
    
    private String escapeHtml(String text) {
        return text.replace("&", "&amp;")
                  .replace("<", "&lt;")
                  .replace(">", "&gt;")
                  .replace("\n", "<br>");
    }
    
    private String formatTimestamp(Instant timestamp) {
        if (timestamp == null) {
            return "";
        }
        return TIME_FORMATTER.format(timestamp);
    }
}

