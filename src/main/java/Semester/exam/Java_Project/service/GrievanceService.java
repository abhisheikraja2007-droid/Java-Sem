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
    private final UserRepository userRepo;
    private final ApplicationEventPublisher eventPublisher;

    public GrievanceService(GrievanceRepository grievanceRepo, CategoryRepository categoryRepo,
                            EscalationRepository escalationRepo, UserRepository userRepo,
                            ApplicationEventPublisher eventPublisher) {
        this.grievanceRepo = grievanceRepo;
        this.categoryRepo = categoryRepo;
        this.escalationRepo = escalationRepo;
        this.userRepo = userRepo;
        this.eventPublisher = eventPublisher;
    }

    // --- RULE 1: Create Grievance linked to citizen ---
    public Grievance createGrievance(Long categoryId, String description, String location, String citizenUsername) {
        Category category = categoryRepo.findById(categoryId)
                .orElseThrow(() -> new ResourceNotFoundException("Category with ID " + categoryId + " not found"));

        User citizen = userRepo.findByUsername(citizenUsername)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + citizenUsername));

        Grievance newGrievance = new Grievance();
        newGrievance.setDescription(description);
        newGrievance.setLocation(location);
        newGrievance.setStatus(GrievanceStatus.OPEN);
        newGrievance.setCategory(category);
        newGrievance.setCitizen(citizen);

        return grievanceRepo.save(newGrievance);
    }

    // --- SLA Auto-Escalation (runs midnight daily) ---
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
                escalation.setEscalatedTo("Senior Officer");
                escalationRepo.save(escalation);

                log.warn("Escalated Grievance ID: {}", grievance.getId());
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
        log.info("NOTIFICATION: Grievance ID {} status updated to {}", grievance.getId(), newStatus);
        return grievance;
    }

    // --- RULE 4: Submit Rating (only when RESOLVED) ---
    public Grievance submitRating(Long grievanceId, int rating) {
        if (rating < 1 || rating > 5) {
            throw new IllegalArgumentException(
                "Rating must be between 1 and 5. You provided: " + rating);
        }
        Grievance grievance = grievanceRepo.findById(grievanceId)
                .orElseThrow(() -> new ResourceNotFoundException("Grievance not found with ID: " + grievanceId));
        if (grievance.getStatus() != GrievanceStatus.RESOLVED) {
            throw new IllegalStateException(
                "Cannot rate grievance ID " + grievanceId + " because it is not yet RESOLVED. " +
                "Current status: " + grievance.getStatus());
        }
        grievance.setRating(rating);
        return grievanceRepo.save(grievance);
    }

    // --- RULE 5: Fetch by Department ---
    public Page<Grievance> getGrievancesByDepartment(Long departmentId, Pageable pageable) {
        return grievanceRepo.findByCategoryDepartmentId(departmentId, pageable);
    }

    // --- My Complaints: Citizen sees only their own grievances ---
    public Page<Grievance> getMyGrievances(String username, Pageable pageable) {
        return grievanceRepo.findByCitizenUsername(username, pageable);
    }

    // --- Senior Officer: see all escalated grievances ---
    public Page<Grievance> getEscalatedGrievances(Pageable pageable) {
        return grievanceRepo.findByStatus(GrievanceStatus.ESCALATED, pageable);
    }

    // --- Get single grievance by ID ---
    public Grievance getGrievanceById(Long id) {
        return grievanceRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Grievance not found with ID: " + id));
    }

    public Page<Grievance> getAllGrievances(Pageable pageable) {
        return grievanceRepo.findAll(pageable);
    }
}