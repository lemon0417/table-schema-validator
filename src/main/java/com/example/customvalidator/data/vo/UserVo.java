package com.example.customvalidator.data.vo;

import com.example.customvalidator.validation.annotation.ValidColumn;
import com.example.customvalidator.validation.annotation.ValidTable;
import lombok.*;

import javax.validation.constraints.Email;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;

@ValidTable(name = "User")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserVo {
    private Integer id;

    @ValidColumn(defaultValue = "name")
    private String name;

    @Min(1)
    @Max(120)
    @ValidColumn(message = "xxxxxxx", defaultValue = "1")
    private Integer age;

    @Email
    private String email;

    @ValidColumn(message = "AA")
    private String address;
}
