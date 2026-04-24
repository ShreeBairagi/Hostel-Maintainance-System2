package com.hostel.requests;

/**
 * OtherRequest.java
 * -------------------
 * Subclass for requests that don't fit into standard categories.
 */
public class OtherRequest extends Request {

    public OtherRequest(int requestId, String studentName, String roomNumber, String description, String photoPath) {
        super(requestId, studentName, roomNumber, description, photoPath);
    }

    public OtherRequest(int requestId, String studentName, String roomNumber, String description, String photoPath, String submittedAt) {
        super(requestId, studentName, roomNumber, description, photoPath, submittedAt);
    }

    @Override
    public String getRequestType() {
        return "Other";
    }

    @Override
    public String getPriority() {
        return "ROUTINE";
    }
}
