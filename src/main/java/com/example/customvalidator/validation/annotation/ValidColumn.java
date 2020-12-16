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

    Class targetEntity() default void.class;

    String targetColumn() default "";

    String defaultValue() default "";

    long min() default Long.MIN_VALUE;

    long max() default Long.MAX_VALUE;
}
