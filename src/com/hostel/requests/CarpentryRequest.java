package com.hostel.requests;

/**
 * CarpentryRequest.java
 * For issues like broken doors, damaged furniture, loose cupboards.
 * Priority: ROUTINE (not dangerous, can be scheduled)
 * Concept: Polymorphism - overrides getPriority()
 */
public class CarpentryRequest extends Request {

    // Constructor for a brand new request submitted by student
    public CarpentryRequest(int requestId, String studentName,
                            String roomNumber, String description,
                            String photoPath) {
        super(requestId, studentName, roomNumber, description, photoPath);
    }

    // Constructor used when loading an existing request from CSV
    public CarpentryRequest(int requestId, String studentName,
                            String roomNumber, String description,
                            String photoPath, String submittedAt) {
        super(requestId, studentName, roomNumber, description, photoPath, submittedAt);
    }

    @Override
    public String getRequestType() {
        return "Carpentry";
    }

    @Override
    public String getPriority() {
        return "ROUTINE";
    }
}