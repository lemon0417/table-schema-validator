package com.example.customvalidator.validation.annotation;

import javax.validation.Payload;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Target({FIELD})
@Retention(RUNTIME)
public @interface ValidColumn {
    String message() default "";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    String targetTable() default "";

    String targetColumn() default "";

    String defaultValue() default "";

    boolean empty() default true;

    long min() default 0;
}
