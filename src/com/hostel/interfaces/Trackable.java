package com.hostel.interfaces;

/**
 * Trackable.java
 * ---------------
 * Interface that forces any request to support status tracking.
 * Any class that says "implements Trackable" MUST provide these 3 methods.
 *
 * Concept: Interface
 *
 * Status flow: PENDING → ASSIGNED → COMPLETED
 */
public interface Trackable {

    // Returns the current status of the request
    String getStatus();

    // Updates the status to a new value
    void updateStatus(String newStatus);

    // Returns who the request is currently assigned to
    String getAssignedStaffName();
}
