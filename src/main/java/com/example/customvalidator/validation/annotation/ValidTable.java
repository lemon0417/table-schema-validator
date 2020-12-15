package com.example.customvalidator.validation.annotation;

import com.example.customvalidator.validation.TableSchemaValidator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Constraint(validatedBy = TableSchemaValidator.class)
@Target({TYPE})
@Retention(RUNTIME)
public @interface ValidTable {
    String message() default "";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    String name() default "";
}
