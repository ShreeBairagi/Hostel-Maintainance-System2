package com.hostel.models;

// Warden class - extends User
// Wardens can view all requests and assign them to maintenance staff
public class Warden extends User {

    private String officeRoom;
    private String hostelBlock; // which block this warden manages

    public Warden(int userId, String name, String email, String password, String officeRoom, String hostelBlock) {
        super(userId, name, email, password, "WARDEN");
        this.officeRoom = officeRoom;
        this.hostelBlock = hostelBlock;
    }

    @Override
    public void displayInfo() {
        System.out.println("---- Warden Info ----");
        System.out.println("Name        : " + getName());
        System.out.println("Office Room : " + officeRoom);
        System.out.println("Block       : " + hostelBlock);
        System.out.println("---------------------");
    }

    public String getOfficeRoom() {
        return officeRoom;
    }

    public void setOfficeRoom(String officeRoom) {
        this.officeRoom = officeRoom;
    }

    public String getHostelBlock() {
        return hostelBlock;
    }

    public void setHostelBlock(String hostelBlock) {
        this.hostelBlock = hostelBlock;
    }
}