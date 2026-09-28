package Semester.exam.Java_Project.service;

import Semester.exam.Java_Project.entity.*;
import Semester.exam.Java_Project.repository.*;
import Semester.exam.Java_Project.exception.ResourceNotFoundException;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
public class GrievanceService {

    private final GrievanceRepository grievanceRepo;
    private final CategoryRepository categoryRepo;
    private final EscalationRepository escalationRepo;

    public GrievanceService(GrievanceRepository grievanceRepo, CategoryRepository categoryRepo, EscalationRepository escalationRepo) {
        this.grievanceRepo = grievanceRepo;
        this.categoryRepo = categoryRepo;
        this.escalationRepo = escalationRepo;
    }

    public Grievance createGrievance(Long categoryId, String description) {
        Category category = categoryRepo.findById(categoryId)
                .orElseThrow(() -> new ResourceNotFoundException("Category with ID " + categoryId + " not found"));

        Grievance newGrievance = new Grievance();
        newGrievance.setDescription(description);
        newGrievance.setStatus(GrievanceStatus.OPEN);
        newGrievance.setCategory(category);

        return grievanceRepo.save(newGrievance);
    }

    @Scheduled(cron = "0 0 0 * * ?")
    public void escalateOverdueGrievances() {
        List<Grievance> activeGrievances = grievanceRepo.findByStatusIn(
                List.of(GrievanceStatus.OPEN, GrievanceStatus.IN_PROGRESS)
        );
        LocalDateTime now = LocalDateTime.now();
        for (Grievance grievance : activeGrievances) {
            long daysOpen = ChronoUnit.DAYS.between(grievance.getCreatedAt(), now);
            int slaLimit = grievance.getCategory().getSlaDays();

            if (daysOpen > slaLimit) {
                grievance.setStatus(GrievanceStatus.ESCALATED);
                grievanceRepo.save(grievance);

                Escalation escalation = new Escalation();
                escalation.setGrievance(grievance);
                escalation.setReason("SLA Breached by " + (daysOpen - slaLimit) + " days. Auto-escalated to Senior Officer.");
                escalationRepo.save(escalation);

                System.out.println("Escalated Grievance ID: " + grievance.getId());
            }
        }
    }

    // --- RULE 3: Update Status ---
    public Grievance updateStatus(Long grievanceId, GrievanceStatus newStatus) {
        Grievance grievance = grievanceRepo.findById(grievanceId)
                // CHANGED: Throw specific 404 exception instead of RuntimeException
                .orElseThrow(() -> new ResourceNotFoundException("Grievance not found"));

        grievance.setStatus(newStatus);
        return grievanceRepo.save(grievance);
    }

    // --- RULE 4: Submit Rating ---
    public Grievance submitRating(Long grievanceId, int rating) {
        Grievance grievance = grievanceRepo.findById(grievanceId)
                // CHANGED: Throw specific 404 exception instead of RuntimeException
                .orElseThrow(() -> new ResourceNotFoundException("Grievance not found"));

        if (grievance.getStatus() != GrievanceStatus.RESOLVED) {
            // CHANGED: Throw IllegalStateException for the logic rule
            throw new IllegalStateException("Can only rate resolved grievances");
        }
        grievance.setRating(rating);
        return grievanceRepo.save(grievance);
    }

    // --- RULE 5: Fetch by Department ---
    public List<Grievance> getGrievancesByDepartment(Long departmentId) {
        return grievanceRepo.findByCategoryDepartmentId(departmentId);
    }
}