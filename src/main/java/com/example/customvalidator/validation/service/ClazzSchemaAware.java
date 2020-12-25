package com.example.customvalidator.validation.service;

import com.example.customvalidator.validation.annotation.ValidColumn;
import com.example.customvalidator.validation.annotation.ValidTable;
import com.example.customvalidator.validation.vo.ColumnInfo;
import com.example.customvalidator.validation.vo.FieldInfo;
import org.springframework.util.StringUtils;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
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
        for (Field field : clazz.getDeclaredFields()) {
            if (Modifier.isStatic(field.getModifiers())) continue;

            field.setAccessible(true);
            ValidColumn annotation = field.getAnnotation(ValidColumn.class);
            String targetTable = getTargetTable(clazz, validTable, annotation);
            String targetColumn = getTargetColumn(field, annotation);
            ColumnInfo columnInfo = DatabaseSchemaAware.getColumnInfo(targetTable, targetColumn);
            if (columnInfo == null) continue;

            FieldInfo info = new FieldInfo(
                    field
                    , targetColumn
                    , getMessage(targetTable, field, annotation)
                    , getDefaultValue(columnInfo, annotation)
                    , targetTable
                    , columnInfo
                    , (annotation == null || annotation.empty())
                    , getMin(field, annotation)
            );
            list.add(info);
        }
        return list;
    }

    private static String getTargetTable(Class<?> clazz, ValidTable validTable, ValidColumn annotation) {
        if (annotation != null && !annotation.targetTable().isEmpty()) {
            return annotation.targetTable().replaceAll("`", "");
        }
        String targetTable = validTable.name().replaceAll("`", "");
        if (targetTable.isEmpty()) {
            return UPPER_CAMEL.to(LOWER_UNDERSCORE, clazz.getSimpleName());
        }
        return targetTable;
    }

    private static String getTargetColumn(Field field, ValidColumn annotation) {
        String columnName = (annotation != null && !annotation.targetColumn().isEmpty())
                ? annotation.targetColumn()
                : field.getName();
        return LOWER_CAMEL.to(LOWER_UNDERSCORE, columnName);
    }

    private static long getMin(Field field, ValidColumn annotation) {
        long min = Long.MIN_VALUE;
        if (annotation == null) {
            if (Integer.class.isAssignableFrom(field.getType())) {
                min = Integer.MIN_VALUE;
            } else if (String.class.isAssignableFrom(field.getType())) {
                min = 0;
            }
        } else {
            if (Integer.class.isAssignableFrom(field.getType())) {
                min = (annotation.min() > Integer.MIN_VALUE) ? annotation.min() : Integer.MIN_VALUE;
            } else if (String.class.isAssignableFrom(field.getType())) {
                min = annotation.min() > 0 ? annotation.min() : 0;
            } else {
                min = annotation.min();
            }
        }
        return min;
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
