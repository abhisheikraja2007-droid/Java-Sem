package Semester.exam.Java_Project.entity;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import java.time.LocalDateTime;

// Audit record created when a grievance exceeds its SLA deadline
@Data
@Entity
@Table(name = "escalation")
public class Escalation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Reason for escalation (e.g. "SLA Breached by 2 days")
    private String reason;

    // Senior official receiving the escalation
    private String escalatedTo; 

    // Automatically set when this escalation row is inserted
    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime escalatedAt; 

    @ManyToOne
    @JoinColumn(name = "grievance_id")
    @com.fasterxml.jackson.annotation.JsonIgnore
    private Grievance grievance;

}