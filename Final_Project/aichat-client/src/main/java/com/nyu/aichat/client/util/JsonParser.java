package com.nyu.aichat.client.util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializer;
import com.nyu.aichat.client.model.ConversationView;
import com.nyu.aichat.client.model.MessageView;

import java.time.Instant;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class JsonParser {
    private static final Gson gson = new GsonBuilder()
            .registerTypeAdapter(Instant.class, (JsonDeserializer<Instant>) (json, typeOfT, context) -> 
                Instant.parse(json.getAsString()))
            .create();
    
    public static <T> T fromJson(String json, Class<T> clazz) {
        return gson.fromJson(json, clazz);
    }
    
    public static String toJson(Object obj) {
        return gson.toJson(obj);
    }
    
    public static List<ConversationView> parseConversations(String json) {
        return gson.fromJson(json, new com.google.gson.reflect.TypeToken<List<ConversationView>>(){}.getType());
    }
    
    public static List<MessageView> parseMessages(String json) {
        return gson.fromJson(json, new com.google.gson.reflect.TypeToken<List<MessageView>>(){}.getType());
    }
}

