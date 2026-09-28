package Semester.exam.Java_Project.repository;

import Semester.exam.Java_Project.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {
    // Custom query: Find all categories under a specific department
    List<Category> findByDepartmentId(Long departmentId);
}