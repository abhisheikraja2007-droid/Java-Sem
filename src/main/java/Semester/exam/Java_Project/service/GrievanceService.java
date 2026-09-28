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

    // Spring automatically injects the repositories you created in Phase 3
    private final GrievanceRepository grievanceRepo;
    private final CategoryRepository categoryRepo;
    private final EscalationRepository escalationRepo;

    public GrievanceService(GrievanceRepository grievanceRepo, CategoryRepository categoryRepo, EscalationRepository escalationRepo) {
        this.grievanceRepo = grievanceRepo;
        this.categoryRepo = categoryRepo;
        this.escalationRepo = escalationRepo;
    }

    // --- RULE 1: Grievance Creation & Auto-Routing ---
    public Grievance createGrievance(Long categoryId, String description) {
        // 1. Fetch the category (which inherently knows its assigned Department)
        Category category = categoryRepo.findById(categoryId)
                .orElseThrow(() -> new ResourceNotFoundException("Category with ID " + categoryId + " not found"));

        // 2. Build the grievance
        Grievance newGrievance = new Grievance();
        newGrievance.setDescription(description);
        newGrievance.setStatus(GrievanceStatus.OPEN);
        newGrievance.setCategory(category); // This implicitly routes it to the correct Department

        // 3. Save it to the database
        return grievanceRepo.save(newGrievance);
    }

    // --- RULE 2: SLA Escalation Engine ---
    // The @Scheduled annotation runs this method automatically.
    // "0 0 0 * * ?" is a Cron expression meaning "Run every day at midnight"
    @Scheduled(cron = "0 0 0 * * ?")
    public void escalateOverdueGrievances() {

        // 1. Find all active grievances
        List<Grievance> activeGrievances = grievanceRepo.findByStatusIn(
                List.of(GrievanceStatus.OPEN, GrievanceStatus.IN_PROGRESS)
        );

        LocalDateTime now = LocalDateTime.now();

        // 2. Evaluate each grievance against its SLA
        for (Grievance grievance : activeGrievances) {
            long daysOpen = ChronoUnit.DAYS.between(grievance.getCreatedAt(), now);
            int slaLimit = grievance.getCategory().getSlaDays();

            if (daysOpen > slaLimit) {
                // Change status
                grievance.setStatus(GrievanceStatus.ESCALATED);
                grievanceRepo.save(grievance);

                // Generate Escalation Record
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
                    .orElseThrow(() -> new RuntimeException("Grievance not found"));
            grievance.setStatus(newStatus);
            return grievanceRepo.save(grievance);
        }

        // --- RULE 4: Submit Rating ---
        public Grievance submitRating(Long grievanceId, int rating) {
            Grievance grievance = grievanceRepo.findById(grievanceId)
                    .orElseThrow(() -> new RuntimeException("Grievance not found"));
            if (grievance.getStatus() != GrievanceStatus.RESOLVED) {
                throw new RuntimeException("Can only rate resolved grievances");
            }
            grievance.setRating(rating);
            return grievanceRepo.save(grievance);
        }

        // --- RULE 5: Fetch by Department ---
        public List<Grievance> getGrievancesByDepartment(Long departmentId) {
            return grievanceRepo.findByCategoryDepartmentId(departmentId);
        }
}