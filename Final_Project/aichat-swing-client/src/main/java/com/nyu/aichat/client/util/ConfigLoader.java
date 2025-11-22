package com.nyu.aichat.client.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Loads configuration from config.properties file.
 */
public class ConfigLoader {
    private static final Properties properties = new Properties();
    private static final String DEFAULT_BASE_URL = "http://localhost:8080/api/v1";
    private static final int DEFAULT_TIMEOUT = 30000;
    
    static {
        loadProperties();
    }
    
    private static void loadProperties() {
        try (InputStream input = ConfigLoader.class.getClassLoader()
                .getResourceAsStream("config.properties")) {
            if (input != null) {
                properties.load(input);
            }
        } catch (IOException e) {
            System.err.println("Failed to load config.properties: " + e.getMessage());
        }
    }
    
    public static String getApiBaseUrl() {
        return properties.getProperty("api.baseUrl", DEFAULT_BASE_URL);
    }
    
    public static int getApiTimeout() {
        String timeoutStr = properties.getProperty("api.timeout.ms", String.valueOf(DEFAULT_TIMEOUT));
        try {
            return Integer.parseInt(timeoutStr);
        } catch (NumberFormatException e) {
            return DEFAULT_TIMEOUT;
        }
    }
    
    public static int getWindowWidth() {
        String widthStr = properties.getProperty("ui.window.width", "1200");
        try {
            return Integer.parseInt(widthStr);
        } catch (NumberFormatException e) {
            return 1200;
        }
    }
    
    public static int getWindowHeight() {
        String heightStr = properties.getProperty("ui.window.height", "800");
        try {
            return Integer.parseInt(heightStr);
        } catch (NumberFormatException e) {
            return 800;
        }
    }
    
    public static int getConversationPanelWidth() {
        String widthStr = properties.getProperty("ui.conversation.panel.width", "250");
        try {
            return Integer.parseInt(widthStr);
        } catch (NumberFormatException e) {
            return 250;
        }
    }
}

