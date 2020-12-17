package com.example.customvalidator.validation.vo;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ColumnInfo {
    private Class<?> dataType;
    private Integer columnSize;
    private String columnDef;
    private Boolean nullable;
}
