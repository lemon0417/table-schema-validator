package com.example.customvalidator.data.vo;

import com.example.customvalidator.data.entity.User;
import com.example.customvalidator.validation.annotation.ValidColumn;
import com.example.customvalidator.validation.annotation.ValidTable;
import lombok.*;

import javax.validation.constraints.Email;

@ValidTable(name = User.class)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserVo {
    private Integer id;

    @ValidColumn(defaultValue = "name")
    private String name;

    @ValidColumn(message = "xxxxxxx", defaultValue = "1")
    private Integer age;

    @Email
    private String email;

    @ValidColumn(message = "AA")
    private String address;
}
