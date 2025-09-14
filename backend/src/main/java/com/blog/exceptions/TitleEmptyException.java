package com.blog.exceptions;

public class TitleEmptyException extends RuntimeException {
    public TitleEmptyException(String message) {
        super(message);
    }
}
