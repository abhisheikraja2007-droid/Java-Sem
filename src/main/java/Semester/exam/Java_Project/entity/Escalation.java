package Semester.exam.Java_Project.entity;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import java.time.LocalDateTime;

@Data
@Entity
public class Escalation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String reason;

    // Requirement: "record which officer it was escalated to"
    private String escalatedTo; // e.g. "Senior Officer", "District Collector"

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime escalatedAt; // Requirement: "and when"

    @ManyToOne
    @JoinColumn(name = "grievance_id")
    @com.fasterxml.jackson.annotation.JsonIgnore
    private Grievance grievance;

}