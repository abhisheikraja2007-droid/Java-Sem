package Semester.exam.Java_Project.exception;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * A standardized JSON error response body returned by GlobalExceptionHandler.
 * Every API error will look identical so the frontend can handle them uniformly.
 *
 * Example response:
 * {
 *   "status": 400,
 *   "error": "Bad Request",
 *   "message": "Category ID must be a valid number",
 *   "timestamp": "2026-09-28T15:30:00",
 *   "fieldErrors": { "categoryId": "must not be null" }
 * }
 */
public class ErrorResponse {

    private int status;                     // HTTP status code, e.g. 400, 404, 500
    private String error;                   // Short category, e.g. "Bad Request"
    private String message;                 // Human-readable description
    private LocalDateTime timestamp;        // When the error occurred
    private Map<String, String> fieldErrors; // Field-level validation errors (null when not applicable)

    public ErrorResponse(int status, String error, String message) {
        this.status = status;
        this.error = error;
        this.message = message;
        this.timestamp = LocalDateTime.now();
    }

    public ErrorResponse(int status, String error, String message, Map<String, String> fieldErrors) {
        this(status, error, message);
        this.fieldErrors = fieldErrors;
    }

    // Getters — required by Jackson for JSON serialization
    public int getStatus()                    { return status; }
    public String getError()                  { return error; }
    public String getMessage()                { return message; }
    public LocalDateTime getTimestamp()       { return timestamp; }
    public Map<String, String> getFieldErrors() { return fieldErrors; }
}
