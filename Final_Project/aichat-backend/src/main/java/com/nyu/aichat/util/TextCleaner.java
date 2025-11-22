package com.nyu.aichat.util;

public class TextCleaner {
    private static final String THINK_PATTERN = "(?s)<think>.*?</think>";
    
    public static String cleanResponse(String rawResponse) {
        if (rawResponse == null || rawResponse.isEmpty()) {
            return "";
        }
        
        // Remove <think> blocks (Gemini reasoning blocks)
        String cleaned = rawResponse.replaceAll(THINK_PATTERN, "");
        
        // Remove empty lines
        cleaned = cleaned.replaceAll("(?m)^\\s*$", "");
        
        // Trim whitespace
        cleaned = cleaned.trim();
        
        return cleaned;
    }
}

