package Semester.exam.Java_Project.event;

import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

/**
 * Event Listener that listens for GrievanceEscalatedEvent.
 * Whenever an event is published, Spring automatically invokes this handler.
 */
@Component // Registers this listener as a managed Spring Component
public class NotificationEventListener {

    /**
     * Handles the GrievanceEscalatedEvent.
     * In a production environment, this is where integration with SendGrid (Email)
     * or Twilio (SMS) would notify the Senior Officer in real time.
     * 
     * @param event The escalation event broadcast by GrievanceService
     */
    @EventListener // Automatically subscribes this method to GrievanceEscalatedEvent
    public void handleGrievanceEscalated(GrievanceEscalatedEvent event) {
        Long grievanceId = event.getGrievance().getId();
        String description = event.getGrievance().getDescription();
        
        // Dispatches simulated alert to console
        System.out.println("======================================");
        System.out.println("CRITICAL ALERT DISPATCHED TO SUPERVISOR");
        System.out.println("Grievance ID: " + grievanceId + " has breached SLA.");
        System.out.println("Issue: " + description);
        System.out.println("======================================");
    }
}
