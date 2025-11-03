package com.blog.exceptions;

import org.apache.coyote.BadRequestException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

import jakarta.persistence.EntityNotFoundException;

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
        errRes.put("message", ex.getMessage());
        errRes.put("path", req.getDescription(false));
        return new ResponseEntity<>(errRes, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(InvalidPasswordException.class)
    public ResponseEntity<Map<String, Object>> HandleInvalidPassword(InvalidPasswordException ex, WebRequest req) {
        Map<String, Object> errRes = new HashMap<>();
        errRes.put("timestamp", LocalDateTime.now());
        errRes.put("status", HttpStatus.UNAUTHORIZED);
        errRes.put("message", ex.getMessage());
        errRes.put("path", req.getDescription(false));
        return new ResponseEntity<>(errRes, HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(UserAlreadyExistException.class)
    public ResponseEntity<Map<String, Object>> HandleUserAlreadyExist(UserAlreadyExistException ex, WebRequest req) {
        Map<String, Object> errRes = new HashMap<>();
        errRes.put("timestamp", LocalDateTime.now());
        errRes.put("status", HttpStatus.FORBIDDEN);
        errRes.put("message", ex.getMessage());
        errRes.put("path", req.getDescription(false));
        return new ResponseEntity<>(errRes, HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(ErrSavingException.class)
    public ResponseEntity<Map<String, Object>> HandleErrSavingException(ErrSavingException ex, WebRequest req) {
        Map<String, Object> errRes = new HashMap<>();
        errRes.put("timestamp", LocalDateTime.now());
        errRes.put("status", HttpStatus.INTERNAL_SERVER_ERROR);
        errRes.put("message", ex.getMessage());
        errRes.put("path", req.getDescription(false));
        return new ResponseEntity<>(errRes, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(ContentEmptyException.class)
    public ResponseEntity<Map<String, Object>> HandleContentEmptyException(ContentEmptyException ex, WebRequest req) {
        Map<String, Object> errRes = new HashMap<>();
        errRes.put("timestamp", LocalDateTime.now());
        errRes.put("status", HttpStatus.FORBIDDEN);
        errRes.put("message", ex.getMessage());
        errRes.put("path", req.getDescription(false));
        return new ResponseEntity<>(errRes, HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(TitleEmptyException.class)
    public ResponseEntity<Map<String, Object>> HandleTitleEmptyException(TitleEmptyException ex, WebRequest req) {
        Map<String, Object> errRes = new HashMap<>();
        errRes.put("timestamp", LocalDateTime.now());
        errRes.put("status", HttpStatus.FORBIDDEN);
        errRes.put("message", ex.getMessage());
        errRes.put("path", req.getDescription(false));
        return new ResponseEntity<>(errRes, HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(PostNotFoundException.class)
    public ResponseEntity<Map<String, Object>> HandlePostNotFoundException(PostNotFoundException ex, WebRequest req) {
        Map<String, Object> errRes = new HashMap<>();
        errRes.put("timestamp", LocalDateTime.now());
        errRes.put("status", HttpStatus.NOT_FOUND);
        errRes.put("message", ex.getMessage());
        errRes.put("path", req.getDescription(false));
        return new ResponseEntity<>(errRes, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<Map<String, Object>> HandleIllegalStateException(IllegalStateException ex, WebRequest req) {
        Map<String, Object> errRes = new HashMap<>();
        errRes.put("timestamp", LocalDateTime.now());
        errRes.put("status", HttpStatus.INTERNAL_SERVER_ERROR);
        errRes.put("message", ex.getMessage());
        errRes.put("path", req.getDescription(false));
        return new ResponseEntity<>(errRes, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<Map<String, Object>> HandleRuntimeException(RuntimeException ex, WebRequest req) {
        Map<String, Object> errRes = new HashMap<>();
        errRes.put("timestamp", LocalDateTime.now());
        errRes.put("status", HttpStatus.INTERNAL_SERVER_ERROR);
        errRes.put("message", ex.getMessage());
        errRes.put("path", req.getDescription(false));
        return new ResponseEntity<>(errRes, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, Object>> HandleIllegalArgumentException(IllegalArgumentException ex,
            WebRequest req) {
        Map<String, Object> errRes = new HashMap<>();
        errRes.put("timestamp", LocalDateTime.now());
        errRes.put("status", HttpStatus.BAD_REQUEST);
        errRes.put("error", "Validation Error");
        errRes.put("message", ex.getMessage());
        errRes.put("path", req.getDescription(false).replace("uri=", ""));
        return new ResponseEntity<>(errRes, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(CreateCommentException.class)
    public ResponseEntity<Map<String, Object>> HandleCreateCommentException(CreateCommentException ex,
            WebRequest req) {
        Map<String, Object> errRes = new HashMap<>();
        errRes.put("timestamp", LocalDateTime.now());
        errRes.put("status", HttpStatus.NOT_FOUND);
        errRes.put("message", ex.getMessage());
        errRes.put("path", req.getDescription(false));
        return new ResponseEntity<>(errRes, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(LikeException.class)
    public ResponseEntity<Map<String, Object>> HandleLikeException(LikeException ex,
            WebRequest req) {
        Map<String, Object> errRes = new HashMap<>();
        errRes.put("timestamp", LocalDateTime.now());
        errRes.put("status", HttpStatus.NOT_FOUND);
        errRes.put("message", ex.getMessage());
        errRes.put("path", req.getDescription(false));
        return new ResponseEntity<>(errRes, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(UserNotLoginException.class)
    public ResponseEntity<Map<String, Object>> HandleUserNotLoginException(UserNotLoginException ex,
            WebRequest req) {
        Map<String, Object> errRes = new HashMap<>();
        errRes.put("timestamp", LocalDateTime.now());
        errRes.put("status", HttpStatus.FORBIDDEN);
        errRes.put("message", ex.getMessage());
        errRes.put("path", req.getDescription(false));
        return new ResponseEntity<>(errRes, HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(FollowException.class)
    public ResponseEntity<Map<String, Object>> HandleFollowException(FollowException ex,
            WebRequest req) {
        Map<String, Object> errRes = new HashMap<>();
        errRes.put("timestamp", LocalDateTime.now());
        errRes.put("status", HttpStatus.FORBIDDEN);
        errRes.put("message", ex.getMessage());
        errRes.put("path", req.getDescription(false));
        return new ResponseEntity<>(errRes, HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(ReportException.class)
    public ResponseEntity<Map<String, Object>> HandleReportException(ReportException ex,
            WebRequest req) {
        Map<String, Object> errRes = new HashMap<>();
        errRes.put("timestamp", LocalDateTime.now());
        errRes.put("status", HttpStatus.FORBIDDEN);
        errRes.put("message", ex.getMessage());
        errRes.put("path", req.getDescription(false));
        return new ResponseEntity<>(errRes, HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(UserBannedException.class)
    public ResponseEntity<Map<String, Object>> HandleUserBannedException(UserBannedException ex,
            WebRequest req) {
        Map<String, Object> errRes = new HashMap<>();
        errRes.put("timestamp", LocalDateTime.now());
        errRes.put("status", HttpStatus.UNAUTHORIZED);
        errRes.put("message", ex.getMessage());
        errRes.put("path", req.getDescription(false));
        return new ResponseEntity<>(errRes, HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(BanException.class)
    public ResponseEntity<Map<String, Object>> HandleBanException(BanException ex,
            WebRequest req) {
        Map<String, Object> errRes = new HashMap<>();
        errRes.put("timestamp", LocalDateTime.now());
        errRes.put("status", HttpStatus.FORBIDDEN);
        errRes.put("message", ex.getMessage());
        errRes.put("path", req.getDescription(false));
        return new ResponseEntity<>(errRes, HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(DeleteException.class)
    public ResponseEntity<Map<String, Object>> HandleDeleteException(DeleteException ex,
            WebRequest req) {
        Map<String, Object> errRes = new HashMap<>();
        errRes.put("timestamp", LocalDateTime.now());
        errRes.put("status", HttpStatus.FORBIDDEN);
        errRes.put("message", ex.getMessage());
        errRes.put("path", req.getDescription(false));
        return new ResponseEntity<>(errRes, HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<Map<String, Object>> HandleEntityNotFoundException(EntityNotFoundException ex,
            WebRequest req) {
        Map<String, Object> errRes = new HashMap<>();
        errRes.put("timestamp", LocalDateTime.now());
        errRes.put("status", HttpStatus.FORBIDDEN);
        errRes.put("message", ex.getMessage());
        errRes.put("path", req.getDescription(false));
        return new ResponseEntity<>(errRes, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(SecurityException.class)
    public ResponseEntity<Map<String, Object>> HandleSecurityException(SecurityException ex,
            WebRequest req) {
        Map<String, Object> errRes = new HashMap<>();
        errRes.put("timestamp", LocalDateTime.now());
        errRes.put("status", HttpStatus.FORBIDDEN);
        errRes.put("message", ex.getMessage());
        errRes.put("path", req.getDescription(false));
        return new ResponseEntity<>(errRes, HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public ResponseEntity<Map<String, Object>> HandleMaxUploadSizeExceededException(MaxUploadSizeExceededException ex,
            WebRequest req) {
        Map<String, Object> errRes = new HashMap<>();
        errRes.put("timestamp", LocalDateTime.now());
        errRes.put("status", HttpStatus.FORBIDDEN);
        errRes.put("message", ex.getMessage());
        errRes.put("path", req.getDescription(false));
        return new ResponseEntity<>(errRes, HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<Map<String, Object>> HandleBadRequestException(BadRequestException ex,
            WebRequest req) {
        Map<String, Object> errRes = new HashMap<>();
        errRes.put("timestamp", LocalDateTime.now());
        errRes.put("status", HttpStatus.BAD_REQUEST);
        errRes.put("message", ex.getMessage());
        errRes.put("path", req.getDescription(false));
        return new ResponseEntity<>(errRes, HttpStatus.BAD_REQUEST);
    }
}
