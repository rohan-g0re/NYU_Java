package com.nyu.aichat.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nyu.aichat.entity.Message;
import com.nyu.aichat.entity.MessageRole;
import com.nyu.aichat.exception.AiServiceException;
import com.nyu.aichat.util.Constants;
import com.nyu.aichat.util.TextCleaner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Service for integrating with Google Gemini API.
 * Handles prompt building, API communication, and response parsing.
 */
@Service
public class GeminiService {
    private static final Logger logger = LoggerFactory.getLogger(GeminiService.class);
    
    @Value("${gemini.api.key:}")
    private String geminiApiKey;
    
    private static final String MODEL = "gemini-2.5-flash";
    private static final String API_URL = "https://generativelanguage.googleapis.com/v1beta/models/" + MODEL + ":generateContent";
    private static final int TIMEOUT_SECONDS = 10000; // 10 seconds in milliseconds
    private static final int CONTEXT_MESSAGES = 6;
    
    private final ObjectMapper objectMapper;
    
    public GeminiService() {
        this.objectMapper = new ObjectMapper();
    }
    
    /**
     * Generates an AI response using the Gemini API.
     * 
     * @param userMessage The current user message
     * @param contextMessages Previous messages for context (up to 6)
     * @return The cleaned AI response text
     * @throws AiServiceException if API key is missing or API call fails
     */
    public String generateResponse(String userMessage, List<Message> contextMessages) {
        if (geminiApiKey == null || geminiApiKey.isEmpty()) {
            logger.error("Gemini API key not configured");
            throw new AiServiceException("Gemini API key not configured");
        }
        
        try {
            String prompt = buildPrompt(userMessage, contextMessages);
            logger.debug("Built prompt with {} context messages", contextMessages.size());
            
            String rawResponse = callGeminiApi(prompt);
            logger.debug("Received response from Gemini API");
            
            return TextCleaner.cleanResponse(rawResponse);
        } catch (AiServiceException e) {
            // Re-throw AiServiceException as-is
            throw e;
        } catch (Exception e) {
            logger.error("Unexpected error while generating AI response", e);
            throw new AiServiceException(Constants.ERROR_AI_FALLBACK, e);
        }
    }
    
    /**
     * Builds a prompt string from context messages and the current user message.
     * 
     * @param userMessage The current user message
     * @param contextMessages Previous messages (in DESC order, most recent first)
     * @return Formatted prompt string
     */
    private String buildPrompt(String userMessage, List<Message> contextMessages) {
        StringBuilder prompt = new StringBuilder();
        
        // Context messages come in DESC order (most recent first), but we need chronological order
        // Reverse the list to get oldest first, then limit to last 6
        List<Message> recentMessages = contextMessages.stream()
                .limit(CONTEXT_MESSAGES)
                .collect(Collectors.toList());
        
        // Reverse to chronological order (oldest to newest)
        Collections.reverse(recentMessages);
        
        for (Message msg : recentMessages) {
            String role = msg.getRole() == MessageRole.USER ? Constants.ROLE_USER : Constants.ROLE_ASSISTANT;
            prompt.append(role).append(": ").append(msg.getContent()).append("\n");
        }
        
        // Add current user message
        prompt.append(Constants.ROLE_USER).append(": ").append(userMessage);
        
        return prompt.toString();
    }
    
    /**
     * Calls the Gemini API with the given prompt.
     * 
     * @param prompt The formatted prompt string
     * @return The raw response text from Gemini
     * @throws Exception if API call fails
     */
    private String callGeminiApi(String prompt) throws Exception {
        String responseStr = sendHttpRequest(prompt);
        return parseGeminiResponse(responseStr);
    }
    
    /**
     * Sends an HTTP POST request to the Gemini API.
     * 
     * @param prompt The prompt to send
     * @return The raw JSON response string
     * @throws Exception if HTTP request fails
     */
    private String sendHttpRequest(String prompt) throws Exception {
        HttpURLConnection conn = createConnection();
        sendRequest(conn, prompt);
        return readResponse(conn);
    }
    
    /**
     * Creates and configures an HTTP connection to the Gemini API.
     * 
     * @return Configured HttpURLConnection
     * @throws Exception if URL creation fails
     */
    private HttpURLConnection createConnection() throws Exception {
        URL url = new URL(API_URL + "?key=" + geminiApiKey);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Content-Type", "application/json");
        conn.setConnectTimeout(TIMEOUT_SECONDS);
        conn.setReadTimeout(TIMEOUT_SECONDS);
        conn.setDoOutput(true);
        return conn;
    }
    
    /**
     * Sends the request body to the Gemini API.
     * 
     * @param conn The HTTP connection
     * @param prompt The prompt to send
     * @throws Exception if writing request fails
     */
    private void sendRequest(HttpURLConnection conn, String prompt) throws Exception {
        // Build request JSON using Jackson
        String requestBody = objectMapper.writeValueAsString(
            Collections.singletonMap("contents", 
                Collections.singletonList(
                    Collections.singletonMap("parts",
                        Collections.singletonList(
                            Collections.singletonMap("text", prompt)
                        )
                    )
                )
            )
        );
        
        try (OutputStream os = conn.getOutputStream()) {
            byte[] input = requestBody.getBytes(StandardCharsets.UTF_8);
            os.write(input, 0, input.length);
        }
    }
    
    /**
     * Reads the HTTP response from the Gemini API.
     * 
     * @param conn The HTTP connection
     * @return The raw response body as a string
     * @throws Exception if reading response fails or status code is not OK
     */
    private String readResponse(HttpURLConnection conn) throws Exception {
        int responseCode = conn.getResponseCode();
        if (responseCode != HttpURLConnection.HTTP_OK) {
            logger.error("Gemini API returned error code: {}", responseCode);
            throw new Exception("Gemini API returned error code: " + responseCode);
        }
        
        try (BufferedReader br = new BufferedReader(
                new InputStreamReader(conn.getInputStream(), StandardCharsets.UTF_8))) {
            return br.lines().collect(Collectors.joining());
        }
    }
    
    /**
     * Parses the JSON response from Gemini API and extracts the text content.
     * 
     * @param responseStr The raw JSON response string
     * @return The extracted text content
     * @throws Exception if JSON parsing fails or text field is missing
     */
    private String parseGeminiResponse(String responseStr) throws Exception {
        try {
            JsonNode root = objectMapper.readTree(responseStr);
            
            // Navigate JSON structure: candidates[0].content.parts[0].text
            JsonNode candidates = root.path("candidates");
            if (!candidates.isArray() || candidates.size() == 0) {
                throw new Exception("Invalid response format from Gemini API: no candidates found");
            }
            
            JsonNode content = candidates.get(0).path("content");
            JsonNode parts = content.path("parts");
            if (!parts.isArray() || parts.size() == 0) {
                throw new Exception("Invalid response format from Gemini API: no parts found");
            }
            
            String text = parts.get(0).path("text").asText();
            if (text == null || text.isEmpty()) {
                throw new Exception("Invalid response format from Gemini API: text field is empty");
            }
            
            return text;
        } catch (Exception e) {
            logger.error("Failed to parse Gemini API response", e);
            throw new Exception("Invalid response format from Gemini API: " + e.getMessage(), e);
        }
    }
}
