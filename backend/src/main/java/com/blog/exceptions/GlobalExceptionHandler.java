package com.blog.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleUserNotFoundException(UserNotFoundException ex, WebRequest req) {
        Map<String, Object> errRes = new HashMap<>();
        errRes.put("timestamp", LocalDateTime.now());
        errRes.put("status", HttpStatus.NOT_FOUND);
        errRes.put("err", "User Not Found");
        errRes.put("message", ex.getMessage());
        errRes.put("path", req.getDescription(false));
        return new ResponseEntity<>(errRes, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(InvalidPasswordException.class)
    public ResponseEntity<Map<String, Object>> HandleInvalidPassword(InvalidPasswordException ex, WebRequest req) {
        Map<String, Object> errRes = new HashMap<>();
        errRes.put("timestamp", LocalDateTime.now());
        errRes.put("status", HttpStatus.UNAUTHORIZED);
        errRes.put("err", "Invalid Password");
        errRes.put("message", ex.getMessage());
        errRes.put("path", req.getDescription(false));
        return new ResponseEntity<>(errRes, HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(UserAlreadyExistException.class)
    public ResponseEntity<Map<String, Object>> HandleUserAlreadyExist(UserAlreadyExistException ex, WebRequest req) {
        Map<String, Object> errRes = new HashMap<>();
        errRes.put("timestamp", LocalDateTime.now());
        errRes.put("status", HttpStatus.FORBIDDEN);
        errRes.put("err", "User Already Exist");
        errRes.put("message", ex.getMessage());
        errRes.put("path", req.getDescription(false));
        return new ResponseEntity<>(errRes, HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(ErrSavingException.class)
    public ResponseEntity<Map<String, Object>> HandleErrSavingException(ErrSavingException ex, WebRequest req) {
        Map<String, Object> errRes = new HashMap<>();
        errRes.put("timestamp", LocalDateTime.now());
        errRes.put("status", HttpStatus.INTERNAL_SERVER_ERROR);
        errRes.put("err", "Error");
        errRes.put("message", ex.getMessage());
        errRes.put("path", req.getDescription(false));
        return new ResponseEntity<>(errRes, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(ContentEmptyException.class)
    public ResponseEntity<Map<String, Object>> HandleContentEmptyException(ContentEmptyException ex, WebRequest req) {
        Map<String, Object> errRes = new HashMap<>();
        errRes.put("timestamp", LocalDateTime.now());
        errRes.put("status", HttpStatus.FORBIDDEN);
        errRes.put("err", "Content must not be empty");
        errRes.put("message", ex.getMessage());
        errRes.put("path", req.getDescription(false));
        return new ResponseEntity<>(errRes, HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(TitleEmptyException.class)
    public ResponseEntity<Map<String, Object>> HandleTitleEmptyException(TitleEmptyException ex, WebRequest req) {
        Map<String, Object> errRes = new HashMap<>();
        errRes.put("timestamp", LocalDateTime.now());
        errRes.put("status", HttpStatus.FORBIDDEN);
        errRes.put("err", "Title must not be empty");
        errRes.put("message", ex.getMessage());
        errRes.put("path", req.getDescription(false));
        return new ResponseEntity<>(errRes, HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(PostNotFoundException.class)
    public ResponseEntity<Map<String, Object>> HandlePostNotFoundException(PostNotFoundException ex, WebRequest req) {
        Map<String, Object> errRes = new HashMap<>();
        errRes.put("timestamp", LocalDateTime.now());
        errRes.put("status", HttpStatus.NOT_FOUND);
        errRes.put("err", "Post not found");
        errRes.put("message", ex.getMessage());
        errRes.put("path", req.getDescription(false));
        return new ResponseEntity<>(errRes, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<Map<String, Object>> HandleIllegalStateException(IllegalStateException ex, WebRequest req) {
        Map<String, Object> errRes = new HashMap<>();
        errRes.put("timestamp", LocalDateTime.now());
        errRes.put("status", HttpStatus.INTERNAL_SERVER_ERROR);
        errRes.put("err", ex.getMessage());
        errRes.put("message", ex.getMessage());
        errRes.put("path", req.getDescription(false));
        return new ResponseEntity<>(errRes, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<Map<String, Object>> HandleRuntimeException(RuntimeException ex, WebRequest req) {
        Map<String, Object> errRes = new HashMap<>();
        errRes.put("timestamp", LocalDateTime.now());
        errRes.put("status", HttpStatus.INTERNAL_SERVER_ERROR);
        errRes.put("err", ex.getMessage());
        errRes.put("message", ex.getMessage());
        errRes.put("path", req.getDescription(false));
        return new ResponseEntity<>(errRes, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
