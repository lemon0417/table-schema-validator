package com.example.customvalidator.result;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@Getter
public class ResultFail {
    @JsonInclude(JsonInclude.Include.NON_NULL)
    String traceId;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    String code;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    String message;

    ResultFail(){}

    public ResultFail traceId(String traceId) {
        this.traceId = traceId;
        return this;
    }

    public ResultFail code(String code) {
        this.code = code;
        return this;
    }

    public ResultFail message(String message) {
        this.message = message;
        return this;
    }

    public ResponseEntity<ResultFail> out(HttpStatus status) {
        return new ResponseEntity<>(this, status);
    }

}
