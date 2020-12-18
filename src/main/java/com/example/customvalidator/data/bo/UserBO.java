package com.example.customvalidator.data.bo;

import com.example.customvalidator.validation.annotation.ValidColumn;
import com.example.customvalidator.validation.annotation.ValidTable;
import lombok.*;

import javax.validation.constraints.Email;

@ValidTable(name = "user")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserBO {
    private Integer id;

    @ValidColumn(defaultValue = "name")
    private String name;

    @ValidColumn(message = "xxxxxxx", defaultValue = "1", min = 1)
    private Integer age;

    @Email
    private String email;

    @ValidColumn(message = "AA")
    private String address;
}
