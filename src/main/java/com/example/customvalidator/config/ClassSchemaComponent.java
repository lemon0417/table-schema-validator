package com.example.customvalidator.config;

import java.lang.reflect.Field;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class ClassSchemaComponent {
    private static ConcurrentMap<String, Field[]> map = new ConcurrentHashMap<>();

    public static Field[] getFields(String key, Class<?> clazz) {
        if (map.containsKey(key)) {
            return map.get(key);
        }

        synchronized (map) {
            if (map.containsKey(key)) {
                return map.get(key);
            }
            map.put(key, clazz.getDeclaredFields());
        }
        return map.get(key);
    }

    public static ConcurrentMap<String, Field[]> getClassSchema() {
        return map;
    }
}
