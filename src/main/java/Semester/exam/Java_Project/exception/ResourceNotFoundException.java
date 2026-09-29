package Semester.exam.Java_Project.exception;

/**
 * Custom runtime exception thrown when a requested database resource 
 * (such as a Grievance, Category, or User) cannot be found by its identifier.
 * 
 * Handled globally by GlobalExceptionHandler to return a clean HTTP 404 Not Found response.
 */
public class ResourceNotFoundException extends RuntimeException {
    
    /**
     * Constructor accepting a detailed error message.
     * @param message Explanatory reason (e.g., "Category with ID 99 not found")
     */
    public ResourceNotFoundException(String message) {
        super(message); // Passes message to standard Java RuntimeException
    }
}