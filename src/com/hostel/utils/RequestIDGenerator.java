package com.hostel.utils;

/**
 * RequestIDGenerator.java
 * ------------------------
 * Singleton class - only ONE instance of this class can ever exist.
 * It generates a unique ID for every new maintenance request.
 *
 * Concept: Singleton Design Pattern
 *
 * Why Singleton here?
 * If two parts of the app create separate ID generators, we could
 * get duplicate IDs like REQ-5 appearing twice. Singleton prevents that.
 *
 * How to use:
 * RequestIDGenerator gen = RequestIDGenerator.getInstance();
 * int newId = gen.generateID();
 */
public class RequestIDGenerator {

    // The one and only instance - private so nobody can access it directly
    private static RequestIDGenerator instance = null;

    // Keeps track of the last ID that was given out
    private int currentId;

    // Private constructor - nobody outside this class can call new
    // RequestIDGenerator()
    private RequestIDGenerator() {
        this.currentId = 1000; // IDs will start from 1001, 1002, 1003 ...
    }

    /**
     * This is the only way to get the generator.
     * First call creates it. Every call after that returns the same one.
     */
    public static RequestIDGenerator getInstance() {
        if (instance == null) {
            instance = new RequestIDGenerator();
            System.out.println("RequestIDGenerator created.");
        }
        return instance;
    }

    /**
     * Generates the next unique ID.
     * Each call returns a number one higher than the last.
     */
    public int generateID() {
        currentId++;
        return currentId;
    }

    // Useful if we want to sync with IDs already saved in CSV
    public void setCurrentId(int id) {
        this.currentId = id;
    }

    public int getCurrentId() {
        return currentId;
    }
}