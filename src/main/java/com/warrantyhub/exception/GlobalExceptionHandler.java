package com.warrantyhub.exception;

import jakarta.validation.ConstraintViolationException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

  // Handle specific custom exceptions
  @ExceptionHandler(ResourceNotFoundException.class)
  public ResponseEntity<?> handleResourceNotFoundException(ResourceNotFoundException ex, WebRequest request) {
    ErrorDetails errorDetails = new ErrorDetails(
            new Date(),
            ex.getMessage(),
            request.getDescription(false),
            HttpStatus.NOT_FOUND.value());

    return new ResponseEntity<>(errorDetails, HttpStatus.NOT_FOUND);
  }

  @ExceptionHandler(BadRequestException.class)
  public ResponseEntity<?> handleBadRequestException(BadRequestException ex, WebRequest request) {
    ErrorDetails errorDetails = new ErrorDetails(
            new Date(),
            ex.getMessage(),
            request.getDescription(false),
            HttpStatus.BAD_REQUEST.value());

    return new ResponseEntity<>(errorDetails, HttpStatus.BAD_REQUEST);
  }

  @ExceptionHandler(UnauthorizedException.class)
  public ResponseEntity<?> handleUnauthorizedException(UnauthorizedException ex, WebRequest request) {
    ErrorDetails errorDetails = new ErrorDetails(
            new Date(),
            ex.getMessage(),
            request.getDescription(false),
            HttpStatus.UNAUTHORIZED.value());

    return new ResponseEntity<>(errorDetails, HttpStatus.UNAUTHORIZED);
  }

  @ExceptionHandler(FileStorageException.class)
  public ResponseEntity<?> handleFileStorageException(FileStorageException ex, WebRequest request) {
    ErrorDetails errorDetails = new ErrorDetails(
            new Date(),
            ex.getMessage(),
            request.getDescription(false),
            HttpStatus.INTERNAL_SERVER_ERROR.value());

    return new ResponseEntity<>(errorDetails, HttpStatus.INTERNAL_SERVER_ERROR);
  }

  // Handle Spring Security related exceptions
  @ExceptionHandler(BadCredentialsException.class)
  public ResponseEntity<?> handleBadCredentialsException(BadCredentialsException ex, WebRequest request) {
    ErrorDetails errorDetails = new ErrorDetails(
            new Date(),
            "Invalid username or password",
            request.getDescription(false),
            HttpStatus.UNAUTHORIZED.value());

    return new ResponseEntity<>(errorDetails, HttpStatus.UNAUTHORIZED);
  }

  @ExceptionHandler(AuthenticationException.class)
  public ResponseEntity<?> handleAuthenticationException(AuthenticationException ex, WebRequest request) {
    ErrorDetails errorDetails = new ErrorDetails(
            new Date(),
            "Authentication failed",
            request.getDescription(false),
            HttpStatus.UNAUTHORIZED.value());

    return new ResponseEntity<>(errorDetails, HttpStatus.UNAUTHORIZED);
  }

  @ExceptionHandler(AccessDeniedException.class)
  public ResponseEntity<?> handleAccessDeniedException(AccessDeniedException ex, WebRequest request) {
    ErrorDetails errorDetails = new ErrorDetails(
            new Date(),
            "Access denied",
            request.getDescription(false),
            HttpStatus.FORBIDDEN.value());

    return new ResponseEntity<>(errorDetails, HttpStatus.FORBIDDEN);
  }

  // Handle validation exceptions
  @Override
  protected ResponseEntity<Object> handleMethodArgumentNotValid(
          MethodArgumentNotValidException ex,
          HttpHeaders headers,
          HttpStatusCode status,
          WebRequest request) {

    Map<String, String> errors = new HashMap<>();
    ex.getBindingResult().getAllErrors().forEach((error) -> {
      String fieldName = ((FieldError) error).getField();
      String errorMessage = error.getDefaultMessage();
      errors.put(fieldName, errorMessage);
    });

    ValidationErrorDetails errorDetails = new ValidationErrorDetails(
            new Date(),
            "Validation failed",
            request.getDescription(false),
            HttpStatus.BAD_REQUEST.value(),
            errors);

    return new ResponseEntity<>(errorDetails, status);
  }

  @ExceptionHandler(ConstraintViolationException.class)
  public ResponseEntity<?> handleConstraintViolationException(ConstraintViolationException ex, WebRequest request) {
    Map<String, String> errors = new HashMap<>();
    ex.getConstraintViolations().forEach(violation -> {
      String fieldName = violation.getPropertyPath().toString();
      String errorMessage = violation.getMessage();
      errors.put(fieldName, errorMessage);
    });

    ValidationErrorDetails errorDetails = new ValidationErrorDetails(
            new Date(),
            "Validation failed",
            request.getDescription(false),
            HttpStatus.BAD_REQUEST.value(),
            errors);

    return new ResponseEntity<>(errorDetails, HttpStatus.BAD_REQUEST);
  }

  // Handle missing request parameters
  @Override
  protected ResponseEntity<Object> handleMissingServletRequestParameter(
          MissingServletRequestParameterException ex,
          HttpHeaders headers,
          HttpStatusCode status,
          WebRequest request) {

    ErrorDetails errorDetails = new ErrorDetails(
            new Date(),
            "Required parameter '" + ex.getParameterName() + "' is missing",
            request.getDescription(false),
            HttpStatus.BAD_REQUEST.value());

    return new ResponseEntity<>(errorDetails, status);
  }

  // Handle all other exceptions
  @ExceptionHandler(Exception.class)
  public ResponseEntity<?> handleGlobalException(Exception ex, WebRequest request) {
    ErrorDetails errorDetails = new ErrorDetails(
            new Date(),
            "An unexpected error occurred",
            request.getDescription(false),
            HttpStatus.INTERNAL_SERVER_ERROR.value());

    // Log the exception for debugging
    ex.printStackTrace();

    return new ResponseEntity<>(errorDetails, HttpStatus.INTERNAL_SERVER_ERROR);
  }
}

