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
import org.springframework.transaction.annotation.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
@Transactional
public class GrievanceService {

    private static final Logger log = LoggerFactory.getLogger(GrievanceService.class);

    private final GrievanceRepository grievanceRepo;
    private final CategoryRepository categoryRepo;
    private final EscalationRepository escalationRepo;
    private final UserRepository userRepo;
    private final ApplicationEventPublisher eventPublisher;

    public GrievanceService(GrievanceRepository grievanceRepo, 
                            CategoryRepository categoryRepo,
                            EscalationRepository escalationRepo, 
                            UserRepository userRepo,
                            ApplicationEventPublisher eventPublisher) {
        this.grievanceRepo = grievanceRepo;
        this.categoryRepo = categoryRepo;
        this.escalationRepo = escalationRepo;
        this.userRepo = userRepo;
        this.eventPublisher = eventPublisher;
    }

    // Citizen creates a complaint; category determines which department handles it
    public Grievance createGrievance(Long categoryId, String description, String location, String citizenUsername) {
        if (description == null || description.trim().isEmpty()) {
            throw new IllegalArgumentException("Description cannot be empty");
        }
        if (description.length() > 1000) {
            description = description.substring(0, 1000);
        }
        if (location == null || location.trim().isEmpty()) {
            throw new IllegalArgumentException("Location cannot be empty");
        }

        Category category = categoryRepo.findById(categoryId)
                .orElseThrow(() -> new ResourceNotFoundException("Category with ID " + categoryId + " not found"));

        User citizen = userRepo.findByUsername(citizenUsername)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + citizenUsername));

        Grievance newGrievance = new Grievance();
        newGrievance.setDescription(description.trim());
        newGrievance.setLocation(location.trim());
        newGrievance.setStatus(GrievanceStatus.OPEN);
        newGrievance.setCategory(category);
        newGrievance.setCitizen(citizen);

        return grievanceRepo.save(newGrievance);
    }

    // Runs every night at midnight to check for overdue complaints
    @Scheduled(cron = "0 0 0 * * ?")
    public void escalateOverdueGrievances() {
        List<Grievance> activeGrievances = grievanceRepo.findByStatusIn(
                List.of(GrievanceStatus.OPEN, GrievanceStatus.IN_PROGRESS)
        );
        LocalDateTime now = LocalDateTime.now();

        for (Grievance grievance : activeGrievances) {
            // Skip invalid rows without dates or categories
            if (grievance.getCreatedAt() == null || grievance.getCategory() == null) {
                log.warn("Skipping grievance ID {} due to null createdAt or category", grievance.getId());
                continue;
            }

            int slaLimit = Math.max(1, grievance.getCategory().getSlaDays());
            LocalDateTime deadline = grievance.getCreatedAt().plusDays(slaLimit);

            // Escalate if past the SLA deadline
            if (now.isAfter(deadline)) {
                boolean alreadyEscalated = escalationRepo.findByGrievanceId(grievance.getId()).stream()
                        .anyMatch(e -> "Senior Officer".equalsIgnoreCase(e.getEscalatedTo()));

                grievance.setStatus(GrievanceStatus.ESCALATED);
                grievanceRepo.save(grievance);

                // Avoid duplicate escalation records for the same grievance
                if (!alreadyEscalated) {
                    long daysOverdue = ChronoUnit.DAYS.between(deadline, now);
                    long hoursOverdue = ChronoUnit.HOURS.between(deadline, now);
                    String overdueDesc = daysOverdue > 0 
                            ? (daysOverdue + " day" + (daysOverdue > 1 ? "s" : ""))
                            : (Math.max(1, hoursOverdue) + " hour" + (hoursOverdue > 1 ? "s" : ""));

                    Escalation escalation = new Escalation();
                    escalation.setGrievance(grievance);
                    escalation.setReason("SLA Breached by " + overdueDesc + ". Auto-escalated to Senior Officer.");
                    escalation.setEscalatedTo("Senior Officer");
                    escalationRepo.save(escalation);

                    log.warn("Escalated Grievance ID: {} (overdue by {})", grievance.getId(), overdueDesc);

                    // Fire internal event (can trigger email/SMS alerts)
                    eventPublisher.publishEvent(new GrievanceEscalatedEvent(this, grievance));
                }
            }
        }
    }

    // Update complaint status (e.g. OPEN -> IN_PROGRESS -> RESOLVED)
    public Grievance updateStatus(Long grievanceId, GrievanceStatus newStatus) {
        if (newStatus == null) {
            throw new IllegalArgumentException("New status cannot be null");
        }
        Grievance grievance = grievanceRepo.findById(grievanceId)
                .orElseThrow(() -> new ResourceNotFoundException("Grievance not found"));

        grievance.setStatus(newStatus);
        grievanceRepo.save(grievance);
        log.info("NOTIFICATION: Grievance ID {} status updated to {}", grievance.getId(), newStatus);
        return grievance;
    }

    // Citizens can only rate complaints that have been marked RESOLVED
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

    // Complaints assigned to a department
    public Page<Grievance> getGrievancesByDepartment(Long departmentId, Pageable pageable) {
        return grievanceRepo.findByCategoryDepartmentId(departmentId, pageable);
    }

    // Complaints filed by a specific citizen
    public Page<Grievance> getMyGrievances(String username, Pageable pageable) {
        return grievanceRepo.findByCitizenUsername(username, pageable);
    }

    // Complaints that breached SLA for senior officer dashboard
    public Page<Grievance> getEscalatedGrievances(Pageable pageable) {
        return grievanceRepo.findByStatus(GrievanceStatus.ESCALATED, pageable);
    }

    // Single complaint lookup
    public Grievance getGrievanceById(Long id) {
        return grievanceRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Grievance not found with ID: " + id));
    }

    // All complaints with pagination
    public Page<Grievance> getAllGrievances(Pageable pageable) {
        return grievanceRepo.findAll(pageable);
    }
}