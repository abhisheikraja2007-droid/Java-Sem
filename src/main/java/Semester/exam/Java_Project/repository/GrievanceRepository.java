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

    Page<Grievance> findByCategoryDepartmentId(Long departmentId, Pageable pageable);

    List<Grievance> findByStatusIn(List<GrievanceStatus> statuses);

    // "My Complaints" — returns only grievances filed by this citizen
    Page<Grievance> findByCitizenUsername(String username, Pageable pageable);

    // Senior Officer: fetch all escalated grievances
    Page<Grievance> findByStatus(GrievanceStatus status, Pageable pageable);
}