package com.nyu.aichat.client.util;

import java.io.InputStream;
import java.util.Properties;

public class ConfigLoader {
    private static final String DEFAULT_BASE_URL = "http://localhost:8080/api/v1";
    
    public static String getApiBaseUrl() {
        try {
            Properties props = new Properties();
            InputStream is = ConfigLoader.class.getClassLoader()
                    .getResourceAsStream("config.properties");
            if (is != null) {
                props.load(is);
                String url = props.getProperty("api.base.url");
                if (url != null && !url.isEmpty()) {
                    return url;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return DEFAULT_BASE_URL;
    }
}

