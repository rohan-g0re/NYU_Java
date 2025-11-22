package com.nyu.aichat.client.ui;

import javax.swing.*;
import java.awt.*;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

/**
 * Individual message bubble component.
 * Displays message content and timestamp.
 */
public class MessageBubble extends JPanel {
    private JLabel contentLabel;
    private JLabel timestampLabel;
    private boolean isUserMessage;
    
    public MessageBubble(String content, Instant timestamp, boolean isUserMessage) {
        this.isUserMessage = isUserMessage;
        setupUI(content, timestamp);
    }
    
    private void setupUI(String content, Instant timestamp) {
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setOpaque(false);
        
        // Content label
        contentLabel = new JLabel("<html><div style='width: 400px;'>" + 
                                 escapeHtml(content) + "</div></html>");
        contentLabel.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 14));
        contentLabel.setOpaque(true);
        contentLabel.setBorder(BorderFactory.createEmptyBorder(8, 12, 8, 12));
        
        if (isUserMessage) {
            contentLabel.setBackground(new Color(0, 123, 255)); // Blue
            contentLabel.setForeground(Color.WHITE);
            contentLabel.setHorizontalAlignment(SwingConstants.RIGHT);
        } else {
            contentLabel.setBackground(new Color(233, 236, 239)); // Light gray
            contentLabel.setForeground(Color.BLACK);
            contentLabel.setHorizontalAlignment(SwingConstants.LEFT);
        }
        
        add(contentLabel);
        
        // Timestamp label
        timestampLabel = new JLabel(formatTimestamp(timestamp));
        timestampLabel.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 10));
        timestampLabel.setForeground(new Color(108, 117, 125));
        timestampLabel.setBorder(BorderFactory.createEmptyBorder(2, 12, 0, 12));
        
        if (isUserMessage) {
            timestampLabel.setHorizontalAlignment(SwingConstants.RIGHT);
        } else {
            timestampLabel.setHorizontalAlignment(SwingConstants.LEFT);
        }
        
        add(timestampLabel);
        
        // Set alignment
        setAlignmentX(isUserMessage ? Component.RIGHT_ALIGNMENT : Component.LEFT_ALIGNMENT);
        setMaximumSize(new Dimension(500, Integer.MAX_VALUE));
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
        LocalDateTime dateTime = LocalDateTime.ofInstant(timestamp, ZoneId.systemDefault());
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("h:mm a");
        return dateTime.format(formatter);
    }
}

