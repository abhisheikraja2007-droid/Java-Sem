package Semester.exam.Java_Project.repository;

import Semester.exam.Java_Project.entity.Escalation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

/**
 * Spring Data JPA Repository for Escalation entity.
 * Handles persistence of SLA breach and escalation audit logs.
 */
@Repository // Registers this as a Spring Bean in the application context
public interface EscalationRepository extends JpaRepository<Escalation, Long> {

    /**
     * Derived Query Method: Spring Data generates SQL:
     * SELECT * FROM escalation WHERE grievance_id = ?
     * 
     * Used to check whether an escalation record has already been logged for a grievance.
     * 
     * @param grievanceId ID of the grievance to check
     * @return List of escalations logged for this grievance
     */
    List<Escalation> findByGrievanceId(Long grievanceId);
}