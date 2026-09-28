package Semester.exam.Java_Project.entity;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Entity
public class Grievance {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Integer rating;
    private String description;

    @Enumerated(EnumType.STRING) 
    private GrievanceStatus status;

    @CreationTimestamp 
    @Column(updatable = false)
    private LocalDateTime createdAt;

    @ManyToOne
    @JoinColumn(name = "category_id")
    private Category category;

    @OneToMany(mappedBy = "grievance", cascade = CascadeType.ALL)
    private List<Escalation> escalations;


}