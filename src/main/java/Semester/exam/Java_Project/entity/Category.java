package Semester.exam.Java_Project.entity;

import jakarta.persistence.*;
import lombok.Data;

// Complaint category (e.g. "Potholes", "Streetlights") linked to a Department with an SLA deadline
@Data
@Entity
@Table(name = "category")
public class Category {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    // SLA in days before this complaint auto-escalates to a senior officer
    private int slaDays;

    // Many categories belong to one department (e.g. Roads & PWD)
    @ManyToOne
    @JoinColumn(name = "department_id")
    private Department department;

}