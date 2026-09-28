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
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.NoHandlerFoundException;

import java.util.HashMap;
import java.util.Map;

/**
 * ============================================================
 *  GLOBAL EXCEPTION HANDLER
 * ============================================================
 * Catches ALL exceptions thrown anywhere in the application
 * and converts them into clean, structured JSON responses.
 *
 * Standard response shape:
 * {
 *   "status":  400,
 *   "error":   "Bad Request",
 *   "message": "Human-readable reason",
 *   "timestamp": "2026-09-28T15:30:00",
 *   "fieldErrors": { ... }   <-- only present for validation errors
 * }
 * ============================================================
 */
@ControllerAdvice
public class GlobalExceptionHandler {

    // -------------------------------------------------------
    // 1. OUR OWN CUSTOM EXCEPTIONS
    // -------------------------------------------------------

    /**
     * Thrown when a DB record is not found (e.g. grievance ID does not exist).
     * HTTP 404 Not Found
     */
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleResourceNotFound(ResourceNotFoundException ex) {
        return build(HttpStatus.NOT_FOUND, "Not Found", ex.getMessage());
    }

    // -------------------------------------------------------
    // 2. INPUT / REQUEST VALIDATION EXCEPTIONS
    // -------------------------------------------------------

    /**
     * Thrown when @Valid fails on a @RequestBody DTO.
     * Example: sending "" for a @NotBlank field.
     * HTTP 400 Bad Request — with per-field error map.
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidation(MethodArgumentNotValidException ex) {
        Map<String, String> fieldErrors = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(err ->
                fieldErrors.put(err.getField(), err.getDefaultMessage()));

        ErrorResponse body = new ErrorResponse(
                HttpStatus.BAD_REQUEST.value(),
                "Validation Failed",
                "One or more fields failed validation. See 'fieldErrors' for details.",
                fieldErrors
        );
        return ResponseEntity.badRequest().body(body);
    }

    /**
     * Thrown when a @PathVariable or @RequestParam has the WRONG TYPE.
     * Example: /api/grievances/abc  →  "abc" cannot be parsed as Long.
     * HTTP 400 Bad Request
     */
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ErrorResponse> handleTypeMismatch(MethodArgumentTypeMismatchException ex) {
        String expectedType = ex.getRequiredType() != null ? ex.getRequiredType().getSimpleName() : "unknown";
        String message = String.format(
                "Parameter '%s' must be a valid %s. Received value: '%s'",
                ex.getName(), expectedType, ex.getValue()
        );
        return build(HttpStatus.BAD_REQUEST, "Bad Request", message);
    }

    /**
     * Thrown when a required @RequestParam is completely missing from the URL.
     * Example: PUT /api/grievances/1/status  (without ?status=...)
     * HTTP 400 Bad Request
     */
    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<ErrorResponse> handleMissingParam(MissingServletRequestParameterException ex) {
        String message = String.format(
                "Required query parameter '%s' (type: %s) is missing from the request.",
                ex.getParameterName(), ex.getParameterType()
        );
        return build(HttpStatus.BAD_REQUEST, "Bad Request", message);
    }

    /**
     * Thrown when the JSON request body cannot be parsed at all.
     * Example: sending plain text instead of JSON, or malformed JSON like {name:}
     * HTTP 400 Bad Request
     */
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorResponse> handleUnreadableBody(HttpMessageNotReadableException ex) {
        return build(HttpStatus.BAD_REQUEST, "Bad Request",
                "Request body is missing or contains invalid JSON. " +
                "Please send a valid JSON payload with the Content-Type: application/json header.");
    }

    /**
     * Thrown when a String cannot be parsed to a Number manually.
     * Example: Integer.parseInt("abc")
     * HTTP 400 Bad Request
     */
    @ExceptionHandler(NumberFormatException.class)
    public ResponseEntity<ErrorResponse> handleNumberFormat(NumberFormatException ex) {
        return build(HttpStatus.BAD_REQUEST, "Bad Request",
                "A numeric value was expected but received an invalid input: " + ex.getMessage());
    }

    /**
     * Thrown when a method receives an illegal argument.
     * Example: submitRating() with rating = -1 or rating = 99
     * HTTP 400 Bad Request
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleIllegalArgument(IllegalArgumentException ex) {
        return build(HttpStatus.BAD_REQUEST, "Bad Request", ex.getMessage());
    }

    /**
     * Thrown when business logic rules are violated.
     * Example: trying to rate a grievance that is not yet RESOLVED.
     * HTTP 422 Unprocessable Entity
     */
    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<ErrorResponse> handleIllegalState(IllegalStateException ex) {
        return build(HttpStatus.UNPROCESSABLE_ENTITY, "Business Rule Violation", ex.getMessage());
    }

