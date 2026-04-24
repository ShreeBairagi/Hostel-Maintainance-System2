package com.hostel.exceptions;

/**
 * DuplicateRequestException.java
 * --------------------------------
 * Thrown when a student tries to submit a request for a room that
 * already has an open (PENDING or ASSIGNED) request of the same type.
 *
 * This stops students from flooding the system with duplicate complaints.
 *
 * Concept: Custom Exception (extends Exception = checked exception)
 *
 * Example usage:
 * if (openRooms.contains(roomNumber)) {
 * throw new DuplicateRequestException(
 * "A request is already open for this room.", roomNumber, "Electrical");
 * }
 */
public class DuplicateRequestException extends Exception {
    private static final long serialVersionUID = 1L;

    private String roomNumber;
    private String requestType;

    // Constructor with message only
    public DuplicateRequestException(String message) {
        super(message);
        this.roomNumber = "unknown";
        this.requestType = "unknown";
    }

    // Constructor with full details - use this one whenever possible
    public DuplicateRequestException(String message, String roomNumber, String requestType) {
        super(message);
        this.roomNumber = roomNumber;
        this.requestType = requestType;
    }

    // Getters
    public String getRoomNumber() {
        return roomNumber;
    }

    public String getRequestType() {
        return requestType;
    }

    @Override
    public String toString() {
        return "DuplicateRequestException: " + getMessage()
                + " [Room: " + roomNumber + ", Type: " + requestType + "]";
    }
}