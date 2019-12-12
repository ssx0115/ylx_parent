package com.offcn.core.bean;

import java.io.Serializable;

//判断实体
public class Result implements Serializable {
    private boolean success;
    private String message;

    public Result(boolean success, String message) {
        super();
        this.success = success;
        this.message = message;
    }

    public Result(){

    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
