package Semester.exam.Java_Project.DTO;

// Jakarta Validation constraints (Bean Validation JSR-380)
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

/**
 * Data Transfer Object (DTO) for creating a new Grievance.
 * Using DTOs decouples the external HTTP JSON request payload from the internal database Entity,
 * protecting against Over-Posting attacks and enforcing strict input validation.
 */
public class GrievanceRequestDTO {

    // Ensures the user provides a categoryId (cannot be null)
    @NotNull(message = "Category ID is required")
    // Ensures categoryId is > 0 (prevents negative or zero values)
    @Positive(message = "Category ID must be a positive number (e.g. 1, 2, 3)")
    private Long categoryId;

    // Checks that string is not null and trimmed length is > 0
    @NotBlank(message = "Grievance description cannot be empty")
    // Limits length between 10 and 1000 characters to prevent spam or SQL column overflow
    @Size(min = 10, max = 1000, message = "Description must be between 10 and 1000 characters")
    private String description;

    // Location cannot be empty or whitespace only
    @NotBlank(message = "Location cannot be empty")
    @Size(min = 3, max = 255, message = "Location must be between 3 and 255 characters")
    private String location;

    // Username of the logged-in citizen filing the grievance
    @NotBlank(message = "citizenUsername is required")
    private String citizenUsername;

    // --- Standard Getters and Setters (Required for Jackson JSON serialization/deserialization) ---
    public Long getCategoryId() { return categoryId; }
    public void setCategoryId(Long categoryId) { this.categoryId = categoryId; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }

    public String getCitizenUsername() { return citizenUsername; }
    public void setCitizenUsername(String citizenUsername) { this.citizenUsername = citizenUsername; }
}