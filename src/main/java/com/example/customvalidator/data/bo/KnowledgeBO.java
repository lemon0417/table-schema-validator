package com.example.customvalidator.data.bo;

import com.example.customvalidator.data.constant.TableMapping;
import com.example.customvalidator.validation.annotation.ValidColumn;
import com.example.customvalidator.validation.annotation.ValidTable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@ValidTable(name = TableMapping.KNOWLEDGE)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class KnowledgeBO {
    @ValidColumn(empty = false)
    private String name;

    @ValidColumn(min = -99)
    private Integer order;

    @ValidColumn(min = 5)
    private String rule;
}
