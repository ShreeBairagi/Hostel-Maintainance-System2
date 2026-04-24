package com.hostel.requests;

import com.hostel.interfaces.Ratable;
import com.hostel.interfaces.Trackable;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Request.java
 * ---------------
 * Abstract base class for all maintenance requests.
 * Implements Trackable (for status updates) and Ratable (for star ratings).
 *
 * Subclasses: PlumbingRequest, ElectricalRequest, CarpentryRequest
 *
 * Concepts: Abstract Class, Interface, Polymorphism
 *
 * CSV column order (0-based index):
 * 0 requestId
 * 1 studentName
 * 2 roomNumber
 * 3 requestType
 * 4 description
 * 5 status
 * 6 assignedStaffName
 * 7 submittedAt
 * 8 priority
 * 9 rating
 * 10 photoPath
 */
public abstract class Request implements Trackable, Ratable {

    private int requestId;
    private String studentName;
    private String roomNumber;
    private String description;
    private String status; // PENDING → ASSIGNED → COMPLETED
    private String photoPath; // simulated file path
    private String submittedAt; // date-time when submitted
    private String assignedStaffName;
    private int rating; // 0 = not rated yet, 1-5 after completion

    // ── Constructor for a NEW request ────────────────────────────────────────
    public Request(int requestId, String studentName, String roomNumber,
            String description, String photoPath) {

        this.requestId = requestId;
        this.studentName = studentName;
        this.roomNumber = roomNumber;
        this.description = description;
        this.photoPath = (photoPath == null || photoPath.isEmpty()) ? "no-photo" : photoPath;
        this.status = "PENDING";
        this.assignedStaffName = "Not Assigned";
        this.rating = 0;

        // Save the exact time this request was created
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm");
        this.submittedAt = LocalDateTime.now().format(dtf);
    }

    // ── Constructor for loading FROM CSV (timestamp already known) ────────────
    public Request(int requestId, String studentName, String roomNumber,
            String description, String photoPath, String submittedAt) {

        this(requestId, studentName, roomNumber, description, photoPath);
        this.submittedAt = submittedAt; // overwrite with saved timestamp
    }

    // ── Abstract methods - subclasses MUST implement these ───────────────────

    /** Returns request type: "Plumbing", "Electrical", or "Carpentry" */
    public abstract String getRequestType();

    /**
     * Returns priority level.
     * Concept: Polymorphism - each subclass returns a different value.
     * Electrical = "URGENT", Plumbing = "MEDIUM", Carpentry = "ROUTINE"
     */
    public abstract String getPriority();

    // ── Trackable interface methods ───────────────────────────────────────────

    @Override
    public String getStatus() {
        return status;
    }

    @Override
    public void updateStatus(String newStatus) {
        // Only allow valid status values
        if (newStatus.equals("PENDING")
                || newStatus.equals("ASSIGNED")
                || newStatus.equals("COMPLETED")) {
            this.status = newStatus;
        } else {
            System.out.println("Invalid status: " + newStatus
                    + ". Must be PENDING, ASSIGNED, or COMPLETED.");
        }
    }

    @Override
    public String getAssignedStaffName() {
        return assignedStaffName;
    }

    // ── Ratable interface methods ─────────────────────────────────────────────

    @Override
    public void submitRating(int stars) {
        // Only allow rating if the request is completed
        if (!status.equals("COMPLETED")) {
            System.out.println("Cannot rate a request that is not yet completed.");
            return;
        }
        // Rating must be between 1 and 5
        if (stars < 1 || stars > 5) {
            System.out.println("Rating must be between 1 and 5.");
            return;
        }
        this.rating = stars;
        System.out.println("Thank you! Rating of " + stars + " submitted.");
    }

    @Override
    public int getRating() {
        return rating;
    }

    // ── CSV helper ────────────────────────────────────────────────────────────

    public String toCSV() {
        String safeDesc = description.replace(",", ";"); // commas break CSV parsing
        return requestId + ","
                + studentName + ","
                + roomNumber + ","
                + getRequestType() + ","
                + safeDesc + ","
                + status + ","
                + assignedStaffName + ","
                + submittedAt + ","
                + getPriority() + ","
                + rating + ","
                + photoPath;
    }

    // ── Getters ───────────────────────────────────────────────────────────────

    public int getRequestId() {
        return requestId;
    }

    public String getStudentName() {
        return studentName;
    }

    public String getRoomNumber() {
        return roomNumber;
    }

    public String getDescription() {
        return description;
    }

    public String getPhotoPath() {
        return photoPath;
    }

    public String getSubmittedAt() {
        return submittedAt;
    }

    // ── Setters ───────────────────────────────────────────────────────────────

    public void setRequestId(int id) {
        this.requestId = id;
    }

    public void setStudentName(String name) {
        this.studentName = name;
    }

    public void setRoomNumber(String room) {
        this.roomNumber = room;
    }

    public void setDescription(String desc) {
        this.description = desc;
    }

    public void setPhotoPath(String path) {
        this.photoPath = path;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setAssignedStaffName(String name) {
        this.assignedStaffName = name;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }

    @Override
    public String toString() {
        return "[REQ-" + requestId + "] "
                + getRequestType() + " | Room: " + roomNumber
                + " | Status: " + status
                + " | Priority: " + getPriority()
                + " | Submitted: " + submittedAt;
    }
}