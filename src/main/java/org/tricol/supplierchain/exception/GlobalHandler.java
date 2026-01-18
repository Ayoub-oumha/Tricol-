package org.tricol.supplierchain.exception;


import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<HashMap<String, String>> handleValidationErrors(MethodArgumentNotValidException ex) {
        HashMap<String, String> errors = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach((error) -> {
            errors.put(error.getField(), error.getDefaultMessage());
        });
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errors);
    }



    @ExceptionHandler(DuplicateResourceException.class)
    public ResponseEntity<HashMap<String, String>> handleDuplicate(DuplicateResourceException ex) {
        HashMap<String, String> errors = new HashMap<>();
        errors.put("error", ex.getMessage());
        errors.put("status", "409");
        return ResponseEntity.status(HttpStatus.CONFLICT).body(errors);
    }

    @ExceptionHandler(OperationNotAllowedException.class)
    public ResponseEntity<HashMap<String, String>> handleOperationNotAllowed(OperationNotAllowedException ex) {
        HashMap<String, String> errors = new HashMap<>();
        errors.put("error", ex.getMessage());
        errors.put("status", "409");
        return ResponseEntity.status(HttpStatus.CONFLICT).body(errors);
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<HashMap<String, String>> handleResourceNotFound(ResourceNotFoundException ex) {
        HashMap<String, String> errors = new HashMap<>();
        errors.put("error", ex.getMessage());
        errors.put("status", "404");
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errors);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<Map<String, String>> handleInvalidDate(HttpMessageNotReadableException ex) {
        if (ex.getMessage().contains("DateTimeParseException")) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Date invalide. Vérifiez le jour du mois");
            error.put("status", "400");
            return ResponseEntity.badRequest().body(error);
        }
        return ResponseEntity.badRequest().build();
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<HashMap<String, String>> handleBadCredentials(BadCredentialsException ex) {
        HashMap<String, String> errors = new HashMap<>();
        errors.put("error", "Invalid username or password!");
        errors.put("status", "401");
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errors);
    }

    @ExceptionHandler(DisabledException.class)
    public ResponseEntity<HashMap<String, String>> handleDisabled(DisabledException ex) {
        HashMap<String, String> errors = new HashMap<>();
        errors.put("error", "User account is disabled");
        errors.put("status", "401");
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errors);
    }

    @ExceptionHandler(LockedException.class)
    public ResponseEntity<HashMap<String, String>> handleLocked(LockedException ex) {
        HashMap<String, String> errors = new HashMap<>();
        errors.put("error", "User account is locked");
        errors.put("status", "401");
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errors);
    }

    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<HashMap<String, String>> handleAuthentication(AuthenticationException ex) {
        HashMap<String, String> errors = new HashMap<>();
        errors.put("error", "Authentication failed");
        errors.put("status", "401");
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errors);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<HashMap<String, String>> handleAccessDenied(AccessDeniedException ex) {
        HashMap<String, String> errors = new HashMap<>();
        errors.put("error", "Access Denied - You don't have permission to access this resource");
        errors.put("status", "403");
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(errors);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<HashMap<String, String>> handleGlobal(Exception ex) {
        HashMap<String, String> errors = new HashMap<>();
        errors.put("error", ex.getMessage());
        errors.put("status", "500");
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errors);
    }

}



