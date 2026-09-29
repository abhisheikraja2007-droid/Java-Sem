package Semester.exam.Java_Project.controller;

import Semester.exam.Java_Project.DTO.GrievanceRequestDTO;
import Semester.exam.Java_Project.entity.Grievance;
import Semester.exam.Java_Project.entity.GrievanceStatus;
import Semester.exam.Java_Project.service.GrievanceService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/grievances")
public class GrievanceController {

    private final GrievanceService grievanceService;

    public GrievanceController(GrievanceService grievanceService) {
        this.grievanceService = grievanceService;
    }

    // Citizen files a new complaint
    @PostMapping
    public ResponseEntity<Grievance> submitGrievance(@Valid @RequestBody GrievanceRequestDTO request) {
        Grievance created = grievanceService.createGrievance(
                request.getCategoryId(),
                request.getDescription(),
                request.getLocation(),
                request.getCitizenUsername()
        );
        return ResponseEntity.ok(created);
    }

    // Department officer moves complaint status (e.g. IN_PROGRESS or RESOLVED)
    @PutMapping("/{id}/status")
    public ResponseEntity<Grievance> updateStatus(@PathVariable Long id, @RequestParam GrievanceStatus status) {
        return ResponseEntity.ok(grievanceService.updateStatus(id, status));
    }

    // Citizen rates a resolved complaint (1 to 5)
    @PostMapping("/{id}/rate")
    public ResponseEntity<Grievance> rateGrievance(@PathVariable Long id, @RequestParam int rating) {
        return ResponseEntity.ok(grievanceService.submitRating(id, rating));
    }

    // Department view: all complaints under this department
    @GetMapping("/department/{departmentId}")
    public ResponseEntity<Page<Grievance>> getByDepartment(@PathVariable Long departmentId, Pageable pageable) {
        return ResponseEntity.ok(grievanceService.getGrievancesByDepartment(departmentId, pageable));
    }

    // Allowed sorting fields to prevent invalid property lookup errors
    private static final java.util.Set<String> ALLOWED_SORTS = java.util.Set.of("id", "createdAt", "status", "rating", "description", "location");

    // All complaints (paginated and sorted)
    @GetMapping
    public Page<Grievance> getAllGrievances(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy) {
        int safePage = Math.max(0, page);
        int safeSize = Math.min(Math.max(1, size), 100);
        String safeSort = ALLOWED_SORTS.contains(sortBy) ? sortBy : "createdAt";
        Pageable pageable = PageRequest.of(safePage, safeSize, Sort.by(safeSort).descending());
        return grievanceService.getAllGrievances(pageable);
    }

    // Citizen's personal complaints view
    @GetMapping("/my")
    public ResponseEntity<Page<Grievance>> getMyGrievances(
            @RequestParam String username,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        int safePage = Math.max(0, page);
        int safeSize = Math.min(Math.max(1, size), 100);
        Pageable pageable = PageRequest.of(safePage, safeSize, Sort.by("createdAt").descending());
        return ResponseEntity.ok(grievanceService.getMyGrievances(username, pageable));
    }

    // Escalated complaints for senior officer review
    @GetMapping("/escalated")
    public ResponseEntity<Page<Grievance>> getEscalated(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        int safePage = Math.max(0, page);
        int safeSize = Math.min(Math.max(1, size), 100);
        Pageable pageable = PageRequest.of(safePage, safeSize, Sort.by("createdAt").descending());
        return ResponseEntity.ok(grievanceService.getEscalatedGrievances(pageable));
    }

    // Single complaint by ID
    @GetMapping("/{id}")
    public ResponseEntity<Grievance> getById(@PathVariable Long id) {
        return ResponseEntity.ok(grievanceService.getGrievanceById(id));
    }

    // Manual test trigger for the SLA escalation job
    @PostMapping("/test/trigger-sla")
    public ResponseEntity<String> testSlaEngine() {
        grievanceService.escalateOverdueGrievances();
        return ResponseEntity.ok("SLA Engine triggered. Check your database!");
    }
}