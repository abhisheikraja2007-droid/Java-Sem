package Semester.exam.Java_Project.event;

import Semester.exam.Java_Project.entity.Grievance;
import org.springframework.context.ApplicationEvent;

/**
 * Custom Spring Application Event triggered whenever a Grievance is escalated.
 * Decouples the SLA checking logic from downstream notification systems (e.g. Email/SMS/Dashboard alerts).
 */
public class GrievanceEscalatedEvent extends ApplicationEvent {
    
    // Holds reference to the escalated Grievance
    private final Grievance grievance;

    /**
     * Constructor
     * @param source The object that published the event (usually GrievanceService)
     * @param grievance The Grievance entity that breached its SLA
     */
    public GrievanceEscalatedEvent(Object source, Grievance grievance) {
        super(source); // Passes event publisher reference to Spring's ApplicationEvent base class
        this.grievance = grievance;
    }

    // Getter so event listeners can inspect which grievance was escalated
    public Grievance getGrievance() {
        return grievance;
    }
}
