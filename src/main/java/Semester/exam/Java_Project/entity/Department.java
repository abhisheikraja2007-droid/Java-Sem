package Semester.exam.Java_Project.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.util.List;

// Municipal department responsible for handling categories of complaints
@Data
@Entity
@Table(name = "department")
public class Department {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private String name;

    // One department has many categories; JsonIgnore prevents infinite recursion in JSON
    @OneToMany(mappedBy = "department")
    @com.fasterxml.jackson.annotation.JsonIgnore
    private List<Category> categories;

}