package com.example.customvalidator.data.bo;

import com.example.customvalidator.data.constant.TableMapping;
import com.example.customvalidator.validation.annotation.ValidTable;
import lombok.*;

import javax.validation.Valid;

@ValidTable(name = TableMapping.KNOWLEDGE_CATEGORY)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class KnowledgeCategoryBO {
    private String name;

    @Valid
    private KnowledgeBO knowledge;
}
