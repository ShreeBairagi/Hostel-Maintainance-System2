package com.hostel.collections;

import com.hostel.exceptions.DuplicateRequestException;
import com.hostel.exceptions.InvalidRoomException;
import com.hostel.models.MaintenanceStaff;
import com.hostel.requests.Request;
import com.hostel.utils.FileHandler;
import com.hostel.utils.RequestIDGenerator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * RequestManager.java
 * --------------------
 * The brain of the application.
 * Manages all requests using three different Collections.
 *
 * Concepts: Collections (List, Set, Map) + Exception Handling
 *
 * Collections used:
 * List<Request> - stores ALL requests in order
 * Set<String> - stores room numbers that have OPEN requests
 * (Set automatically prevents duplicates)
 * Map<Integer, Request> - maps request ID → Request for fast lookup
 */
public class RequestManager {

    // List - keeps all requests in the order they were added
    private List<Request> allRequests;

    // Set - only holds rooms that currently have a PENDING or ASSIGNED request
    // Set is used here because it automatically rejects duplicate entries
    private Set<String> roomsWithOpenRequests;

    // Map - lets us find a request instantly by its ID (like a dictionary)
    private Map<Integer, Request> requestById;

    // List of maintenance staff members
    private List<MaintenanceStaff> allStaff;

    // Constructor - loads existing requests from file when app starts
    public RequestManager() {
        allRequests = new ArrayList<Request>();
        roomsWithOpenRequests = new HashSet<String>();
        requestById = new HashMap<Integer, Request>();
        allStaff = new ArrayList<MaintenanceStaff>();

        // Load saved requests from CSV into all three collections
        loadFromFile();
        
        // Load staff from CSV
        allStaff = FileHandler.loadAllStaff();
    }

    // ── Load all saved requests from CSV into memory ─────────────────────────
    private void loadFromFile() {
        List<Request> saved = FileHandler.loadAllRequests();

        for (Request r : saved) {
            allRequests.add(r);
            requestById.put(r.getRequestId(), r);

            // Only add to open rooms set if not completed
            if (!r.getStatus().equals("COMPLETED")) {
                roomsWithOpenRequests.add(r.getRoomNumber() + "-" + r.getRequestType());
            }
        }

        // Sync ID generator so new IDs don't clash with saved ones
        if (!allRequests.isEmpty()) {
            int maxId = 1000;
            for (Request r : allRequests) {
                if (r.getRequestId() > maxId) {
                    maxId = r.getRequestId();
                }
            }
            RequestIDGenerator.getInstance().setCurrentId(maxId);
        }

        System.out.println("Loaded " + allRequests.size() + " requests from file.");
    }

    // ── Add a new request ─────────────────────────────────────────────────────
    /**
     * Adds a new request after validating room number and checking for duplicates.
     *
     * Throws InvalidRoomException - if room number is empty or null
     * Throws DuplicateRequestException - if same room already has open request of
     * same type
     */
    public void addRequest(Request request)
            throws InvalidRoomException, DuplicateRequestException {

        String room = request.getRoomNumber();
        String type = request.getRequestType();

        // Validation 1 - room number cannot be empty
        if (room == null || room.trim().isEmpty()) {
            throw new InvalidRoomException(
                    "Room number cannot be empty.", room);
        }

        // Validation 2 - room number must be at least 3 characters (e.g. A101)
        if (room.trim().length() < 3) {
            throw new InvalidRoomException(
                    "Room number '" + room + "' is too short. Use format like A101.", room);
        }

        // Validation 3 - check for duplicate open request
        String key = room + "-" + type; // e.g. "A101-Electrical"
        if (roomsWithOpenRequests.contains(key)) {
            throw new DuplicateRequestException(
                    "A " + type + " request is already open for room " + room
                            + ". Please wait for it to be resolved.",
                    room, type);
        }

        // All checks passed - add to all three collections
        allRequests.add(request);
        requestById.put(request.getRequestId(), request);
        roomsWithOpenRequests.add(key);

        // Save to CSV file
        FileHandler.saveRequest(request);

        System.out.println("Request added: " + request);
    }

