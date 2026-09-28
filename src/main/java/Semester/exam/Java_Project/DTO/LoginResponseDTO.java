package Semester.exam.Java_Project.DTO;

public class LoginResponseDTO {
    private Long id;
    private String username;
    private String fullName;
    private String role;

    public LoginResponseDTO(Long id, String username, String fullName, String role) {
        this.id = id;
        this.username = username;
        this.fullName = fullName;
        this.role = role;
    }

    public Long getId() { return id; }
    public String getUsername() { return username; }
    public String getFullName() { return fullName; }
    public String getRole() { return role; }
}
