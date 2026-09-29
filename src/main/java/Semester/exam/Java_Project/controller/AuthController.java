package Semester.exam.Java_Project.controller;

import Semester.exam.Java_Project.DTO.LoginRequestDTO;
import Semester.exam.Java_Project.DTO.LoginResponseDTO;
import Semester.exam.Java_Project.entity.User;
import Semester.exam.Java_Project.repository.UserRepository;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * REST Controller handling user authentication (Login).
 * Verifies username and password and returns user profile and role details.
 */
@RestController // Marks this as a REST controller returning JSON
@RequestMapping("/api/auth") // Base routing path for authentication endpoints
public class AuthController {

    // Repository for looking up user credentials in the database
    private final UserRepository userRepository;

    // Constructor injection
    public AuthController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * POST /api/auth/login
     * Validates credentials and logs the citizen or officer into the system.
     * 
     * @param request Validated JSON containing username and password
     * @return 200 OK with LoginResponseDTO if valid, or 401 UNAUTHORIZED if invalid
     */
    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequestDTO request) {
        // Clean and trim incoming username and password inputs
        String cleanUsername = request.getUsername() != null ? request.getUsername().trim() : "";
        String password = request.getPassword() != null ? request.getPassword() : "";

        // Query database for user by username
        User user = userRepository.findByUsername(cleanUsername).orElse(null);

        // Check if user exists and password matches
        if (user == null || !user.getPassword().equals(password)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "Invalid username or password"));
        }

        // Return user info and role without exposing the raw password
        return ResponseEntity.ok(new LoginResponseDTO(
                user.getId(),
                user.getUsername(),
                user.getFullName(),
                user.getRole().name()
        ));
    }
}
