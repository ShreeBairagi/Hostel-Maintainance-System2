package com.hostel.exceptions;

/**
 * InvalidRoomException.java
 * --------------------------
 * Thrown when a student enters a room number that does not exist
 * or is in the wrong format (e.g. empty string, unknown block).
 *
 * Concept: Custom Exception (extends Exception = checked exception)
 *
 * Example usage:
 * if (roomNumber == null || roomNumber.isEmpty()) {
 * throw new InvalidRoomException("Room number cannot be empty.", roomNumber);
 * }
 */
public class InvalidRoomException extends Exception {
    private static final long serialVersionUID = 1L;

    // Keep track of the bad room number that caused this exception
    private String roomNumber;

    // Constructor with just a message
    public InvalidRoomException(String message) {
        super(message);
        this.roomNumber = "unknown";
    }

    // Constructor with message AND the invalid room number
    public InvalidRoomException(String message, String roomNumber) {
        super(message);
        this.roomNumber = roomNumber;
    }

    // Getter - calling code can read the bad room number if needed
    public String getRoomNumber() {
        return roomNumber;
    }

    @Override
    public String toString() {
        return "InvalidRoomException: " + getMessage()
                + " [Room entered: " + roomNumber + "]";
    }
}