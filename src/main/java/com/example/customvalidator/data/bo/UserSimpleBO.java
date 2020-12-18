package com.example.customvalidator.data.bo;

import com.example.customvalidator.validation.annotation.ValidColumn;
import com.example.customvalidator.validation.annotation.ValidTable;
import lombok.*;

@ValidTable(name = "user")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserSimpleBO {
    private Integer id;

    @ValidColumn(defaultValue = "name")
    private String name;

    @ValidColumn(message = "xxxxxxx", defaultValue = "1")
    private Integer age;
}
