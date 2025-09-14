package com.blog.exceptions;

public class ContentEmptyException extends RuntimeException {
    public ContentEmptyException(String message) {
        super(message);
    }
}
