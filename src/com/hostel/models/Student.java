package com.hostel.models;

// Student class - extends User
// Students can submit requests and rate completed work
public class Student extends User {

    private String roomNumber;
    private String hostelBlock;
    private int year; // year of study

    public Student(int userId, String name, String email, String password, String roomNumber, String hostelBlock,
            int year) {
        super(userId, name, email, password, "STUDENT");
        this.roomNumber = roomNumber;
        this.hostelBlock = hostelBlock;
        this.year = year;
    }

    @Override
    public void displayInfo() {
        System.out.println("---- Student Info ----");
        System.out.println("Name       : " + getName());
        System.out.println("Room No    : " + roomNumber);
        System.out.println("Block      : " + hostelBlock);
        System.out.println("Year       : " + year);
        System.out.println("----------------------");
    }

    // Getters and Setters
    public String getRoomNumber() {
        return roomNumber;
    }

    public void setRoomNumber(String roomNumber) {
        this.roomNumber = roomNumber;
    }

    public String getHostelBlock() {
        return hostelBlock;
    }

    public void setHostelBlock(String hostelBlock) {
        this.hostelBlock = hostelBlock;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }
}