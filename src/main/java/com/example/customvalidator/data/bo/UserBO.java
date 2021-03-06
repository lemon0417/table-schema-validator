package com.example.customvalidator.data.bo;

import com.example.customvalidator.data.constant.TableMapping;
import com.example.customvalidator.validation.annotation.ValidColumn;
import com.example.customvalidator.validation.annotation.ValidTable;
import lombok.*;

import javax.validation.constraints.Email;

@ValidTable(name = TableMapping.USER)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserBO {
    private Integer id;

    @ValidColumn(defaultValue = "name", min = 5)
    private String name;

    @ValidColumn(message = "xxxxxxx", defaultValue = "1", min = 1)
    private Integer age;

    @Email
    private String email;

    @ValidColumn(message = "AA")
    private String address;

    private String password;
}
