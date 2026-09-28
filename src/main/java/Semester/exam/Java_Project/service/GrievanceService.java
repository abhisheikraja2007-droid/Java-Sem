package Semester.exam.Java_Project.service;

import Semester.exam.Java_Project.entity.*;
import Semester.exam.Java_Project.repository.*;
import Semester.exam.Java_Project.exception.ResourceNotFoundException;
import Semester.exam.Java_Project.event.GrievanceEscalatedEvent;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
public class GrievanceService {

    private static final Logger log = LoggerFactory.getLogger(GrievanceService.class);

    private final GrievanceRepository grievanceRepo;
    private final CategoryRepository categoryRepo;
    private final EscalationRepository escalationRepo;
    private final ApplicationEventPublisher eventPublisher; // ADDED for Event-Driven Notifications

    // ADDED ApplicationEventPublisher to Constructor
    public GrievanceService(GrievanceRepository grievanceRepo, CategoryRepository categoryRepo,
                            EscalationRepository escalationRepo, ApplicationEventPublisher eventPublisher) {
        this.grievanceRepo = grievanceRepo;
        this.categoryRepo = categoryRepo;
        this.escalationRepo = escalationRepo;
        this.eventPublisher = eventPublisher;
    }

    public Grievance createGrievance(Long categoryId, String description, String location) {
        Category category = categoryRepo.findById(categoryId)
                .orElseThrow(() -> new ResourceNotFoundException("Category with ID " + categoryId + " not found"));

        Grievance newGrievance = new Grievance();
        newGrievance.setDescription(description);
        newGrievance.setLocation(location);
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

                // ADDED: Publish the event so the Listener can send notifications
                eventPublisher.publishEvent(new GrievanceEscalatedEvent(this, grievance));
            }
        }
    }

    // --- RULE 3: Update Status ---
    public Grievance updateStatus(Long grievanceId, GrievanceStatus newStatus) {
        Grievance grievance = grievanceRepo.findById(grievanceId)
                .orElseThrow(() -> new ResourceNotFoundException("Grievance not found"));

        grievance.setStatus(newStatus);
        grievanceRepo.save(grievance);
        
        // The Notification
        log.info("NOTIFICATION: Grievance ID {} status has been updated to {}", grievance.getId(), newStatus);
        
        return grievance;
    }

    // --- RULE 4: Submit Rating ---
    public Grievance submitRating(Long grievanceId, int rating) {
        Grievance grievance = grievanceRepo.findById(grievanceId)
                .orElseThrow(() -> new ResourceNotFoundException("Grievance not found"));

        if (grievance.getStatus() != GrievanceStatus.RESOLVED) {
            throw new IllegalStateException("Can only rate resolved grievances");
        }
        grievance.setRating(rating);
        return grievanceRepo.save(grievance);
    }

    // --- RULE 5: Fetch by Department (UPDATED for Pagination) ---
    public Page<Grievance> getGrievancesByDepartment(Long departmentId, Pageable pageable) {
        return grievanceRepo.findByCategoryDepartmentId(departmentId, pageable);
    }

    public Page<Grievance> getAllGrievances(Pageable pageable) {
        return grievanceRepo.findAll(pageable);
    }
}