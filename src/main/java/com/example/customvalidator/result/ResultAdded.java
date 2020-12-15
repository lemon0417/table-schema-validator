package com.example.customvalidator.result;

import lombok.Getter;


/**
 * 統一 Controller Result格式
 */
@Getter
public class ResultAdded<T, J> {
    T data;
    J added;

    ResultAdded(){

    }

    public ResultAdded<T, J> data(T data) {
        this.data = data;
        return this;
    }

    public ResultAdded<T, J> added(J added) {
        this.added = added;
        return this;
    }
}
