package com.example.customvalidator.validation.vo;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.lang.reflect.Field;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class FieldInfo {
    private Field field;
    private String targetTable;
    private String targetColumn;
    private String message;
    private String defaultValue;
    private ColumnInfo columnInfo;
    private boolean empty;
    private long min;
}
