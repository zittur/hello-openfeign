package com.zittur.common.exception;

public class BaseException extends RuntimeException {

    public int code;

    public String message;

    public BaseException(int code, String message) {
        this.code = code;
        this.message = message;
    }

    public String getMessage() {
        return this.message;
    }
}
