package Semester.exam.Java_Project;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * Main Entry Point for the Spring Boot Application.
 * 
 * - @SpringBootApplication: Enables Component Scanning, Auto-Configuration, and Configuration.
 * - @EnableScheduling: Enables Spring's background task executor to run @Scheduled cron jobs (like our SLA auto-escalation).
 */
@SpringBootApplication // Triggers component scan across all subpackages (controller, service, repository, etc.)
@EnableScheduling      // Activates the background thread pool for @Scheduled SLA checks
public class JavaProjectApplication {

	/**
	 * Main method: launches the embedded Tomcat web server and starts the Spring application context.
	 */
	public static void main(String[] args) {
		SpringApplication.run(JavaProjectApplication.class, args);
	}

}
