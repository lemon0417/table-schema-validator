package com.example.customvalidator.data.bo;

import com.example.customvalidator.constant.TableMapping;
import com.example.customvalidator.validation.annotation.ValidColumn;
import com.example.customvalidator.validation.annotation.ValidTable;
import lombok.*;

@ValidTable(name = TableMapping.KNOWLEDGE)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class KnowledgeBO {
    private Long id;

    private Long category;

    @ValidColumn(min = 1)
    private String name;

    @ValidColumn(min = 5)
    private String rule;
}
