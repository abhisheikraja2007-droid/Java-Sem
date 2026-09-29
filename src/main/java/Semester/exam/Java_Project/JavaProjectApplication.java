package Semester.exam.Java_Project;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication // Triggers component scan across all subpackages (controller, service,
						// repository, etc.)
@EnableScheduling // Activates the background thread pool for @Scheduled SLA checks
public class JavaProjectApplication {
	public static void main(String[] args) {
		SpringApplication.run(JavaProjectApplication.class, args);
	}

}
