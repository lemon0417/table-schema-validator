package com.example.customvalidator.validation.util;

import javax.persistence.Table;
import java.util.Locale;

public class TransformerUtil {

    public static String toTableName(Class<?> clazz) {
        String name;
        if (clazz.isAnnotationPresent(Table.class) && clazz.getAnnotation(Table.class).name().length() > 0) {
            name = clazz.getAnnotation(Table.class).name();
        } else {
            name = toUnderscoreNaming(clazz.getSimpleName());
        }
        return name;
    }

    public static String toUnderscoreNaming(String source) {
        StringBuilder builder = new StringBuilder(source.replace('.', '_'));
        for (int i = 1; i < builder.length() - 1; i++) {
            if (isUnderscoreRequired(builder.charAt(i - 1), builder.charAt(i), builder.charAt(i + 1))) {
                builder.insert(i++, '_');
            }
        }
        return builder.toString().toLowerCase(Locale.ROOT);
    }

    public static String toUpperCamelCaseNaming(String source) {
        StringBuilder sb = new StringBuilder();
        if (source == null || source.isEmpty()) {
            return "";
        } else if (!source.contains("_")) {
            return source.substring(0, 1).toUpperCase() + source.substring(1);
        }
        String camels[] = source.split("_");
        for (String camel : camels) {
            if (camel.isEmpty()) {
                continue;
            }
            if (sb.length() == 0) {
                sb.append(camel.toLowerCase());
            } else {
                sb.append(camel.substring(0, 1).toUpperCase());
                sb.append(camel.substring(1).toLowerCase());
            }
        }
        return sb.toString();
    }

    private static boolean isUnderscoreRequired(char before, char current, char after) {
        return Character.isLowerCase(before) && Character.isUpperCase(current) && Character.isLowerCase(after);
    }
}
