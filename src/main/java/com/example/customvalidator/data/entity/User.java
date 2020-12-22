package com.example.customvalidator.data.entity;

import com.example.customvalidator.data.constant.TableMapping;
import com.example.customvalidator.validation.annotation.ValidColumn;
import com.example.customvalidator.validation.annotation.ValidTable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.Email;

@ValidTable(name = TableMapping.USER)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ValidColumn(defaultValue = "user")
    @Column(length = 10)
    private String name;

    @ValidColumn(message = "{common.notEmpty}", defaultValue = "1", min = 1)
    private Integer age;

    @Email
    @ValidColumn(min = 5)
    private String email;

    private String address;
}
