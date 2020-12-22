package com.example.customvalidator.controller;

import com.example.customvalidator.result.ResultErrors;
import com.example.customvalidator.result.ResultHolder;
import lombok.extern.log4j.Log4j2;
import org.hibernate.validator.internal.engine.path.PathImpl;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.Nullable;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import java.util.Set;

@Log4j2
@ControllerAdvice
public class ControllerExceptionAdvice {
    @ExceptionHandler(ConstraintViolationException.class)
    @Nullable
    public ResponseEntity<ResultErrors> handleConstraintViolation(ConstraintViolationException e) {
        log(e);
        ResultErrors errors = ResultHolder.errors();

        Set<ConstraintViolation<?>> constraintViolations = e.getConstraintViolations();
        if (constraintViolations != null && e.getConstraintViolations().size() > 0) {
            constraintViolations.forEach(cv -> {
                errors.errors(((PathImpl) cv.getPropertyPath()).getLeafNode().getName(), cv.getMessage());
            });
        } else {
            errors.message(e.getMessage());
        }
        return errors.out(HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @Nullable
    public ResponseEntity<ResultErrors> handleMethodArgumentNotValid(MethodArgumentNotValidException e) {
        log(e);
        ResultErrors errors = ResultHolder.errors();

        if (e.getBindingResult() != null && e.getBindingResult().getErrorCount() > 0) {
            for (ObjectError err : e.getBindingResult().getAllErrors()) {
                if (err instanceof FieldError) {
                    errors.errors(((FieldError) err).getField(), err.getDefaultMessage());
                } else {
                    errors.errors(err.getObjectName(), err.getDefaultMessage());
                }
            }
        } else {
            errors.message(e.getMessage());
        }
        return errors.out(HttpStatus.BAD_REQUEST);
    }

    private void log(Exception e) {
        if (log.isDebugEnabled()) {
            log.error(e.getMessage(), e);
        } else {
            log.error(e.getMessage());
        }
    }
}
