package Semester.exam.Java_Project.repository;

import Semester.exam.Java_Project.entity.Escalation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface EscalationRepository extends JpaRepository<Escalation, Long> {
    // Custom query: Find all escalations for a specific grievance
    List<Escalation> findByGrievanceId(Long grievanceId);
}