package com.example.customvalidator.result;

public class Result {
    ResultData data = new ResultData();
    ResultAdded added = new ResultAdded();
    ResultRefresh refresh = new ResultRefresh();
    ResultFail fail = new ResultFail();
    ResultErrors errors = new ResultErrors();

    Result(){}

    public void clear(){
        this.data.data = null;
        this.added.data = null;
        this.added.added = null;
        this.refresh.data = null;
        this.fail.traceId = null;
        this.fail.message = null;
        this.errors.traceId = null;
        this.errors.message = null;
        this.errors.errors = null;
    }
}
