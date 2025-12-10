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
    private JTextArea contentArea;
    private JLabel timestampLabel;
    private boolean isUserMessage;
    
    public MessageBubble(String content, Instant timestamp, boolean isUserMessage) {
        this.isUserMessage = isUserMessage;
        setupUI(content, timestamp);
    }
    
    private void setupUI(String content, Instant timestamp) {
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setOpaque(false);
        
        // Use JTextArea for proper word wrapping
        contentArea = new JTextArea(content);
        contentArea.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 14));
        contentArea.setEditable(false);
        contentArea.setFocusable(false);
        contentArea.setOpaque(true);
        contentArea.setBorder(BorderFactory.createEmptyBorder(8, 12, 8, 12));
        contentArea.setLineWrap(true);
        contentArea.setWrapStyleWord(true);
        
        // Set fixed width and let JTextArea calculate the height automatically
        int maxWidth = 400;
        contentArea.setSize(maxWidth, Short.MAX_VALUE);
        Dimension preferredSize = contentArea.getPreferredSize();
        int calculatedHeight = Math.max(preferredSize.height, contentArea.getFont().getSize() + 16);
        contentArea.setPreferredSize(new Dimension(maxWidth, calculatedHeight));
        contentArea.setMaximumSize(new Dimension(maxWidth, Integer.MAX_VALUE));
        contentArea.setMinimumSize(new Dimension(100, calculatedHeight));
        
        if (isUserMessage) {
            contentArea.setBackground(new Color(0, 123, 255)); // Blue
            contentArea.setForeground(Color.WHITE);
        } else {
            contentArea.setBackground(new Color(233, 236, 239)); // Light gray
            contentArea.setForeground(Color.BLACK);
        }
        
        add(contentArea);
        
        // Timestamp label
        timestampLabel = new JLabel(formatTimestamp(timestamp));
        timestampLabel.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 10));
        timestampLabel.setForeground(new Color(108, 117, 125));
        timestampLabel.setBorder(BorderFactory.createEmptyBorder(2, 12, 0, 12));
        
        if (isUserMessage) {
            timestampLabel.setHorizontalAlignment(SwingConstants.LEFT);
        } else {
            timestampLabel.setHorizontalAlignment(SwingConstants.RIGHT);
        }
        
        add(timestampLabel);
        
        // Note: Positioning (left/right) is handled by wrapper in MessagePanel
        setMaximumSize(new Dimension(500, Integer.MAX_VALUE));
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

