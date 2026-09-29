package Semester.exam.Java_Project.repository;

import Semester.exam.Java_Project.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

/**
 * Spring Data JPA Repository for Category entity.
 * JpaRepository provides out-of-the-box CRUD operations (save, findById, findAll, deleteById, count).
 */
@Repository // Marks this interface as a Spring Data repository bean
public interface CategoryRepository extends JpaRepository<Category, Long> {

    /**
     * Derived Query Method: Spring Data inspects the method name and generates:
     * SELECT * FROM category WHERE department_id = ?
     * 
     * @param departmentId ID of the parent department
     * @return List of categories belonging to that department
     */
    List<Category> findByDepartmentId(Long departmentId);
}