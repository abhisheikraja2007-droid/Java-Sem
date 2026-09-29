package Semester.exam.Java_Project.repository;

import Semester.exam.Java_Project.entity.Grievance;
import Semester.exam.Java_Project.entity.GrievanceStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GrievanceRepository extends JpaRepository<Grievance, Long> {

    List<Grievance> findByStatus(GrievanceStatus status);

    // Spring Data navigates Grievance -> Category -> Department to filter by dept ID
    Page<Grievance> findByCategoryDepartmentId(Long departmentId, Pageable pageable);

    // Used by the SLA engine to check both OPEN and IN_PROGRESS complaints
    List<Grievance> findByStatusIn(List<GrievanceStatus> statuses);

    // "My Complaints" list for the logged-in citizen
    Page<Grievance> findByCitizenUsername(String username, Pageable pageable);

    // Filter grievances by status with pagination
    Page<Grievance> findByStatus(GrievanceStatus status, Pageable pageable);
}