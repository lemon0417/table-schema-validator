package com.example.customvalidator.data.entity;

import com.example.customvalidator.validation.annotation.ValidColumn;
import com.example.customvalidator.validation.annotation.ValidTable;
import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.Email;

@ValidTable
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ValidColumn(defaultValue = "user")
    @Column(length = 10)
    private String name;

    @ValidColumn(message = "xxxxxxx", defaultValue = "1", min = 1, max = 120)
    private Integer age;

    @Email
    @ValidColumn(min = 5)
    private String email;

    @ValidColumn(message = "AA")
    private String address;
}
