package Semester.exam.Java_Project.repository;

import Semester.exam.Java_Project.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Spring Data JPA Repository for User (app_user) entity.
 * Provides user lookup and existence verification for authentication and seeding.
 */
@Repository // Registers this as a Spring Bean
public interface UserRepository extends JpaRepository<User, Long> {

    /**
     * Finds a user by their unique login username.
     * Uses Java 8 Optional to avoid NullPointerExceptions if user does not exist.
     * Generates SQL: SELECT * FROM app_user WHERE username = ?
     */
    Optional<User> findByUsername(String username);

    /**
     * Checks if a user already exists with the given username.
     * Used in DataInitializer to avoid duplicate seeding errors.
     * Generates SQL: SELECT COUNT(*) > 0 FROM app_user WHERE username = ?
     */
    boolean existsByUsername(String username);
}
