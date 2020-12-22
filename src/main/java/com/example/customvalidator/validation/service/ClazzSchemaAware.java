package com.example.customvalidator.validation.service;

import com.example.customvalidator.validation.annotation.ValidColumn;
import com.example.customvalidator.validation.annotation.ValidTable;
import com.example.customvalidator.validation.vo.ColumnInfo;
import com.example.customvalidator.validation.vo.FieldInfo;
import org.springframework.util.StringUtils;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import static com.google.common.base.CaseFormat.*;

public class ClazzSchemaAware {
    private static final String MESSAGE_HEADER = "game.";
    private static final ConcurrentMap<Class<?>, List<FieldInfo>> CACHE = new ConcurrentHashMap<>();

    public static ConcurrentMap<Class<?>, List<FieldInfo>> get() {
        return CACHE;
    }

    public static List<FieldInfo> get(Class<?> clazz) {
        List<FieldInfo> fields = CACHE.get(clazz);
        if (fields != null) return fields;
        synchronized (clazz) {
            fields = CACHE.get(clazz);
            if (fields != null) return fields;
            CACHE.put(clazz, fields = parse(clazz));
        }
        return fields;
    }

    public static void refresh() {
        for (Map.Entry<Class<?>, List<FieldInfo>> entry : CACHE.entrySet()) {
            synchronized (entry.getKey()) {
                entry.setValue(parse(entry.getKey()));
            }
        }
    }

    public static List<FieldInfo> parse(Class<?> clazz) {
        List<FieldInfo> list = new ArrayList<>();

        ValidTable validTable = clazz.getAnnotation(ValidTable.class);
        String targetTable = validTable.name().replaceAll("`", "");
        if (targetTable.isEmpty()) {
            targetTable = UPPER_CAMEL.to(LOWER_UNDERSCORE, clazz.getSimpleName());
        }

        for (Field field : clazz.getDeclaredFields()) {
            field.setAccessible(true);
            ValidColumn annotation = field.getAnnotation(ValidColumn.class);
            if (annotation != null && !annotation.targetTable().isEmpty()) {
                targetTable = annotation.targetTable().replaceAll("`", "");
            }
            String columnName = getColumnName(field, annotation);
            ColumnInfo columnInfo = DatabaseSchemaAware.getColumnInfo(targetTable, columnName);
            if (columnInfo == null) continue;

            long min = 0;
            if (annotation == null) {
                if (field.getType().isAssignableFrom(Integer.class)) {
                    min = Integer.MIN_VALUE;
                } else if (field.getType().isAssignableFrom(Long.class)) {
                    min = Long.MIN_VALUE;
                }
            } else {
                if (field.getType().isAssignableFrom(String.class)) {
                    min = annotation.min();
                } else if (field.getType().isAssignableFrom(Integer.class)) {
                    min = (annotation.min() > Integer.MIN_VALUE) ? annotation.min() : Integer.MIN_VALUE;
                } else if (field.getType().isAssignableFrom(Long.class)) {
                    min = annotation.min();
                }
            }
            FieldInfo info = new FieldInfo(
                    field
                    , columnName
                    , getMessage(targetTable, field, annotation)
                    , getDefaultValue(columnInfo, annotation)
                    , targetTable
                    , columnInfo
                    , (annotation == null || annotation.empty())
                    , min
            );
            list.add(info);
        }
        return list;
    }

    private static String getColumnName(Field field, ValidColumn annotation) {
        String columnName = (annotation != null && !annotation.targetColumn().isEmpty())
                ? annotation.targetColumn()
                : field.getName();
        return LOWER_CAMEL.to(LOWER_UNDERSCORE, columnName);
    }

    private static String getMessage(String targetTable, Field field, ValidColumn annotation) {
        if (annotation != null && !annotation.message().isEmpty()) {
            return annotation.message();
        }
        StringBuilder builder = new StringBuilder()
                .append("{")
                .append(MESSAGE_HEADER)
                .append(UPPER_CAMEL.to(LOWER_CAMEL, targetTable))
                .append(".")
                .append(field.getName())
                .append("}");
        return builder.toString();
    }

    private static String getDefaultValue(ColumnInfo columnInfo, ValidColumn annotation) {
        return StringUtils.hasLength(columnInfo.getColumnDef()) || (annotation == null)
                ? columnInfo.getColumnDef()
                : annotation.defaultValue();
    }
}
