package com.hostel.models;

// MaintenanceStaff class - extends User
// Staff members are assigned requests by the warden and mark them complete
public class MaintenanceStaff extends User {

    private String specialization; // e.g. "Plumbing", "Electrical", "Carpentry"
    private boolean isAvailable;
    private int completedTasksCount;

    public MaintenanceStaff(int userId, String name, String email, String password, String specialization) {
        super(userId, name, email, password, "STAFF");
        this.specialization = specialization;
        this.isAvailable = true;
        this.completedTasksCount = 0;
    }

    @Override
    public void displayInfo() {
        System.out.println("---- Maintenance Staff Info ----");
        System.out.println("Name           : " + getName());
        System.out.println("Specialization : " + specialization);
        System.out.println("Available      : " + (isAvailable ? "Yes" : "No"));
        System.out.println("Tasks Done     : " + completedTasksCount);
        System.out.println("--------------------------------");
    }

    public void incrementTaskCount() {
        this.completedTasksCount++;
    }

    // Getters and Setters
    public String getSpecialization() {
        return specialization;
    }

    public void setSpecialization(String specialization) {
        this.specialization = specialization;
    }

    public boolean isAvailable() {
        return isAvailable;
    }

    public void setAvailable(boolean available) {
        isAvailable = available;
    }

    public int getCompletedTasksCount() {
        return completedTasksCount;
    }

    public void setCompletedTasksCount(int completedTasksCount) {
        this.completedTasksCount = completedTasksCount;
    }
}