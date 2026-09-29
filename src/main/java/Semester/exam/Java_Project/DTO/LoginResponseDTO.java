package Semester.exam.Java_Project.DTO;

/**
 * Data Transfer Object returned upon successful authentication.
 * Omits sensitive details (such as the password) to keep API responses secure.
 */
public class LoginResponseDTO {
    // Unique user ID
    private Long id;
    // Login username
    private String username;
    // Citizen or Officer's display name
    private String fullName;
    // User's role string (e.g. "CITIZEN" or "SENIOR_OFFICER")
    private String role;

    // All-args constructor for instantiating the response
    public LoginResponseDTO(Long id, String username, String fullName, String role) {
        this.id = id;
        this.username = username;
        this.fullName = fullName;
        this.role = role;
    }

    // Getters allowing Jackson to convert this Java object to JSON
    public Long getId() { return id; }
    public String getUsername() { return username; }
    public String getFullName() { return fullName; }
    public String getRole() { return role; }
}
