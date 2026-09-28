package Semester.exam.Java_Project.repository;

import Semester.exam.Java_Project.entity.Grievance;
import Semester.exam.Java_Project.entity.GrievanceStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface GrievanceRepository extends JpaRepository<Grievance, Long> {
    
    // Custom query 1: Find by status (using the Enum we created in Phase 2)
    List<Grievance> findByStatus(GrievanceStatus status);

    // Custom query 2: Find all grievances for a specific department.
    List<Grievance> findByCategoryDepartmentId(Long departmentId);

    List<Grievance> findByStatusIn(List<GrievanceStatus> statuses);
}