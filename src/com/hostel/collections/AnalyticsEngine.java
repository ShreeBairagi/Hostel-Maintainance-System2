package com.hostel.collections;

import com.hostel.requests.Request;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * AnalyticsEngine.java
 * ---------------------
 * Provides useful stats and insights for the warden/admin.
 * Answers questions like:
 * - Which type of issue is most common?
 * - What is the average rating given to staff?
 * - How many requests are pending right now?
 * - Which staff member has completed the most tasks?
 *
 * Concept: Collections (Map for counting), iteration, basic analytics
 */
public class AnalyticsEngine {

    // Reference to the RequestManager so we can access all requests
    private RequestManager requestManager;

    public AnalyticsEngine(RequestManager requestManager) {
        this.requestManager = requestManager;
    }

    // ── Count requests by status ──────────────────────────────────────────────
    public int countByStatus(String status) {
        int count = 0;
        for (Request r : requestManager.getAllRequests()) {
            if (r.getStatus().equals(status)) {
                count++;
            }
        }
        return count;
    }

    // ── Find the most common request type ────────────────────────────────────
    /**
     * Uses a Map to count how many times each type appears.
     * Then finds the type with the highest count.
     * Returns something like "Electrical (12 requests)"
     */
    public String getMostCommonRequestType() {

        Map<String, Integer> typeCounts = new HashMap<String, Integer>();

        for (Request r : requestManager.getAllRequests()) {
            String type = r.getRequestType();

            if (typeCounts.containsKey(type)) {
                typeCounts.put(type, typeCounts.get(type) + 1);
            } else {
                typeCounts.put(type, 1);
            }
        }

        if (typeCounts.isEmpty()) {
            return "No requests yet.";
        }

        // Find the type with the highest count
        String mostCommon = "";
        int highestCount = 0;

        for (Map.Entry<String, Integer> entry : typeCounts.entrySet()) {
            if (entry.getValue() > highestCount) {
                highestCount = entry.getValue();
                mostCommon = entry.getKey();
            }
        }

        return mostCommon + " (" + highestCount + " requests)";
    }

    // ── Calculate average rating across all rated requests ───────────────────
    public double getAverageRating() {

        int total = 0;
        int count = 0;

        for (Request r : requestManager.getAllRequests()) {
            if (r.getRating() > 0) { // 0 means not rated yet
                total += r.getRating();
                count++;
            }
        }

        if (count == 0) {
            return 0.0; // no ratings yet
        }

        // Round to 2 decimal places
        double avg = (double) total / count;
        return Math.round(avg * 100.0) / 100.0;
    }

    // ── Find which staff member completed the most requests ──────────────────
    public String getTopPerformingStaff() {

        Map<String, Integer> staffCounts = new HashMap<String, Integer>();

        for (Request r : requestManager.getAllRequests()) {
            if (r.getStatus().equals("COMPLETED")) {
                String staffName = r.getAssignedStaffName();

                if (staffCounts.containsKey(staffName)) {
                    staffCounts.put(staffName, staffCounts.get(staffName) + 1);
                } else {
                    staffCounts.put(staffName, 1);
                }
            }
        }

        if (staffCounts.isEmpty()) {
            return "No completed requests yet.";
        }

        String topStaff = "";
        int topCount = 0;

        for (Map.Entry<String, Integer> entry : staffCounts.entrySet()) {
            if (entry.getValue() > topCount) {
                topCount = entry.getValue();
                topStaff = entry.getKey();
            }
        }

        return topStaff + " (" + topCount + " tasks completed)";
    }

    // ── Get a full summary for the analytics panel ───────────────────────────
    public List<String> getFullSummary() {

        List<String> summary = new ArrayList<String>();

        summary.add("Total Requests   : " + requestManager.getTotalCount());
        summary.add("Pending          : " + countByStatus("PENDING"));
        summary.add("Assigned         : " + countByStatus("ASSIGNED"));
        summary.add("Completed        : " + countByStatus("COMPLETED"));
        summary.add("Most Common Issue: " + getMostCommonRequestType());
        summary.add("Average Rating   : " + getAverageRating() + " / 5.0");
        summary.add("Top Staff Member : " + getTopPerformingStaff());

        return summary;
    }
}
