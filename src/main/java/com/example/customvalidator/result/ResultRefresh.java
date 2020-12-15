package com.example.customvalidator.result;

import lombok.Getter;


/**
 * 統一 Controller Result格式
 */
@Getter
public class ResultRefresh<T> {
    T data;

    ResultRefresh() {
    }

    public ResultRefresh<T> data(T data) {
        this.data = data;
        return this;
    }
}
