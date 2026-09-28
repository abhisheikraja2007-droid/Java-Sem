package Semester.exam.Java_Project.event;

import Semester.exam.Java_Project.entity.Grievance;
import org.springframework.context.ApplicationEvent;

public class GrievanceEscalatedEvent extends ApplicationEvent {
    
    private final Grievance grievance;

    public GrievanceEscalatedEvent(Object source, Grievance grievance) {
        super(source);
        this.grievance = grievance;
    }

    public Grievance getGrievance() {
        return grievance;
    }
}
