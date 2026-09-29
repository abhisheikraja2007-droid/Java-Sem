package Semester.exam.Java_Project.DTO;

// Jakarta Validation constraint ensuring field is not null and not empty
import jakarta.validation.constraints.NotBlank;

/**
 * Data Transfer Object for incoming login requests.
 * Carries citizen/officer credentials from frontend to AuthController.
 */
public class LoginRequestDTO {

    // Ensures the client sends a non-blank username
    @NotBlank(message = "Username is required")
    private String username;

    // Ensures the client sends a non-blank password
    @NotBlank(message = "Password is required")
    private String password;

    // Getters and Setters for Jackson deserialization
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
}
