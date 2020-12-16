package com.example.customvalidator.validation;

import com.example.customvalidator.validation.annotation.ValidColumn;
import com.example.customvalidator.validation.annotation.ValidTable;
import com.example.customvalidator.validation.config.ClassSchemaComponent;
import com.example.customvalidator.validation.config.DatabaseSchemaComponent;
import com.example.customvalidator.validation.util.TransformerUtil;
import com.example.customvalidator.validation.vo.ColumnInfo;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import javax.persistence.Entity;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.lang.reflect.Field;

public class TableSchemaValidator implements ConstraintValidator<ValidTable, Object> {
    private static final String MESSAGE_HEADER = "game.";

    @Override
    public void initialize(ValidTable validColumn) {
    }

    @Override
    public boolean isValid(Object vo, ConstraintValidatorContext context) {
        boolean result = true;
        Class<?> clazz = vo.getClass();
        Class<?> targetEntity = clazz.isAnnotationPresent(Entity.class)
                ? clazz
                : (
                (clazz.isAnnotationPresent(ValidTable.class)
                        && !clazz.getAnnotation(ValidTable.class).name().equals(Void.TYPE))
                        ? clazz.getAnnotation(ValidTable.class).name()
                        : null
        );

        Field[] fields = ClassSchemaComponent.getFields(clazz.getName(), clazz);
        for (Field field : fields) {
            if (!field.isAnnotationPresent(ValidColumn.class)) {
                continue;
            }

            boolean currentResult = true;
            field.setAccessible(true);
            ValidColumn annotation = field.getDeclaredAnnotation(ValidColumn.class);
            String columnName = TransformerUtil.toUnderscoreNaming(
                    StringUtils.hasText(annotation.targetColumn())
                            ? annotation.targetColumn()
                            : field.getName()
            );

            Class<?> currentTargetEntity = annotation.targetEntity().equals(Void.TYPE)
                    ? targetEntity : annotation.targetEntity();

            ColumnInfo info = DatabaseSchemaComponent.getColumnInfo(currentTargetEntity, columnName);
            Class<?> dataType = info.getDataType();
            Assert.isTrue(dataType.isAssignableFrom(field.getType()),
                    "Column[" + field.getName() + "] and schema[" + columnName + "] type is mismatch");

            Object obj = null;
            try {
                // set default value
                obj = field.get(vo);
                if (obj == null && !info.getNullable()) {
                    String defaultValue = (info.getColumnDef() != null) && (info.getColumnDef().length() > 0)
                            ? info.getColumnDef()
                            : annotation.defaultValue();
                    field.set(vo, dataType.cast(defaultValue));
                    obj = dataType.cast(defaultValue);
                }
            } catch (Exception e) {
                e.printStackTrace();
                result = false;
                context.buildConstraintViolationWithTemplate(e.getMessage())
                        .addConstraintViolation();
            }

            if (obj != null) {
                long min = annotation.min();
                long max = annotation.max();
                if (min > max) {
                    throw new IllegalArgumentException();
                }

                if (obj instanceof String) {
                    min = min > 0 ? min : 0;
                    max = max > info.getColumnSize() ? info.getColumnSize() : max;
                    String temp = (String) obj;
                    currentResult = (temp.length() <= max) && (temp.length() >= min);
                } else if (obj instanceof Number) {
                    currentResult = String.valueOf(obj).length() <= info.getColumnSize();

                    if (obj instanceof Integer) {
                        min = min > Integer.MIN_VALUE ? min : Integer.MIN_VALUE;
                        max = max < Integer.MAX_VALUE ? max : Integer.MAX_VALUE;
                        int temp = (int) obj;
                        currentResult &= temp >= min && temp <= max;
                    } else if (obj instanceof Long) {
                        min = Math.max(min, Long.MIN_VALUE);
                        max = Math.min(max, Long.MAX_VALUE);
                        long temp = (long) obj;
                        currentResult &= (temp >= min) && (temp <= max);
                    }
                }
            }

            result &= currentResult;
            if (!currentResult) {
                String message = getMessage(currentTargetEntity.getSimpleName(), field, annotation.message());
                context.buildConstraintViolationWithTemplate(message)
                        .addPropertyNode(field.getName())
                        .addConstraintViolation()
                        .disableDefaultConstraintViolation();
            }
        }
        return result;
    }

    private String getMessage(String key, Field field, String customMessage) {
        String message = customMessage.length() > 0
                ? customMessage
                : MESSAGE_HEADER + key + "." + field.getName();
        if (!message.startsWith("{")) message = "{" + message;
        if (!message.endsWith("}")) message = message + "}";
        return message;
    }
}
