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

    // 1. Submit a new grievance (citizen must be logged in — sends citizenUsername)
    // POST /api/grievances
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

    // 2. Update status (department / senior officer action)
    // PUT /api/grievances/{id}/status?status=IN_PROGRESS
    @PutMapping("/{id}/status")
    public ResponseEntity<Grievance> updateStatus(@PathVariable Long id, @RequestParam GrievanceStatus status) {
        return ResponseEntity.ok(grievanceService.updateStatus(id, status));
    }

    // 3. Rate a resolved grievance
    // POST /api/grievances/{id}/rate?rating=5
    @PostMapping("/{id}/rate")
    public ResponseEntity<Grievance> rateGrievance(@PathVariable Long id, @RequestParam int rating) {
        return ResponseEntity.ok(grievanceService.submitRating(id, rating));
    }

    // 4. Get grievances for a specific department
    // GET /api/grievances/department/{departmentId}
    @GetMapping("/department/{departmentId}")
    public ResponseEntity<Page<Grievance>> getByDepartment(@PathVariable Long departmentId, Pageable pageable) {
        return ResponseEntity.ok(grievanceService.getGrievancesByDepartment(departmentId, pageable));
    }

    // 5. Get ALL grievances (paginated) — used by senior officer dashboard
    // GET /api/grievances
    @GetMapping
    public Page<Grievance> getAllGrievances(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortBy).descending());
        return grievanceService.getAllGrievances(pageable);
    }

    // 6. "My Complaints" — citizen sees only their own grievances
    // GET /api/grievances/my?username=citizen
    @GetMapping("/my")
    public ResponseEntity<Page<Grievance>> getMyGrievances(
            @RequestParam String username,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        return ResponseEntity.ok(grievanceService.getMyGrievances(username, pageable));
    }

    // 7. Escalated grievances — for senior officer oversight panel
    // GET /api/grievances/escalated
    @GetMapping("/escalated")
    public ResponseEntity<Page<Grievance>> getEscalated(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        return ResponseEntity.ok(grievanceService.getEscalatedGrievances(pageable));
    }

    // 8. Get single grievance by ID
    // GET /api/grievances/{id}
    @GetMapping("/{id}")
    public ResponseEntity<Grievance> getById(@PathVariable Long id) {
        return ResponseEntity.ok(grievanceService.getGrievanceById(id));
    }

    // 9. Test: manually trigger SLA engine
    @PostMapping("/test/trigger-sla")
    public ResponseEntity<String> testSlaEngine() {
        grievanceService.escalateOverdueGrievances();
        return ResponseEntity.ok("SLA Engine triggered. Check your database!");
    }
}