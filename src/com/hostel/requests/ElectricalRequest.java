package com.hostel.requests;

/**
 * ElectricalRequest.java
 * For issues like broken fans, no power, short circuits.
 * Priority: URGENT (electrical faults are a safety hazard)
 * Concept: Polymorphism - overrides getPriority()
 */
public class ElectricalRequest extends Request {

    // Constructor for a brand new request submitted by student
    public ElectricalRequest(int requestId, String studentName,
                             String roomNumber, String description,
                             String photoPath) {
        super(requestId, studentName, roomNumber, description, photoPath);
    }

    // Constructor used when loading an existing request from CSV
    public ElectricalRequest(int requestId, String studentName,
                             String roomNumber, String description,
                             String photoPath, String submittedAt) {
        super(requestId, studentName, roomNumber, description, photoPath, submittedAt);
    }

    @Override
    public String getRequestType() {
        return "Electrical";
    }

    @Override
    public String getPriority() {
        return "URGENT";
    }
}