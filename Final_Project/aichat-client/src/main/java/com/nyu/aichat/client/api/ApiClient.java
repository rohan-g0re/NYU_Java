package com.nyu.aichat.client.api;

import com.nyu.aichat.client.model.ConversationView;
import com.nyu.aichat.client.model.MessageView;
import com.nyu.aichat.client.util.ConfigLoader;
import com.nyu.aichat.client.util.JsonParser;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ApiClient {
    private final String baseUrl;
    
    public ApiClient() {
        this.baseUrl = ConfigLoader.getApiBaseUrl();
    }
    
    public ApiClient(String baseUrl) {
        this.baseUrl = baseUrl;
    }
    
    // Auth endpoints
    public LoginResponse login(String username, String password) throws ApiException {
        Map<String, String> body = new HashMap<>();
        body.put("username", username);
        body.put("password", password);
        
        String response = sendPostRequest("/auth/login", null, body);
        return JsonParser.fromJson(response, LoginResponse.class);
    }
    
    public LoginResponse signup(String username, String password) throws ApiException {
        Map<String, String> body = new HashMap<>();
        body.put("username", username);
        body.put("password", password);
        
        String response = sendPostRequest("/auth/signup", null, body);
        return JsonParser.fromJson(response, LoginResponse.class);
    }
    
    // Conversation endpoints
    public List<ConversationView> getConversations(Long userId) throws ApiException {
        String response = sendGetRequest("/conversations", userId);
        return JsonParser.parseConversations(response);
    }
    
    public ConversationView createConversation(Long userId, String title) throws ApiException {
        Map<String, String> body = new HashMap<>();
        body.put("title", title);
        
        String response = sendPostRequest("/conversations", userId, body);
        return JsonParser.fromJson(response, ConversationView.class);
    }
    
    public void updateConversationTitle(Long userId, Long conversationId, String title) throws ApiException {
        Map<String, String> body = new HashMap<>();
        body.put("title", title);
        
        sendPutRequest("/conversations/" + conversationId + "/title", userId, body);
    }
    
    public void deleteConversation(Long userId, Long conversationId) throws ApiException {
        sendDeleteRequest("/conversations/" + conversationId, userId);
    }
    
    // Message endpoints
    public List<MessageView> getMessages(Long conversationId, Long userId) throws ApiException {
        String response = sendGetRequest("/conversations/" + conversationId + "/messages", userId);
        return JsonParser.parseMessages(response);
    }
    
    public MessageView sendMessage(Long conversationId, Long userId, String text) throws ApiException {
        Map<String, String> body = new HashMap<>();
        body.put("text", text);
        
        String response = sendPostRequest("/conversations/" + conversationId + "/messages", userId, body);
        SendMessageResponse resp = JsonParser.fromJson(response, SendMessageResponse.class);
        return resp.getAssistantMessage();
    }
    
    // HTTP helper methods
    private String sendGetRequest(String endpoint, Long userId) throws ApiException {
        try {
            HttpURLConnection conn = createConnection(endpoint, "GET", userId);
            return readResponse(conn);
        } catch (Exception e) {
            throw new ApiException("Failed to send GET request: " + e.getMessage());
        }
    }
    
    private String sendPostRequest(String endpoint, Long userId, Object body) throws ApiException {
        try {
            HttpURLConnection conn = createConnection(endpoint, "POST", userId);
            conn.setDoOutput(true);
            
            String jsonBody = JsonParser.toJson(body);
            try (OutputStream os = conn.getOutputStream()) {
                byte[] input = jsonBody.getBytes(StandardCharsets.UTF_8);
                os.write(input, 0, input.length);
            }
            
            return readResponse(conn);
        } catch (Exception e) {
            throw new ApiException("Failed to send POST request: " + e.getMessage());
        }
    }
    
    private String sendPutRequest(String endpoint, Long userId, Object body) throws ApiException {
        try {
            HttpURLConnection conn = createConnection(endpoint, "PUT", userId);
            conn.setDoOutput(true);
            
            String jsonBody = JsonParser.toJson(body);
            try (OutputStream os = conn.getOutputStream()) {
                byte[] input = jsonBody.getBytes(StandardCharsets.UTF_8);
                os.write(input, 0, input.length);
            }
            
            return readResponse(conn);
        } catch (Exception e) {
            throw new ApiException("Failed to send PUT request: " + e.getMessage());
        }
    }
    
    private String sendDeleteRequest(String endpoint, Long userId) throws ApiException {
        try {
            HttpURLConnection conn = createConnection(endpoint, "DELETE", userId);
            return readResponse(conn);
        } catch (Exception e) {
            throw new ApiException("Failed to send DELETE request: " + e.getMessage());
        }
    }
    
    private HttpURLConnection createConnection(String endpoint, String method, Long userId) throws Exception {
        URL url = new URL(baseUrl + endpoint);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod(method);
        conn.setRequestProperty("Content-Type", "application/json");
        conn.setConnectTimeout(10000);
        conn.setReadTimeout(10000);
        
        if (userId != null) {
            conn.setRequestProperty("X-User-Id", userId.toString());
        }
        
        return conn;
    }
    
    private String readResponse(HttpURLConnection conn) throws ApiException {
        try {
            int responseCode = conn.getResponseCode();
            BufferedReader reader;
            
            if (responseCode >= 200 && responseCode < 300) {
                reader = new BufferedReader(new InputStreamReader(conn.getInputStream(), StandardCharsets.UTF_8));
            } else {
                reader = new BufferedReader(new InputStreamReader(conn.getErrorStream(), StandardCharsets.UTF_8));
                String errorBody = readAll(reader);
                ErrorResponse error = JsonParser.fromJson(errorBody, ErrorResponse.class);
                throw new ApiException(responseCode, error.getError(), error.getMessage());
            }
            
            return readAll(reader);
        } catch (ApiException e) {
            throw e;
        } catch (Exception e) {
            throw new ApiException("Failed to read response: " + e.getMessage());
        }
    }
    
    private String readAll(BufferedReader reader) throws Exception {
        StringBuilder response = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            response.append(line);
        }
        return response.toString();
    }
    
    // Inner classes for request/response
    public static class LoginResponse {
        private Long userId;
        private String username;
        
        public Long getUserId() {
            return userId;
        }
        
        public String getUsername() {
            return username;
        }
    }
    
    public static class SendMessageResponse {
        private MessageView assistantMessage;
        
        public MessageView getAssistantMessage() {
            return assistantMessage;
        }
    }
    
    public static class ErrorResponse {
        private String error;
        private String message;
        
        public String getError() {
            return error;
        }
        
        public String getMessage() {
            return message;
        }
    }
}