    // -------------------------------------------------------
    // 3. HTTP PROTOCOL / ROUTING EXCEPTIONS
    // -------------------------------------------------------

    /**
     * Thrown when the request URL does not match any controller endpoint.
     * Example: GET /api/nonexistent
     * HTTP 404 Not Found
     *
     * NOTE: Requires spring.mvc.throw-exception-if-no-handler-found=true
     *       and spring.web.resources.add-mappings=false in application.properties
     */
    @ExceptionHandler(NoHandlerFoundException.class)
    public ResponseEntity<ErrorResponse> handleNoHandler(NoHandlerFoundException ex) {
        return build(HttpStatus.NOT_FOUND, "Not Found",
                "No endpoint found for: " + ex.getHttpMethod() + " " + ex.getRequestURL());
    }

    /**
     * Thrown when the HTTP method is wrong for an endpoint.
     * Example: GET /api/grievances/1/rate  (should be POST)
     * HTTP 405 Method Not Allowed
     */
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<ErrorResponse> handleMethodNotAllowed(HttpRequestMethodNotSupportedException ex) {
        return build(HttpStatus.METHOD_NOT_ALLOWED, "Method Not Allowed",
                "HTTP method '" + ex.getMethod() + "' is not supported for this endpoint. " +
                "Supported methods: " + ex.getSupportedHttpMethods());
    }

    /**
     * Thrown when the Content-Type header is wrong.
     * Example: sending form-data to an endpoint that expects application/json
     * HTTP 415 Unsupported Media Type
     */
    @ExceptionHandler(HttpMediaTypeNotSupportedException.class)
    public ResponseEntity<ErrorResponse> handleUnsupportedMediaType(HttpMediaTypeNotSupportedException ex) {
        return build(HttpStatus.UNSUPPORTED_MEDIA_TYPE, "Unsupported Media Type",
                "Content-Type '" + ex.getContentType() + "' is not supported. " +
                "Please use: application/json");
    }

    // -------------------------------------------------------
    // 4. DATABASE / PERSISTENCE EXCEPTIONS
    // -------------------------------------------------------

    /**
     * Thrown by JPA when an entity is not found via EntityManager.
     * (Similar to ResourceNotFoundException but from the JPA layer.)
     * HTTP 404 Not Found
     */
    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleEntityNotFound(EntityNotFoundException ex) {
        return build(HttpStatus.NOT_FOUND, "Not Found",
                "The requested record does not exist in the database.");
    }

    /**
     * Thrown when a database constraint is violated.
     * Example: inserting a duplicate unique field, or a NOT NULL column gets null.
     * HTTP 409 Conflict
     */
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ErrorResponse> handleDataIntegrity(DataIntegrityViolationException ex) {
        return build(HttpStatus.CONFLICT, "Data Conflict",
                "The request conflicts with existing data. " +
                "This may be caused by a duplicate entry or a violated database constraint.");
    }

    // -------------------------------------------------------
    // 5. UNEXPECTED / PROGRAMMING ERRORS
    // -------------------------------------------------------

    /**
     * Thrown when code tries to use a null reference.
     * This should never reach the user — it indicates a bug.
     * HTTP 500 Internal Server Error
     */
    @ExceptionHandler(NullPointerException.class)
    public ResponseEntity<ErrorResponse> handleNullPointer(NullPointerException ex) {
        return build(HttpStatus.INTERNAL_SERVER_ERROR, "Internal Server Error",
                "An internal error occurred (null reference). Please report this to the administrator.");
    }

    /**
     * Thrown on arithmetic errors like division by zero.
     * HTTP 500 Internal Server Error
     */
    @ExceptionHandler(ArithmeticException.class)
    public ResponseEntity<ErrorResponse> handleArithmetic(ArithmeticException ex) {
        return build(HttpStatus.INTERNAL_SERVER_ERROR, "Internal Server Error",
                "An arithmetic error occurred: " + ex.getMessage());
    }

    /**
     * CATCH-ALL: Handles any exception not explicitly caught above.
     * HTTP 500 Internal Server Error
     *
     * This is the safety net — every possible exception will end up here
     * if it doesn't match a more specific handler above.
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleAll(Exception ex) {
        return build(HttpStatus.INTERNAL_SERVER_ERROR, "Internal Server Error",
                "An unexpected error occurred. Please try again later.");
    }

    // -------------------------------------------------------
    // PRIVATE HELPER — avoids repeating new ErrorResponse(...)
    // -------------------------------------------------------
    private ResponseEntity<ErrorResponse> build(HttpStatus status, String error, String message) {
        ErrorResponse body = new ErrorResponse(status.value(), error, message);
        return new ResponseEntity<>(body, status);
    }
}