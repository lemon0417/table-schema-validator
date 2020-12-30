package com.example.customvalidator.validation;

import com.example.customvalidator.validation.annotation.ValidTable;
import com.example.customvalidator.validation.service.ClazzSchemaAware;
import com.example.customvalidator.validation.vo.ColumnInfo;
import com.example.customvalidator.validation.vo.FieldInfo;
import org.springframework.util.NumberUtils;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.util.List;

public class SchemaValidator implements ConstraintValidator<ValidTable, Object> {
    private static final String NOT_EMPTY_MESSAGE = "{common.notEmpty}";

    @Override
    public void initialize(ValidTable validColumn) {
    }

    @Override
    public boolean isValid(Object vo, ConstraintValidatorContext context) {
        boolean result = true;
        List<FieldInfo> fieldInfos = ClazzSchemaAware.get(vo.getClass());
        for (FieldInfo fieldInfo : fieldInfos) {
            boolean currentResult = true;
            Field field = fieldInfo.getField();
            ColumnInfo info = fieldInfo.getColumnInfo();

            Object obj = null;
            Class<?> dataType = info.getDataType();
            try {
                // set default value
                obj = field.get(vo);
                boolean empty = (obj == null) || obj.toString().isEmpty();
                if (!info.getNullable() && empty) {
                    if (!fieldInfo.isEmpty()) {
                        context.buildConstraintViolationWithTemplate(NOT_EMPTY_MESSAGE)
                                .addPropertyNode(field.getName())
                                .addConstraintViolation()
                                .disableDefaultConstraintViolation();
                        result = false;
                        continue;
                    }

                    String defaultValue = fieldInfo.getDefaultValue();
                    if (Number.class.isAssignableFrom(dataType)) {
                        field.set(vo, NumberUtils.parseNumber(0 + defaultValue, dataType.asSubclass(Number.class)));
                    } else if (Boolean.class.isAssignableFrom(dataType)) {
                        field.set(vo, defaultValue.equals("1"));
                    } else {
                        field.set(vo, dataType.cast(defaultValue));
                    }
                    // no need to check
                    continue;
                }
            } catch (Exception e) {
                e.printStackTrace();
                result = false;
                context.buildConstraintViolationWithTemplate(e.getMessage())
                        .addConstraintViolation();
            }

            if (obj != null) {
                long min = fieldInfo.getMin();
                Integer columnSize = info.getColumnSize();
                int length = obj.toString().length();

                if (obj instanceof String) {
                    currentResult = (length <= columnSize) && (length >= min);
                } else if (obj instanceof Number) {
                    currentResult = length <= columnSize;
                    if (obj instanceof BigDecimal) {
                        currentResult &= ((BigDecimal) obj).compareTo(new BigDecimal(min)) >= 0;
                    } else {
                        currentResult &= Long.parseLong(obj.toString()) >= min;
                    }
                }
            }

            result &= currentResult;
            if (!currentResult) {
                context.buildConstraintViolationWithTemplate(fieldInfo.getMessage())
                        .addPropertyNode(field.getName())
                        .addConstraintViolation()
                        .disableDefaultConstraintViolation();
            }
        }
        return result;
    }
}
