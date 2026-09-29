package Semester.exam.Java_Project.entity;

import jakarta.persistence.*;
import lombok.Data;

// System user: CITIZEN or SENIOR_OFFICER
@Data
@Entity
@Table(name = "app_user") // "user" is a reserved SQL word, so we use "app_user"
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String username;

    // WRITE_ONLY keeps the password from leaking in API responses
    @Column(nullable = false)
    @com.fasterxml.jackson.annotation.JsonProperty(access = com.fasterxml.jackson.annotation.JsonProperty.Access.WRITE_ONLY)
    private String password;

    @Column(nullable = false)
    private String fullName;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;
}
