package Semester.exam.Java_Project.event;

import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
public class NotificationEventListener {

    @EventListener
    public void handleGrievanceEscalated(GrievanceEscalatedEvent event) {
        Long grievanceId = event.getGrievance().getId();
        String description = event.getGrievance().getDescription();
        
        // In a real application, you would put your EmailService or Twilio SMS logic here.
        System.out.println("======================================");
        System.out.println("CRITICAL ALERT DISPATCHED TO SUPERVISOR");
        System.out.println("Grievance ID: " + grievanceId + " has breached SLA.");
        System.out.println("Issue: " + description);
        System.out.println("======================================");
    }
}