    // ── Assign a request to a staff member ───────────────────────────────────
    public boolean assignRequest(int requestId, String staffName) {

        Request r = requestById.get(requestId); // fast lookup using Map

        if (r == null) {
            System.out.println("No request found with ID: " + requestId);
            return false;
        }

        if (!r.getStatus().equals("PENDING")) {
            System.out.println("Request " + requestId + " is already " + r.getStatus());
            return false;
        }

        r.setAssignedStaffName(staffName);
        r.updateStatus("ASSIGNED");

        // Save updated list to file
        FileHandler.updateAllRequests(allRequests);

        System.out.println("Request " + requestId + " assigned to " + staffName);
        return true;
    }

    // ── Mark a request as completed ───────────────────────────────────────────
    public boolean completeRequest(int requestId) {

        Request r = requestById.get(requestId);

        if (r == null) {
            System.out.println("No request found with ID: " + requestId);
            return false;
        }

        if (!r.getStatus().equals("ASSIGNED")) {
            System.out.println("Request must be ASSIGNED before it can be COMPLETED.");
            return false;
        }

        r.updateStatus("COMPLETED");

        // Remove from open rooms set - this room can now submit a new request
        String key = r.getRoomNumber() + "-" + r.getRequestType();
        roomsWithOpenRequests.remove(key);

        FileHandler.updateAllRequests(allRequests);

        System.out.println("Request " + requestId + " marked as COMPLETED.");
        return true;
    }

    // ── Submit a rating for a completed request ───────────────────────────────
    public boolean submitRating(int requestId, int stars) {

        Request r = requestById.get(requestId);

        if (r == null) {
            System.out.println("No request found with ID: " + requestId);
            return false;
        }

        if (!r.getStatus().equals("COMPLETED")) {
            System.out.println("Request must be COMPLETED before it can be rated.");
            return false;
        }

        r.submitRating(stars); // uses Ratable interface method
        FileHandler.updateAllRequests(allRequests);
        FileHandler.saveRating(requestId, r.getStudentName(), stars);

        return true;
    }

    // ── Getters for fetching filtered lists ───────────────────────────────────

    // Returns all requests
    public List<Request> getAllRequests() {
        return allRequests;
    }

    // Returns only requests matching a specific status
    public List<Request> getRequestsByStatus(String status) {
        List<Request> result = new ArrayList<Request>();
        for (Request r : allRequests) {
            if (r.getStatus().equals(status)) {
                result.add(r);
            }
        }
        return result;
    }

    // Returns only requests submitted by a specific student (by name)
    public List<Request> getRequestsByStudent(String studentName) {
        List<Request> result = new ArrayList<Request>();
        for (Request r : allRequests) {
            if (r.getStudentName().equalsIgnoreCase(studentName)) {
                result.add(r);
            }
        }
        return result;
    }

    // Returns only requests matching a room number
    public List<Request> getRequestsByRoomNumber(String room) {
        List<Request> result = new ArrayList<Request>();
        for (Request r : allRequests) {
            if (r.getRoomNumber().equalsIgnoreCase(room)) {
                result.add(r);
            }
        }
        return result;
    }

    // ── Staff Management ──────────────────────────────────────────────────────

    public void addStaff(MaintenanceStaff staff) {
        allStaff.add(staff);
        FileHandler.saveStaff(staff);
    }

    public List<MaintenanceStaff> getAllStaff() {
        return allStaff;
    }

    // Returns a single request by ID using the Map
    public Request getRequestById(int requestId) {
        return requestById.get(requestId);
    }

    // Returns the set of rooms with open requests (for duplicate checking)
    public Set<String> getRoomsWithOpenRequests() {
        return roomsWithOpenRequests;
    }

    // Returns total number of requests
    public int getTotalCount() {
        return allRequests.size();
    }
}
