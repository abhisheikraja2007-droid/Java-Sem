package Semester.exam.Java_Project.repository;

import Semester.exam.Java_Project.entity.Department;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA Repository for Department entity.
 * Provides built-in methods: save(), findById(), findAll(), count(), deleteById().
 */
@Repository // Registers this interface as an injectable Spring Bean
public interface DepartmentRepository extends JpaRepository<Department, Long> {
    // Standard JpaRepository methods are inherited automatically without needing explicit code
}