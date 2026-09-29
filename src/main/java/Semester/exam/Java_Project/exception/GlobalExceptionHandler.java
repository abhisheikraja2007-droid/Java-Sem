package Semester.exam.Java_Project.exception;

import jakarta.persistence.EntityNotFoundException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.NoHandlerFoundException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.util.HashMap;
import java.util.Map;

// Central place to catch errors across all controllers so the API always returns a clean JSON error response
@RestControllerAdvice
public class GlobalExceptionHandler {

    // 404: when an ID isn't found in the database (e.g. invalid grievance or category ID)
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleResourceNotFound(ResourceNotFoundException ex) {
        return build(HttpStatus.NOT_FOUND, "Not Found", ex.getMessage());
    }

    // 400: when @Valid fails on a request body (e.g. blank description, invalid category ID)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidation(MethodArgumentNotValidException ex) {
        Map<String, String> fieldErrors = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(err ->
                fieldErrors.put(err.getField(), err.getDefaultMessage()));

        ErrorResponse body = new ErrorResponse(
                HttpStatus.BAD_REQUEST.value(),
                "Validation Failed",
                "Please fix the validation errors in the fields below.",
                fieldErrors
        );
        return ResponseEntity.badRequest().body(body);
    }

    // 400: wrong data type in URL (e.g. passing "abc" for an integer/Long ID)
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ErrorResponse> handleTypeMismatch(MethodArgumentTypeMismatchException ex) {
        String expectedType = ex.getRequiredType() != null ? ex.getRequiredType().getSimpleName() : "unknown";
        String message = String.format("Parameter '%s' should be a %s, but got: '%s'",
                ex.getName(), expectedType, ex.getValue());
        return build(HttpStatus.BAD_REQUEST, "Bad Request", message);
    }

    // 400: query parameter is missing (e.g. forgot ?status= in PUT /status)
    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<ErrorResponse> handleMissingParam(MissingServletRequestParameterException ex) {
        String message = String.format("Missing required parameter: '%s' (%s)",
                ex.getParameterName(), ex.getParameterType());
        return build(HttpStatus.BAD_REQUEST, "Bad Request", message);
    }

    // 400: malformed JSON or empty body sent to POST/PUT
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorResponse> handleUnreadableBody(HttpMessageNotReadableException ex) {
        return build(HttpStatus.BAD_REQUEST, "Bad Request",
                "Invalid JSON request body. Please verify your JSON syntax and Content-Type header.");
    }

    // 400: failed string-to-number parsing
    @ExceptionHandler(NumberFormatException.class)
    public ResponseEntity<ErrorResponse> handleNumberFormat(NumberFormatException ex) {
        return build(HttpStatus.BAD_REQUEST, "Bad Request",
                "Expected a number but got an invalid format: " + ex.getMessage());
    }

    // 400: bad arguments passed to a service method (e.g. rating < 1 or > 5)
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleIllegalArgument(IllegalArgumentException ex) {
        return build(HttpStatus.BAD_REQUEST, "Bad Request", ex.getMessage());
    }

    // 422: business rule violated (e.g. trying to rate a grievance before it is RESOLVED)
    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<ErrorResponse> handleIllegalState(IllegalStateException ex) {
        return build(HttpStatus.UNPROCESSABLE_ENTITY, "Business Rule Violation", ex.getMessage());
    }

    // 404: route doesn't exist
    @ExceptionHandler(NoHandlerFoundException.class)
    public ResponseEntity<ErrorResponse> handleNoHandler(NoHandlerFoundException ex) {
        return build(HttpStatus.NOT_FOUND, "Not Found",
                "No endpoint found for: " + ex.getHttpMethod() + " " + ex.getRequestURL());
    }

    // 404: Spring Boot 3 static or controller resource not found
    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<ErrorResponse> handleNoResource(NoResourceFoundException ex) {
        return build(HttpStatus.NOT_FOUND, "Not Found",
                "No resource or endpoint found for: " + ex.getHttpMethod() + " " + ex.getResourcePath());
    }

    // 405: used wrong HTTP method (e.g. calling GET on a POST-only route)
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<ErrorResponse> handleMethodNotAllowed(HttpRequestMethodNotSupportedException ex) {
        return build(HttpStatus.METHOD_NOT_ALLOWED, "Method Not Allowed",
                "HTTP method " + ex.getMethod() + " is not allowed here. Supported: " + ex.getSupportedHttpMethods());
    }

    // 415: wrong Content-Type header
    @ExceptionHandler(HttpMediaTypeNotSupportedException.class)
    public ResponseEntity<ErrorResponse> handleUnsupportedMediaType(HttpMediaTypeNotSupportedException ex) {
        return build(HttpStatus.UNSUPPORTED_MEDIA_TYPE, "Unsupported Media Type",
                "Content-Type '" + ex.getContentType() + "' is not supported. Use application/json.");
    }

    // 404: JPA entity not found
    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleEntityNotFound(EntityNotFoundException ex) {
        return build(HttpStatus.NOT_FOUND, "Not Found", "Requested record was not found in the database.");
    }

    // 409: database unique constraint violation (e.g. duplicate username)
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ErrorResponse> handleDataIntegrity(DataIntegrityViolationException ex) {
        return build(HttpStatus.CONFLICT, "Data Conflict",
                "Database constraint violated. Check for duplicate entries or missing required foreign keys.");
    }

    // 500: catch accidental null pointers
    @ExceptionHandler(NullPointerException.class)
    public ResponseEntity<ErrorResponse> handleNullPointer(NullPointerException ex) {
        return build(HttpStatus.INTERNAL_SERVER_ERROR, "Internal Server Error",
                "A null pointer occurred. Please report this issue to support.");
    }

    // 500: math errors like division by zero
    @ExceptionHandler(ArithmeticException.class)
    public ResponseEntity<ErrorResponse> handleArithmetic(ArithmeticException ex) {
        return build(HttpStatus.INTERNAL_SERVER_ERROR, "Internal Server Error", ex.getMessage());
    }

    // 500: ultimate fallback for anything uncaught
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleAll(Exception ex) {
        return build(HttpStatus.INTERNAL_SERVER_ERROR, "Internal Server Error",
                "An unexpected server error occurred. Please try again later.");
    }

    // Small helper to keep response construction clean
    private ResponseEntity<ErrorResponse> build(HttpStatus status, String error, String message) {
        ErrorResponse body = new ErrorResponse(status.value(), error, message);
        return new ResponseEntity<>(body, status);
    }
}