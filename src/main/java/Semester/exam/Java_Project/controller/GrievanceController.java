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

    // Constructor Injection: Spring automatically provides the Service layer here
    public GrievanceController(GrievanceService grievanceService) {
        this.grievanceService = grievanceService;
    }

    // 1. Submit a new grievance using a JSON Request Body and Validation
    // POST http://localhost:8080/api/grievances
    // JSON Body: { "categoryId": 1, "description": "Pothole on Main St" }
    @PostMapping
    public ResponseEntity<Grievance> submitGrievance(@Valid @RequestBody GrievanceRequestDTO request) {
        Grievance createdGrievance = grievanceService.createGrievance(
                request.getCategoryId(),
                request.getDescription()
        );
        return ResponseEntity.ok(createdGrievance);
    }

    // 2. Department updates the status
    // PUT http://localhost:8080/api/grievances/1/status?status=IN_PROGRESS
    @PutMapping("/{id}/status")
    public ResponseEntity<Grievance> updateStatus(@PathVariable Long id, @RequestParam GrievanceStatus status) {
        Grievance updatedGrievance = grievanceService.updateStatus(id, status);
        return ResponseEntity.ok(updatedGrievance);
    }

    // 3. Citizen submits a rating for a resolved grievance
    // POST http://localhost:8080/api/grievances/1/rate?rating=5
    @PostMapping("/{id}/rate")
    public ResponseEntity<Grievance> rateGrievance(@PathVariable Long id, @RequestParam int rating) {
        Grievance ratedGrievance = grievanceService.submitRating(id, rating);
        return ResponseEntity.ok(ratedGrievance);
    }

    // 4. Fetch all grievances for a specific department (UPDATED for Pagination)
    // GET http://localhost:8080/api/grievances/department/1?page=0&size=5&sort=createdAt,desc
    @GetMapping("/department/{departmentId}")
    public ResponseEntity<Page<Grievance>> getByDepartment(@PathVariable Long departmentId, Pageable pageable) {
        Page<Grievance> grievances = grievanceService.getGrievancesByDepartment(departmentId, pageable);
        return ResponseEntity.ok(grievances);
    }

    @GetMapping
    public Page<Grievance> getAllGrievances(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy) {

        // Create a Pageable object with sorting applied (e.g., newest first)
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortBy).descending());

        return grievanceService.getAllGrievances(pageable);
    }

    // TEMPORARY ENDPOINT FOR TESTING THE SLA ENGINE
    @PostMapping("/test/trigger-sla")
    public ResponseEntity<String> testSlaEngine() {
        grievanceService.escalateOverdueGrievances();
        return ResponseEntity.ok("SLA Engine triggered. Check your database!");
    }
}