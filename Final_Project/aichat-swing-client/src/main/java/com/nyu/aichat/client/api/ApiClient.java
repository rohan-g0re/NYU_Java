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
            // Try to read error stream
            if (conn.getErrorStream() != null) {
                try (BufferedReader reader = new BufferedReader(
                        new InputStreamReader(conn.getErrorStream(), StandardCharsets.UTF_8))) {
                    String line;
                    while ((line = reader.readLine()) != null) {
                        errorBody += line;
                    }
                }
            }
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

