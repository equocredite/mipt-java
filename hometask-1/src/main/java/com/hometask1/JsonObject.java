package com.hometask1;

import java.util.HashMap;
import java.util.Map;

public class JsonObject {
    private final Map<String, Object> elements = new HashMap<>();

    public <T> T getAs(String key, Class<T> type) {
        Object value = elements.get(key);
        return type.cast(value);
    }

    public void put(String key, Object value) {
        elements.put(key, value);
    }
}
