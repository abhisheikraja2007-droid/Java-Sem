package Semester.exam.Java_Project.entity;

public enum GrievanceStatus {
    // Initial state when a citizen submits a new complaint
    OPEN,
    // State when a department officer starts investigating/working on the complaint
    IN_PROGRESS,
    // State when the issue has been addressed and marked complete (eligible for
    // citizen rating)
    RESOLVED,
    // State triggered by the SLA auto-escalation engine if the SLA is breached
    ESCALATED
}