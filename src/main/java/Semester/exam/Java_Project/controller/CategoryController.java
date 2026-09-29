package Semester.exam.Java_Project.controller;

import Semester.exam.Java_Project.entity.Category;
import Semester.exam.Java_Project.repository.CategoryRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * REST Controller exposing public endpoints for complaint Categories.
 * Allows frontend forms to populate dropdown lists of available categories.
 */
@RestController // Combines @Controller and @ResponseBody (all methods return serialized JSON)
@RequestMapping("/api/categories") // Base URL routing path for category-related endpoints
public class CategoryController {

    // Dependency injection of CategoryRepository
    private final CategoryRepository categoryRepository;

    // Constructor injection: Spring provides the repository bean automatically
    public CategoryController(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    /**
     * GET /api/categories
     * Fetches all complaint categories along with their SLA days and parent department.
     * 
     * @return HTTP 200 OK with JSON array of Category entities
     */
    @GetMapping
    public ResponseEntity<List<Category>> getAllCategories() {
        return ResponseEntity.ok(categoryRepository.findAll());
    }
}
