package com.example.customvalidator.validation.config;

import com.example.customvalidator.validation.annotation.ValidColumn;
import com.example.customvalidator.validation.annotation.ValidTable;
import com.example.customvalidator.validation.vo.ColumnInfo;
import com.example.customvalidator.validation.vo.FieldInfo;
import org.springframework.util.StringUtils;

import javax.persistence.Entity;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import static com.google.common.base.CaseFormat.*;

public class ClazzSchemaComponent {
    private static final String MESSAGE_HEADER = "game.";
    private static final ConcurrentMap<Class<?>, List<FieldInfo>> CACHE = new ConcurrentHashMap<>();

    public static List<FieldInfo> getFields(Class<?> clazz) {
        List<FieldInfo> fields = CACHE.get(clazz);
        if (fields != null) return fields;
        synchronized (clazz) {
            fields = CACHE.get(clazz);
            if (fields != null) return fields;
            CACHE.put(clazz, fields = findDetails(clazz));
        }
        return fields;
    }

    private static List<FieldInfo> findDetails(Class<?> clazz) {
        List<FieldInfo> list = new ArrayList<>();

        Class<?> targetEntity = clazz.isAnnotationPresent(Entity.class)
                ? clazz
                : (
                (clazz.isAnnotationPresent(ValidTable.class)
                        && !clazz.getAnnotation(ValidTable.class).name().equals(Void.TYPE))
                        ? clazz.getAnnotation(ValidTable.class).name()
                        : null
        );

        for (Field field : clazz.getDeclaredFields()) {
            field.setAccessible(true);
            ValidColumn annotation = field.getAnnotation(ValidColumn.class);
            String columnName = getColumnName(field, annotation);
            targetEntity = getTargetEntity(targetEntity, annotation);
            ColumnInfo columnInfo = DatabaseSchemaComponent.getColumnInfo(targetEntity, columnName);
            if (columnInfo == null) continue;

            long min = 0;
            if (field.getType().isAssignableFrom(String.class)) {
                min = (annotation != null) ? annotation.min() : 0;
            } else if (field.getType().isAssignableFrom(Integer.class)) {
                min = (annotation != null) && (annotation.min() > Integer.MIN_VALUE) ? annotation.min() : Integer.MIN_VALUE;
            } else if (field.getType().isAssignableFrom(Long.class)) {
                min = (annotation != null) ? annotation.min() : Long.MIN_VALUE;
            }

            FieldInfo info = new FieldInfo(
                    field
                    , columnName
                    , getMessage(targetEntity, field, annotation)
                    , getDefaultValue(columnInfo, annotation)
                    , targetEntity
                    , columnInfo
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

    private static String getMessage(Class<?> clazz, Field field, ValidColumn annotation) {
        return (annotation != null && !annotation.message().isEmpty())
                ? annotation.message()
                : MESSAGE_HEADER + UPPER_CAMEL.to(LOWER_CAMEL, clazz.getSimpleName()) + "." + field.getName();
    }

    private static String getDefaultValue(ColumnInfo columnInfo, ValidColumn annotation) {
        return StringUtils.hasLength(columnInfo.getColumnDef()) || (annotation == null)
                ? columnInfo.getColumnDef()
                : annotation.defaultValue();
    }

    private static Class<?> getTargetEntity(Class<?> targetEntity, ValidColumn annotation) {
        return annotation == null || (annotation != null && annotation.targetEntity().equals(Void.TYPE))
                ? targetEntity : annotation.targetEntity();
    }

    public static ConcurrentMap<Class<?>, List<FieldInfo>> getClassSchema() {
        return CACHE;
    }
}
