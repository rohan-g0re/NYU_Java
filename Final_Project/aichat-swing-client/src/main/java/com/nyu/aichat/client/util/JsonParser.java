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

