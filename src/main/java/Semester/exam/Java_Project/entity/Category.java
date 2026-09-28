package Semester.exam.Java_Project.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.util.List;

@Data
@Entity
public class Category {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    
    private int slaDays; 

    @ManyToOne
    @JoinColumn(name = "department_id") 
    private Department department;


}