package Semester.exam.Java_Project.entity;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import java.time.LocalDateTime;
import java.util.List;

// Main complaint entity connecting citizen, category, and escalation history
@Data
@Entity
@Table(name = "grievance")
public class Grievance {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Citizen rating (1 to 5) after the issue is resolved
    private Integer rating;

    @Column(length = 1000, nullable = false)
    private String description;
    
    @Column(nullable = false)
    private String location;

    // OPEN, IN_PROGRESS, RESOLVED, ESCALATED (saved as text in database)
    @Enumerated(EnumType.STRING) 
    @Column(nullable = false)
    private GrievanceStatus status;

    // Timestamp when the citizen submitted the complaint (never updated)
    @CreationTimestamp 
    @Column(updatable = false)
    private LocalDateTime createdAt;

    // Complaint category (determines department routing and SLA days)
    @ManyToOne
    @JoinColumn(name = "category_id")
    private Category category;

    // The citizen who filed this complaint
    @ManyToOne
    @JoinColumn(name = "citizen_id")
    private User citizen;

    // History of escalations if SLA was breached
    @OneToMany(mappedBy = "grievance", cascade = CascadeType.ALL)
    private List<Escalation> escalations;

}