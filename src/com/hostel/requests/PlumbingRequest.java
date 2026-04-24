package com.hostel.requests;

/**
 * PlumbingRequest.java
 * For issues like leaking taps, blocked drains, broken pipes.
 * Priority: MEDIUM
 * Concept: Polymorphism - overrides getPriority()
 */
public class PlumbingRequest extends Request {

    // Constructor for a brand new request submitted by student
    public PlumbingRequest(int requestId, String studentName,
                           String roomNumber, String description,
                           String photoPath) {
        super(requestId, studentName, roomNumber, description, photoPath);
    }

    // Constructor used when loading an existing request from CSV
    public PlumbingRequest(int requestId, String studentName,
                           String roomNumber, String description,
                           String photoPath, String submittedAt) {
        super(requestId, studentName, roomNumber, description, photoPath, submittedAt);
    }

    @Override
    public String getRequestType() {
        return "Plumbing";
    }

    @Override
    public String getPriority() {
        return "MEDIUM";
    }
}