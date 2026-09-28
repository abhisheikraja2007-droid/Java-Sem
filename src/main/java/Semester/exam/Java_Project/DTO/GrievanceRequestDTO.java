package Semester.exam.Java_Project.DTO;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

public class GrievanceRequestDTO {

    @NotNull(message = "Category ID is required")
    @Positive(message = "Category ID must be a positive number (e.g. 1, 2, 3)")
    private Long categoryId;

    @NotBlank(message = "Grievance description cannot be empty")
    @Size(min = 10, max = 1000, message = "Description must be between 10 and 1000 characters")
    private String description;

    @NotBlank(message = "Location cannot be empty")
    @Size(min = 3, max = 255, message = "Location must be between 3 and 255 characters")
    private String location;

    // Username of the logged-in citizen filing the grievance
    @NotBlank(message = "citizenUsername is required")
    private String citizenUsername;

    // Getters and Setters
    public Long getCategoryId() { return categoryId; }
    public void setCategoryId(Long categoryId) { this.categoryId = categoryId; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }
    public String getCitizenUsername() { return citizenUsername; }
    public void setCitizenUsername(String citizenUsername) { this.citizenUsername = citizenUsername; }
}
