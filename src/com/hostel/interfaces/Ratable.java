package com.hostel.interfaces;

/**
 * Ratable.java
 * ---------------
 * Interface for anything that can be rated by a student.
 * Only COMPLETED requests should be rated.
 * Rating scale: 1 (worst) to 5 (best)
 *
 * Concept: Interface
 */
public interface Ratable {

    // Student submits their rating
    void submitRating(int stars);

    // Returns the rating that was given (0 means not rated yet)
    int getRating();
}