package com.example.customvalidator.data.entity;

import com.example.customvalidator.validation.annotation.ValidColumn;
import com.example.customvalidator.validation.annotation.ValidTable;
import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;

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

    @Min(1)
    @Max(120)
    @ValidColumn(message = "xxxxxxx", defaultValue = "1")
    private Integer age;

    @Email
    private String email;

    @ValidColumn(message = "AA")
    private String address;
}
