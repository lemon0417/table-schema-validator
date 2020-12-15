package com.example.customvalidator.validation.vo;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@Builder
@ToString
public class ColumnInfo {
    private Class<?> dataType;
    private Integer columnSize;
    private String columnDef;
    private Boolean nullable;
}
