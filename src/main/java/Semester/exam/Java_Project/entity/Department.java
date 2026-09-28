package Semester.exam.Java_Project.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.util.List;

@Data
@Entity
public class Department {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private String name;

    @OneToMany(mappedBy = "department")
    @com.fasterxml.jackson.annotation.JsonIgnore
    private List<Category> categories;


}