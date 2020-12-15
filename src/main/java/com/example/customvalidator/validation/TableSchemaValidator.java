package com.example.customvalidator.validation;

import com.example.customvalidator.validation.config.ClassSchemaComponent;
import com.example.customvalidator.validation.config.DatabaseSchemaComponent;
import com.example.customvalidator.validation.annotation.ValidColumn;
import com.example.customvalidator.validation.annotation.ValidTable;
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
        String tableName = TransformerUtil.toTableName(clazz);
        String key = clazz.isAnnotationPresent(Entity.class)
                ? tableName
                : (
                clazz.isAnnotationPresent(ValidTable.class)
                        ? TransformerUtil.toUnderscoreNaming(clazz.getAnnotation(ValidTable.class).name())
                        : ""
        );

        Field[] fields = ClassSchemaComponent.getFields(tableName, clazz);
        for (Field field : fields) {
            if (!field.isAnnotationPresent(ValidColumn.class)) {
                continue;
            }

            boolean currentResult = true;
            field.setAccessible(true);
            ValidColumn annotation = field.getAnnotation(ValidColumn.class);
            String columnName = TransformerUtil.toUnderscoreNaming(
                    StringUtils.hasText(annotation.targetColumn())
                            ? annotation.targetColumn()
                            : field.getName()
            );

            if (annotation.targetEntity().length() > 0) {
                key = TransformerUtil.toUnderscoreNaming(annotation.targetEntity());
            }

            ColumnInfo info = DatabaseSchemaComponent.getColumnInfo(key, columnName);
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
                if (obj instanceof String) {
                    currentResult = ((String) obj).length() <= info.getColumnSize();
                } else if (obj instanceof Number) {
                    currentResult = String.valueOf(obj).length() <= info.getColumnSize();
                }
            }

            result &= currentResult;
            if (!currentResult) {
                String message = getMessage(key, field, annotation);
                context.buildConstraintViolationWithTemplate(message)
                        .addPropertyNode(field.getName())
                        .addConstraintViolation()
                        .disableDefaultConstraintViolation();
            }
        }
        return result;
    }

    private String getMessage(String key, Field field, ValidColumn annotation) {
        String message = annotation.message().length() > 0
                ? annotation.message()
                : MESSAGE_HEADER + TransformerUtil.toUpperCamelCaseNaming(key) + "." + field.getName();
        if (!message.startsWith("{")) message = "{" + message;
        if (!message.endsWith("}")) message = message + "}";
        return message;
    }
}
