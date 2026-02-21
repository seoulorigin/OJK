package com.seoulorigin.OJK.global.exception;

import com.seoulorigin.OJK.global.response.ErrorResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationException(MethodArgumentNotValidException exception) {
        FieldError firstFieldError = exception.getBindingResult().getFieldErrors().stream().findFirst().orElse(null);
        String message = firstFieldError != null
                ? firstFieldError.getDefaultMessage()
                : ErrorCode.INVALID_INPUT.getDefaultMessage();
        return ResponseEntity.status(ErrorCode.INVALID_INPUT.getStatus())
                .body(ErrorResponse.of(ErrorCode.INVALID_INPUT.getCode(), message));
    }

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ErrorResponse> handleBusinessException(BusinessException exception) {
        ErrorCode errorCode = exception.getErrorCode();
        return ResponseEntity.status(errorCode.getStatus())
                .body(ErrorResponse.of(errorCode.getCode(), exception.getMessage()));
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleIllegalArgumentException(IllegalArgumentException exception) {
        return ResponseEntity.status(ErrorCode.BAD_REQUEST.getStatus())
                .body(ErrorResponse.of(ErrorCode.BAD_REQUEST.getCode(), exception.getMessage()));
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ErrorResponse> handleAccessDeniedException(AccessDeniedException exception) {
        return ResponseEntity.status(ErrorCode.FORBIDDEN.getStatus())
                .body(ErrorResponse.of(ErrorCode.FORBIDDEN.getCode(), exception.getMessage()));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleException(Exception exception) {
        return ResponseEntity.status(ErrorCode.INTERNAL_SERVER_ERROR.getStatus())
                .body(ErrorResponse.of(
                        ErrorCode.INTERNAL_SERVER_ERROR.getCode(),
                        ErrorCode.INTERNAL_SERVER_ERROR.getDefaultMessage()
                ));
    }
}
