# Java Swing Client - Complete Implementation Plan

## Table of Contents
1. [Project Structure](#project-structure)
2. [Dependencies & Configuration](#dependencies)
3. [Model Classes](#model-classes)
4. [API Client Implementation](#api-client)
5. [UI Components](#ui-components)
6. [Utility Classes](#utility-classes)
7. [Main Entry Point](#main-entry)
8. [Implementation Checklist](#checklist)

---

## Project Structure

```
aichat-swing-client/
├── pom.xml
├── README.md
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/
│   │   │       └── nyu/
│   │   │           └── aichat/
│   │   │               └── client/
│   │   │                   ├── Main.java
│   │   │                   ├── api/
│   │   │                   │   ├── ApiClient.java
│   │   │                   │   └── ApiException.java
│   │   │                   ├── model/
│   │   │                   │   ├── UserSession.java
│   │   │                   │   ├── ConversationView.java
│   │   │                   │   └── MessageView.java
│   │   │                   ├── ui/
│   │   │                   │   ├── LoginFrame.java
│   │   │                   │   ├── MainChatFrame.java
│   │   │                   │   ├── ConversationPanel.java
│   │   │                   │   ├── MessagePanel.java
│   │   │                   │   ├── MessageBubble.java
│   │   │                   │   └── InputPanel.java
│   │   │                   └── util/
│   │   │                       ├── JsonParser.java
│   │   │                       └── ConfigLoader.java
│   │   └── resources/
│   │       └── config.properties
│   └── test/
│       └── java/
│           └── com/nyu/aichat/client/
│               └── api/ApiClientTest.java
```

---

## Dependencies & Configuration

### pom.xml

```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 
         http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>

    <groupId>com.nyu</groupId>
    <artifactId>aichat-swing-client</artifactId>
    <version>1.0.0</version>
    <packaging>jar</packaging>
    <name>AI Chat Swing Client</name>
    <description>Java Swing desktop client for AI Chat application</description>

    <properties>
        <java.version>1.8</java.version>
        <maven.compiler.source>1.8</maven.compiler.source>
        <maven.compiler.target>1.8</maven.compiler.target>
        <project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>
        <gson.version>2.10.1</gson.version>
    </properties>

    <dependencies>
        <!-- Gson for JSON parsing -->
        <dependency>
            <groupId>com.google.code.gson</groupId>
            <artifactId>gson</artifactId>
            <version>${gson.version}</version>
        </dependency>

        <!-- JUnit for testing -->
        <dependency>
            <groupId>junit</groupId>
            <artifactId>junit</artifactId>
            <version>4.13.2</version>
            <scope>test</scope>
        </dependency>
    </dependencies>

    <build>
        <plugins>
            <plugin>
                <groupId>org.apache.maven.plugins</groupId>
                <artifactId>maven-compiler-plugin</artifactId>
                <version>3.8.1</version>
                <configuration>
                    <source>1.8</source>
                    <target>1.8</target>
                </configuration>
            </plugin>
            
            <!-- Create executable JAR -->
            <plugin>
                <groupId>org.apache.maven.plugins</groupId>
                <artifactId>maven-jar-plugin</artifactId>
                <version>3.2.0</version>
                <configuration>
                    <archive>
                        <manifest>
                            <mainClass>com.nyu.aichat.client.Main</mainClass>
                        </manifest>
                    </archive>
                </configuration>
            </plugin>
        </plugins>
    </build>
</project>
```

### config.properties

```properties
# API Configuration
api.baseUrl=http://localhost:8080/api/v1
api.timeout.ms=30000

# UI Configuration
ui.window.width=1200
ui.window.height=800
ui.conversation.panel.width=250
```

---

## Model Classes

### UserSession.java

**File:** `src/main/java/com/nyu/aichat/client/model/UserSession.java`

```java
package com.nyu.aichat.client.model;

/**
 * Represents the current user session after successful login/signup.
 * Immutable class storing userId and username.
 */
public class UserSession {
    private final Long userId;
    private final String username;
    
    public UserSession(Long userId, String username) {
        if (userId == null) {
            throw new IllegalArgumentException("userId cannot be null");
        }
        if (username == null || username.trim().isEmpty()) {
            throw new IllegalArgumentException("username cannot be null or empty");
        }
        this.userId = userId;
        this.username = username;
    }
    
    public Long getUserId() {
        return userId;
    }
    
    public String getUsername() {
        return username;
    }
    
    @Override
    public String toString() {
        return "UserSession{userId=" + userId + ", username='" + username + "'}";
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UserSession that = (UserSession) o;
        return userId.equals(that.userId) && username.equals(that.username);
    }
    
    @Override
    public int hashCode() {
        return userId.hashCode() * 31 + username.hashCode();
    }
}
```

### ConversationView.java

**File:** `src/main/java/com/nyu/aichat/client/model/ConversationView.java`

```java
package com.nyu.aichat.client.model;

import java.time.Instant;

/**
 * Represents a conversation in the UI.
 * Maps from backend ConversationDto.
 */
public class ConversationView {
    private Long id;
    private String title;
    private Instant createdAt;
    
    public ConversationView() {
    }
    
    public ConversationView(Long id, String title, Instant createdAt) {
        this.id = id;
        this.title = title;
        this.createdAt = createdAt;
    }
    
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public String getTitle() {
        return title;
    }
    
    public void setTitle(String title) {
        this.title = title;
    }
    
    public Instant getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }
    
    /**
     * Formats the creation date for display in the UI.
     * @return Formatted date string (e.g., "2h ago", "Yesterday", "Jan 15")
     */
    public String getFormattedDate() {
        if (createdAt == null) {
            return "";
        }
        
        Instant now = Instant.now();
        long diffSeconds = now.getEpochSecond() - createdAt.getEpochSecond();
        
        if (diffSeconds < 60) {
            return "Just now";
        } else if (diffSeconds < 3600) {
            long minutes = diffSeconds / 60;
            return minutes + "m ago";
        } else if (diffSeconds < 86400) {
            long hours = diffSeconds / 3600;
            return hours + "h ago";
        } else if (diffSeconds < 604800) {
            long days = diffSeconds / 86400;
            return days + "d ago";
        } else {
            // Format as date: "Jan 15" or "2024-01-15"
            return createdAt.toString().substring(0, 10); // Simple format
        }
    }
    
    @Override
    public String toString() {
        return title != null ? title : "Untitled";
    }
}
```

### MessageView.java

**File:** `src/main/java/com/nyu/aichat/client/model/MessageView.java`

```java
package com.nyu.aichat.client.model;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

/**
 * Represents a message in the chat.
 * Maps from backend MessageDto.
 */
public class MessageView {
    private Long id;
    private String role;  // "user" or "assistant"
    private String content;
    private Instant ts;
    
    public MessageView() {
    }
    
    public MessageView(Long id, String role, String content, Instant ts) {
        this.id = id;
        this.role = role;
        this.content = content;
        this.ts = ts;
    }
    
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public String getRole() {
        return role;
    }
    
    public void setRole(String role) {
        this.role = role;
    }
    
    public String getContent() {
        return content;
    }
    
    public void setContent(String content) {
        this.content = content;
    }
    
    public Instant getTs() {
        return ts;
    }
    
    public void setTs(Instant ts) {
        this.ts = ts;
    }
    
    /**
     * Checks if this is a user message.
     */
    public boolean isUserMessage() {
        return "user".equals(role);
    }
    
    /**
     * Checks if this is an assistant message.
     */
    public boolean isAssistantMessage() {
        return "assistant".equals(role);
    }
    
    /**
     * Formats the timestamp for display.
     * @return Formatted time string (e.g., "10:30 AM")
     */
    public String getFormattedTimestamp() {
        if (ts == null) {
            return "";
        }
        
        LocalDateTime dateTime = LocalDateTime.ofInstant(ts, ZoneId.systemDefault());
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("h:mm a");
        return dateTime.format(formatter);
    }
    
    @Override
    public String toString() {
        return "MessageView{id=" + id + ", role='" + role + "', content='" + 
               (content != null && content.length() > 50 ? content.substring(0, 50) + "..." : content) + "'}";
    }
}
```

---

## API Client Implementation

### ApiException.java

**File:** `src/main/java/com/nyu/aichat/client/api/ApiException.java`

```java
package com.nyu.aichat.client.api;

/**
 * Custom exception for API errors.
 * Contains error code and message from backend ErrorResponse.
 */
public class ApiException extends Exception {
    private final String errorCode;
    private final int httpStatus;
    
    public ApiException(String errorCode, String message, int httpStatus) {
        super(message);
        this.errorCode = errorCode;
        this.httpStatus = httpStatus;
    }
    
    public ApiException(String errorCode, String message) {
        this(errorCode, message, 0);
    }
    
    public String getErrorCode() {
        return errorCode;
    }
    
    public int getHttpStatus() {
        return httpStatus;
    }
    
    @Override
    public String toString() {
        return "ApiException{errorCode='" + errorCode + "', message='" + getMessage() + 
               "', httpStatus=" + httpStatus + "}";
    }
}
```

### ApiClient.java

**File:** `src/main/java/com/nyu/aichat/client/api/ApiClient.java`

```java
package com.nyu.aichat.client.api;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializer;
import com.google.gson.reflect.TypeToken;
import com.nyu.aichat.client.model.ConversationView;
import com.nyu.aichat.client.model.MessageView;
import com.nyu.aichat.client.util.ConfigLoader;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * HTTP client for communicating with Spring Boot REST API.
 * Uses HttpURLConnection for HTTP requests.
 * JSON serialization/deserialization via Gson.
 */
public class ApiClient {
    private static final String DEFAULT_BASE_URL = "http://localhost:8080/api/v1";
    private final String baseUrl;
    private final ExecutorService executorService;
    private final Gson gson;
    
    /**
     * Constructor using default base URL from config.properties.
     */
    public ApiClient() {
        this(ConfigLoader.getApiBaseUrl());
    }
    
    /**
     * Constructor with custom base URL.
     */
    public ApiClient(String baseUrl) {
        this.baseUrl = baseUrl != null ? baseUrl : DEFAULT_BASE_URL;
        this.executorService = Executors.newCachedThreadPool();
        
        // Configure Gson with Instant deserializer
        GsonBuilder builder = new GsonBuilder();
        builder.registerTypeAdapter(Instant.class, (JsonDeserializer<Instant>) (json, type, context) -> {
            String dateStr = json.getAsString();
            return Instant.parse(dateStr);
        });
        this.gson = builder.create();
    }
    
    /**
     * Login endpoint.
     * POST /api/v1/auth/login
     */
    public LoginResponse login(String username, String password) throws ApiException {
        Map<String, String> body = new HashMap<>();
        body.put("username", username);
        body.put("password", password);
        
        String response = sendPostRequest("/auth/login", null, body);
        return gson.fromJson(response, LoginResponse.class);
    }
    
    /**
     * Signup endpoint.
     * POST /api/v1/auth/signup
     */
    public LoginResponse signup(String username, String password) throws ApiException {
        Map<String, String> body = new HashMap<>();
        body.put("username", username);
        body.put("password", password);
        
        String response = sendPostRequest("/auth/signup", null, body);
        return gson.fromJson(response, LoginResponse.class);
    }
    
    /**
     * Get all conversations for a user.
     * GET /api/v1/conversations
     */
    public List<ConversationView> getConversations(Long userId) throws ApiException {
        String response = sendGetRequest("/conversations", userId);
        return gson.fromJson(response, new TypeToken<List<ConversationView>>(){}.getType());
    }
    
    /**
     * Create a new conversation.
     * POST /api/v1/conversations
     */
    public ConversationView createConversation(Long userId, String title) throws ApiException {
        Map<String, String> body = new HashMap<>();
        if (title != null && !title.trim().isEmpty()) {
            body.put("title", title);
        }
        
        String response = sendPostRequest("/conversations", userId, body);
        return gson.fromJson(response, ConversationView.class);
    }
    
    /**
     * Update conversation title.
     * PUT /api/v1/conversations/{id}/title
     */
    public void updateConversationTitle(Long userId, Long conversationId, String title) throws ApiException {
        Map<String, String> body = new HashMap<>();
        body.put("title", title);
        
        sendPutRequest("/conversations/" + conversationId + "/title", userId, body);
    }
    
    /**
     * Delete a conversation (soft delete).
     * DELETE /api/v1/conversations/{id}
     */
    public void deleteConversation(Long userId, Long conversationId) throws ApiException {
        sendDeleteRequest("/conversations/" + conversationId, userId);
    }
    
    /**
     * Get all messages for a conversation.
     * GET /api/v1/conversations/{id}/messages
     */
    public List<MessageView> getMessages(Long conversationId, Long userId) throws ApiException {
        String response = sendGetRequest("/conversations/" + conversationId + "/messages", userId);
        return gson.fromJson(response, new TypeToken<List<MessageView>>(){}.getType());
    }
    
    /**
     * Send a message and get AI response.
     * POST /api/v1/conversations/{id}/messages
     * Returns the assistant's response message.
     */
    public MessageView sendMessage(Long conversationId, Long userId, String text) throws ApiException {
        Map<String, String> body = new HashMap<>();
        body.put("text", text);
        
        String response = sendPostRequest("/conversations/" + conversationId + "/messages", userId, body);
        
        // Parse SendMessageResponse wrapper
        Map<String, Object> responseMap = gson.fromJson(response, Map.class);
        Map<String, Object> assistantMessageMap = (Map<String, Object>) responseMap.get("assistantMessage");
        
        // Convert to MessageView
        return gson.fromJson(gson.toJson(assistantMessageMap), MessageView.class);
    }
    
    // ========== HTTP Helper Methods ==========
    
    private String sendGetRequest(String endpoint, Long userId) throws ApiException {
        try {
            HttpURLConnection conn = createConnection(endpoint, "GET", userId);
            
            int responseCode = conn.getResponseCode();
            if (responseCode >= 200 && responseCode < 300) {
                return readResponse(conn);
            } else {
                throw parseErrorResponse(conn, responseCode);
            }
        } catch (IOException e) {
            throw new ApiException("NETWORK_ERROR", "Failed to connect to server: " + e.getMessage());
        }
    }
    
    private String sendPostRequest(String endpoint, Long userId, Object body) throws ApiException {
        try {
            HttpURLConnection conn = createConnection(endpoint, "POST", userId);
            conn.setDoOutput(true);
            
            // Write request body
            try (OutputStream os = conn.getOutputStream();
                 Writer writer = new OutputStreamWriter(os, StandardCharsets.UTF_8)) {
                gson.toJson(body, writer);
            }
            
            int responseCode = conn.getResponseCode();
            if (responseCode >= 200 && responseCode < 300) {
                return readResponse(conn);
            } else {
                throw parseErrorResponse(conn, responseCode);
            }
        } catch (IOException e) {
            throw new ApiException("NETWORK_ERROR", "Failed to connect to server: " + e.getMessage());
        }
    }
    
    private String sendPutRequest(String endpoint, Long userId, Object body) throws ApiException {
        try {
            HttpURLConnection conn = createConnection(endpoint, "PUT", userId);
            conn.setDoOutput(true);
            
            // Write request body
            try (OutputStream os = conn.getOutputStream();
                 Writer writer = new OutputStreamWriter(os, StandardCharsets.UTF_8)) {
                gson.toJson(body, writer);
            }
            
            int responseCode = conn.getResponseCode();
            if (responseCode >= 200 && responseCode < 300) {
                return readResponse(conn);
            } else {
                throw parseErrorResponse(conn, responseCode);
            }
        } catch (IOException e) {
            throw new ApiException("NETWORK_ERROR", "Failed to connect to server: " + e.getMessage());
        }
    }
    
    private String sendDeleteRequest(String endpoint, Long userId) throws ApiException {
        try {
            HttpURLConnection conn = createConnection(endpoint, "DELETE", userId);
            
            int responseCode = conn.getResponseCode();
            if (responseCode >= 200 && responseCode < 300) {
                // DELETE may return empty body
                try {
                    return readResponse(conn);
                } catch (Exception e) {
                    return "{}"; // Empty response is OK for DELETE
                }
            } else {
                throw parseErrorResponse(conn, responseCode);
            }
        } catch (IOException e) {
            throw new ApiException("NETWORK_ERROR", "Failed to connect to server: " + e.getMessage());
        }
    }
    
    private HttpURLConnection createConnection(String endpoint, String method, Long userId) throws IOException {
        URL url = new URL(baseUrl + endpoint);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod(method);
        conn.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
        conn.setRequestProperty("Accept", "application/json");
        
        // Set X-User-Id header for authenticated endpoints
        if (userId != null) {
            conn.setRequestProperty("X-User-Id", userId.toString());
        }
        
        // Set timeout
        int timeout = ConfigLoader.getApiTimeout();
        conn.setConnectTimeout(timeout);
        conn.setReadTimeout(timeout);
        
        return conn;
    }
    
    private String readResponse(HttpURLConnection conn) throws IOException {
        StringBuilder response = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(conn.getInputStream(), StandardCharsets.UTF_8))) {
            String line;
            while ((line = reader.readLine()) != null) {
                response.append(line);
            }
        }
        return response.toString();
    }
    
    private ApiException parseErrorResponse(HttpURLConnection conn, int responseCode) throws IOException {
        String errorBody = "";
        try {
            errorBody = readResponse(conn);
        } catch (Exception e) {
            // Ignore if error stream is empty
        }
        
        String errorCode = "UNKNOWN_ERROR";
        String message = "An error occurred";
        
        if (!errorBody.isEmpty()) {
            try {
                Map<String, String> errorMap = gson.fromJson(errorBody, Map.class);
                errorCode = errorMap.getOrDefault("error", errorCode);
                message = errorMap.getOrDefault("message", message);
            } catch (Exception e) {
                message = errorBody;
            }
        }
        
        return new ApiException(errorCode, message, responseCode);
    }
    
    /**
     * Shutdown executor service (call on application exit).
     */
    public void shutdown() {
        executorService.shutdown();
    }
    
    // Inner class for LoginResponse (matches backend DTO)
    public static class LoginResponse {
        private Long userId;
        private String username;
        
        public Long getUserId() {
            return userId;
        }
        
        public void setUserId(Long userId) {
            this.userId = userId;
        }
        
        public String getUsername() {
            return username;
        }
        
        public void setUsername(String username) {
            this.username = username;
        }
    }
}
```

---

## Utility Classes

### ConfigLoader.java

**File:** `src/main/java/com/nyu/aichat/client/util/ConfigLoader.java`

```java
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
```

### JsonParser.java

**File:** `src/main/java/com/nyu/aichat/client/util/JsonParser.java`

```java
package com.nyu.aichat.client.util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializer;
import com.google.gson.reflect.TypeToken;
import com.nyu.aichat.client.model.ConversationView;
import com.nyu.aichat.client.model.MessageView;

import java.time.Instant;
import java.util.List;

/**
 * Utility class for JSON parsing using Gson.
 * Provides convenience methods for common parsing operations.
 */
public class JsonParser {
    private static final Gson gson;
    
    static {
        GsonBuilder builder = new GsonBuilder();
        // Register Instant deserializer
        builder.registerTypeAdapter(Instant.class, (JsonDeserializer<Instant>) (json, type, context) -> {
            String dateStr = json.getAsString();
            return Instant.parse(dateStr);
        });
        gson = builder.create();
    }
    
    /**
     * Parse JSON string to ConversationView list.
     */
    public static List<ConversationView> parseConversations(String json) {
        return gson.fromJson(json, new TypeToken<List<ConversationView>>(){}.getType());
    }
    
    /**
     * Parse JSON string to MessageView list.
     */
    public static List<MessageView> parseMessages(String json) {
        return gson.fromJson(json, new TypeToken<List<MessageView>>(){}.getType());
    }
    
    /**
     * Parse JSON string to object of given class.
     */
    public static <T> T fromJson(String json, Class<T> clazz) {
        return gson.fromJson(json, clazz);
    }
    
    /**
     * Convert object to JSON string.
     */
    public static String toJson(Object obj) {
        return gson.toJson(obj);
    }
    
    /**
     * Get Gson instance for advanced usage.
     */
    public static Gson getGson() {
        return gson;
    }
}
```

---

## UI Components

### LoginFrame.java

**File:** `src/main/java/com/nyu/aichat/client/ui/LoginFrame.java`

```java
package com.nyu.aichat.client.ui;

import com.nyu.aichat.client.api.ApiClient;
import com.nyu.aichat.client.api.ApiException;
import com.nyu.aichat.client.model.UserSession;
import com.nyu.aichat.client.util.ConfigLoader;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Login/Signup window.
 * Handles user authentication and opens MainChatFrame on success.
 */
public class LoginFrame extends JFrame {
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JButton loginButton;
    private JCheckBox signupCheckbox;
    private JLabel errorLabel;
    private ApiClient apiClient;
    private ExecutorService executorService;
    
    public LoginFrame() {
        this.apiClient = new ApiClient();
        this.executorService = Executors.newCachedThreadPool();
        setupUI();
    }
    
    private void setupUI() {
        setTitle("AI Chat - Login");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);
        
        // Main panel with padding
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        // Title
        JLabel titleLabel = new JLabel("AI Chat", SwingConstants.CENTER);
        titleLabel.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 24));
        mainPanel.add(titleLabel, BorderLayout.NORTH);
        
        // Form panel
        JPanel formPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
        // Username field
        gbc.gridx = 0;
        gbc.gridy = 0;
        formPanel.add(new JLabel("Username:"), gbc);
        gbc.gridx = 1;
        usernameField = new JTextField(20);
        formPanel.add(usernameField, gbc);
        
        // Password field
        gbc.gridx = 0;
        gbc.gridy = 1;
        formPanel.add(new JLabel("Password:"), gbc);
        gbc.gridx = 1;
        passwordField = new JPasswordField(20);
        formPanel.add(passwordField, gbc);
        
        // Signup checkbox
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 2;
        signupCheckbox = new JCheckBox("New user? Sign up");
        formPanel.add(signupCheckbox, gbc);
        
        // Login/Signup button
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.NONE;
        loginButton = new JButton("Login");
        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (signupCheckbox.isSelected()) {
                    onSignupClick();
                } else {
                    onLoginClick();
                }
            }
        });
        formPanel.add(loginButton, gbc);
        
        // Error label
        gbc.gridy = 4;
        errorLabel = new JLabel(" ");
        errorLabel.setForeground(Color.RED);
        errorLabel.setHorizontalAlignment(SwingConstants.CENTER);
        formPanel.add(errorLabel, gbc);
        
        mainPanel.add(formPanel, BorderLayout.CENTER);
        
        // Update button text when checkbox changes
        signupCheckbox.addActionListener(e -> {
            loginButton.setText(signupCheckbox.isSelected() ? "Sign Up" : "Login");
        });
        
        add(mainPanel);
        pack();
        setLocationRelativeTo(null);
    }
    
    private void onLoginClick() {
        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword());
        
        if (username.isEmpty() || password.isEmpty()) {
            showError("Please enter username and password");
            return;
        }
        
        loginButton.setEnabled(false);
        errorLabel.setText("Logging in...");
        
        executorService.execute(() -> {
            try {
                ApiClient.LoginResponse response = apiClient.login(username, password);
                SwingUtilities.invokeLater(() -> {
                    handleAuthSuccess(response);
                });
            } catch (ApiException e) {
                SwingUtilities.invokeLater(() -> {
                    handleAuthError(e.getMessage());
                });
            } finally {
                SwingUtilities.invokeLater(() -> {
                    loginButton.setEnabled(true);
                });
            }
        });
    }
    
    private void onSignupClick() {
        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword());
        
        if (username.isEmpty() || password.isEmpty()) {
            showError("Please enter username and password");
            return;
        }
        
        if (username.length() < 3 || username.length() > 20) {
            showError("Username must be between 3 and 20 characters");
            return;
        }
        
        if (!username.matches("^[a-zA-Z0-9_]+$")) {
            showError("Username can only contain letters, digits, and underscores");
            return;
        }
        
        if (password.length() < 6) {
            showError("Password must be at least 6 characters");
            return;
        }
        
        loginButton.setEnabled(false);
        errorLabel.setText("Signing up...");
        
        executorService.execute(() -> {
            try {
                ApiClient.LoginResponse response = apiClient.signup(username, password);
                SwingUtilities.invokeLater(() -> {
                    handleAuthSuccess(response);
                });
            } catch (ApiException e) {
                SwingUtilities.invokeLater(() -> {
                    handleAuthError(e.getMessage());
                });
            } finally {
                SwingUtilities.invokeLater(() -> {
                    loginButton.setEnabled(true);
                });
            }
        });
    }
    
    private void handleAuthSuccess(ApiClient.LoginResponse response) {
        UserSession session = new UserSession(response.getUserId(), response.getUsername());
        
        // Open main chat frame
        SwingUtilities.invokeLater(() -> {
            MainChatFrame mainFrame = new MainChatFrame(session, apiClient);
            mainFrame.setVisible(true);
            dispose(); // Close login window
        });
    }
    
    private void handleAuthError(String errorMessage) {
        showError(errorMessage);
    }
    
    private void showError(String message) {
        errorLabel.setText(message);
    }
    
    @Override
    public void dispose() {
        super.dispose();
        executorService.shutdown();
    }
}
```

### MainChatFrame.java

**File:** `src/main/java/com/nyu/aichat/client/ui/MainChatFrame.java`

```java
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
            this::onDeleteConversation
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
```

### ConversationPanel.java

**File:** `src/main/java/com/nyu/aichat/client/ui/ConversationPanel.java`

```java
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
```

### MessagePanel.java

**File:** `src/main/java/com/nyu/aichat/client/ui/MessagePanel.java`

```java
package com.nyu.aichat.client.ui;

import com.nyu.aichat.client.model.MessageView;

import javax.swing.*;
import java.awt.*;
import java.util.List;

/**
 * Scrollable panel displaying chat messages.
 */
public class MessagePanel extends JScrollPane {
    private JPanel contentPanel;
    private Long currentConversationId;
    
    public MessagePanel() {
        this.currentConversationId = null;
        setupUI();
    }
    
    private void setupUI() {
        contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        contentPanel.setBackground(Color.WHITE);
        
        setViewportView(contentPanel);
        setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        setBorder(null);
    }
    
    public void setMessages(List<MessageView> messages) {
        contentPanel.removeAll();
        for (MessageView message : messages) {
            addMessage(message, message.isUserMessage());
        }
        revalidate();
        repaint();
        scrollToBottom();
    }
    
    public void addMessage(MessageView message, boolean isUser) {
        MessageBubble bubble = createMessageBubble(message, isUser);
        contentPanel.add(bubble);
        contentPanel.add(Box.createVerticalStrut(10)); // Spacing between messages
        
        revalidate();
        repaint();
        scrollToBottom();
    }
    
    public void clearMessages() {
        contentPanel.removeAll();
        currentConversationId = null;
        revalidate();
        repaint();
    }
    
    public void setCurrentConversation(Long conversationId) {
        this.currentConversationId = conversationId;
    }
    
    private MessageBubble createMessageBubble(MessageView message, boolean isUser) {
        return new MessageBubble(message.getContent(), message.getTs(), isUser);
    }
    
    private void scrollToBottom() {
        SwingUtilities.invokeLater(() -> {
            JScrollBar vertical = getVerticalScrollBar();
            vertical.setValue(vertical.getMaximum());
        });
    }
}
```

### MessageBubble.java

**File:** `src/main/java/com/nyu/aichat/client/ui/MessageBubble.java`

```java
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
```

### InputPanel.java

**File:** `src/main/java/com/nyu/aichat/client/ui/InputPanel.java`

```java
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
        messageTextArea = new JTextArea(3, 30);
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
            messageTextArea.setPlaceholder("Select a conversation first");
        } else {
            messageTextArea.setPlaceholder("Type your message...");
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
```

**Note:** The `InputPanel` uses a custom `PlaceholderTextArea` class. For simplicity, you can use a regular `JTextArea` and handle placeholder via a label overlay or remove the placeholder feature.

---

## Main Entry Point

### Main.java

**File:** `src/main/java/com/nyu/aichat/client/Main.java`

```java
package com.nyu.aichat.client;

import com.nyu.aichat.client.ui.LoginFrame;

import javax.swing.*;

/**
 * Main entry point for the Swing client application.
 */
public class Main {
    public static void main(String[] args) {
        // Set look and feel to system default
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            System.err.println("Failed to set look and feel: " + e.getMessage());
        }
        
        // Start UI on EDT
        SwingUtilities.invokeLater(() -> {
            LoginFrame loginFrame = new LoginFrame();
            loginFrame.setVisible(true);
        });
    }
}
```

---

## Implementation Checklist

### Phase 1: Project Setup
- [ ] Create Maven project structure (`aichat-swing-client/`)
- [ ] Create `pom.xml` with Gson dependency
- [ ] Create `src/main/resources/config.properties`
- [ ] Create package structure (`com.nyu.aichat.client.*`)

### Phase 2: Model Classes
- [ ] Implement `UserSession.java`
- [ ] Implement `ConversationView.java` with `getFormattedDate()`
- [ ] Implement `MessageView.java` with `getFormattedTimestamp()`

### Phase 3: Utility Classes
- [ ] Implement `ConfigLoader.java`
- [ ] Implement `JsonParser.java`

### Phase 4: API Client
- [ ] Implement `ApiException.java`
- [ ] Implement `ApiClient.java`:
  - [ ] Constructor and base URL handling
  - [ ] `login()` and `signup()` methods
  - [ ] `getConversations()` method
  - [ ] `createConversation()` method
  - [ ] `updateConversationTitle()` method
  - [ ] `deleteConversation()` method
  - [ ] `getMessages()` method
  - [ ] `sendMessage()` method
  - [ ] HTTP helper methods (`sendGetRequest`, `sendPostRequest`, etc.)
  - [ ] Error parsing (`parseErrorResponse`)

### Phase 5: UI Components
- [ ] Implement `LoginFrame.java`:
  - [ ] UI setup with form fields
  - [ ] Login button handler
  - [ ] Signup checkbox and handler
  - [ ] Error display
  - [ ] Success handler (open MainChatFrame)
- [ ] Implement `MainChatFrame.java`:
  - [ ] JSplitPane layout
  - [ ] Conversation panel integration
  - [ ] Message panel integration
  - [ ] Input panel integration
  - [ ] Conversation selection handler
  - [ ] Message sending handler
- [ ] Implement `ConversationPanel.java`:
  - [ ] JList with DefaultListModel
  - [ ] Custom cell renderer
  - [ ] New Chat button
  - [ ] Delete context menu
- [ ] Implement `MessagePanel.java`:
  - [ ] Scrollable content panel
  - [ ] Message display methods
  - [ ] Auto-scroll to bottom
- [ ] Implement `MessageBubble.java`:
  - [ ] User message styling (blue, right-aligned)
  - [ ] Assistant message styling (gray, left-aligned)
  - [ ] Timestamp display
- [ ] Implement `InputPanel.java`:
  - [ ] Text area with Enter key handling
  - [ ] Send button
  - [ ] Waiting state handling

### Phase 6: Main Entry Point
- [ ] Implement `Main.java` with SwingUtilities.invokeLater

### Phase 7: Testing
- [ ] Test login with valid credentials
- [ ] Test signup with new user
- [ ] Test conversation creation
- [ ] Test message sending and receiving
- [ ] Test conversation deletion
- [ ] Test error handling (network errors, API errors)
- [ ] Test UI responsiveness

---

## Notes

1. **Threading**: All API calls should run on background threads (`ExecutorService`), and UI updates must happen on EDT (`SwingUtilities.invokeLater`).

2. **Error Handling**: Catch `ApiException` and display user-friendly error messages via `JOptionPane` or error labels.

3. **Gson Configuration**: Ensure Gson can deserialize `Instant` fields from ISO-8601 strings.

4. **Look and Feel**: Use system default L&F for native appearance.

5. **Resource Management**: Shutdown `ExecutorService` on application exit.

6. **Validation**: Client-side validation for username/password before API calls improves UX.

This plan provides complete, line-by-line implementation details for the Java Swing client matching your LLD specifications.

