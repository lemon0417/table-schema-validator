package com.example.customvalidator.data.bo;

import com.example.customvalidator.data.constant.TableMapping;
import com.example.customvalidator.validation.annotation.ValidColumn;
import com.example.customvalidator.validation.annotation.ValidTable;
import lombok.*;

@ValidTable(name = TableMapping.USER)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserSimpleBO {
    private Integer id;

    @ValidColumn(defaultValue = "name")
    private String name;

    @ValidColumn(message = "xxxxxxx", defaultValue = "1")
    private Integer age;
}
