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

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime escalatedAt;

    @ManyToOne
    @JoinColumn(name = "grievance_id")
    @com.fasterxml.jackson.annotation.JsonIgnore
    private Grievance grievance;

}