package Semester.exam.Java_Project.exception;

// A custom exception to throw when a database record isn't found
public class ResourceNotFoundException extends RuntimeException {
    public ResourceNotFoundException(String message) {
        super(message);
    }
}