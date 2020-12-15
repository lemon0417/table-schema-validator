package com.example.customvalidator.result;

import lombok.Getter;


/**
 * 統一 Controller Result格式
 */
@Getter
public class ResultData<T> {
    T data;

    ResultData(){

    }

    public ResultData<T> data(T data) {
        this.data = data;
        return this;
    }
}
