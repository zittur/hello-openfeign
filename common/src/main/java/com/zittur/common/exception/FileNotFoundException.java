package com.zittur.common.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.NOT_FOUND)
public class FileNotFoundException extends BaseException {

    private int code;

    private String message;

    public FileNotFoundException(String message) {
        super(HttpStatus.NOT_FOUND.value(), message);
    }
}
