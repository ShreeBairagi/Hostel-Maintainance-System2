package com.hostel.utils;

import com.hostel.requests.CarpentryRequest;
import com.hostel.requests.ElectricalRequest;
import com.hostel.requests.OtherRequest;
import com.hostel.requests.PlumbingRequest;
import com.hostel.requests.Request;
import com.hostel.models.MaintenanceStaff;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * FileHandler.java
 * ----------------
 * Handles all reading and writing of data to CSV files.
 *
 * CSV column order (must match Request.toCSV() exactly):
 *   0  RequestID
 *   1  StudentName
 *   2  RoomNumber
 *   3  Type
 *   4  Description
 *   5  Status
 *   6  AssignedStaff
 *   7  SubmittedAt
 *   8  Priority
 *   9  Rating
 *   10 PhotoPath
 *
 * Concept: File I/O
 */
public class FileHandler {

    private static final String REQUESTS_FILE = "requests.csv";
    private static final String RATINGS_FILE  = "ratings.csv";
    private static final String STAFF_FILE    = "staff.csv";

    private static final String CSV_HEADER =
        "RequestID,StudentName,RoomNumber,Type,Description,Status,AssignedStaff,SubmittedAt,Priority,Rating,PhotoPath";

    // ── Append one new request to the CSV file ───────────────────────────────
    public static void saveRequest(Request request) {
        try {
            File    file  = new File(REQUESTS_FILE);
            boolean isNew = !file.exists();

            // true = append mode, so existing entries are not deleted
            BufferedWriter bw = new BufferedWriter(new FileWriter(file, true));

            if (isNew) {
                bw.write(CSV_HEADER);
                bw.newLine();
            }

            bw.write(request.toCSV());
            bw.newLine();
            bw.close();

            System.out.println("Request saved to file.");

        } catch (IOException e) {
            System.out.println("ERROR saving request: " + e.getMessage());
        }
    }

    // ── Read all requests from the CSV file ──────────────────────────────────
    public static List<Request> loadAllRequests() {

        List<Request> list = new ArrayList<Request>();
        File file = new File(REQUESTS_FILE);

        if (!file.exists()) {
            return list;  // no file yet, return empty list
        }

        try {
            BufferedReader br      = new BufferedReader(new FileReader(file));
            String         line;
            boolean        isFirst = true;

            while ((line = br.readLine()) != null) {

                // Skip the header row
                if (isFirst) {
                    isFirst = false;
                    continue;
                }

                // Skip blank lines
                if (line.trim().isEmpty()) {
                    continue;
                }

                String[] col = line.split(",");

                // Need all 11 columns - skip broken lines
                if (col.length < 11) {
                    System.out.println("Skipping incomplete line: " + line);
                    continue;
                }

                // Parse each column by its fixed position
                int    requestId   = Integer.parseInt(col[0].trim());
                String studentName = col[1].trim();
                String roomNumber  = col[2].trim();
                String type        = col[3].trim();
                String description = col[4].trim().replace(";", ","); // restore commas
                String status      = col[5].trim();
                String staffName   = col[6].trim();
                String submittedAt = col[7].trim();
                // col[8] = priority - skip, it is set automatically by the subclass
                int    rating      = Integer.parseInt(col[9].trim());
                String photoPath   = col[10].trim();

                // Build the correct subclass based on the type column
                Request request = null;

                if (type.equals("Plumbing")) {
                    request = new PlumbingRequest(requestId, studentName,
                                roomNumber, description, photoPath, submittedAt);

                } else if (type.equals("Electrical")) {
                    request = new ElectricalRequest(requestId, studentName,
                                roomNumber, description, photoPath, submittedAt);

                } else if (type.equals("Carpentry")) {
                    request = new CarpentryRequest(requestId, studentName,
                                roomNumber, description, photoPath, submittedAt);

                } else if (type.equals("Other")) {
                    request = new OtherRequest(requestId, studentName,
                                roomNumber, description, photoPath, submittedAt);

                } else {
                    // Unknown type - default to Plumbing so app doesn't crash
                    System.out.println("Unknown type '" + type + "' - loading as Plumbing.");
                    request = new PlumbingRequest(requestId, studentName,
                                roomNumber, description, photoPath, submittedAt);
                }

                // Restore the fields that can change over time
                request.setStatus(status);
                request.setAssignedStaffName(staffName);
                request.setRating(rating);

                list.add(request);
            }

            br.close();

        } catch (IOException e) {
            System.out.println("ERROR loading requests: " + e.getMessage());
        } catch (NumberFormatException e) {
            System.out.println("ERROR reading number from CSV: " + e.getMessage());
        }

        return list;
    }

    // ── Overwrite the entire file (used after updating status or rating) ─────
    public static void updateAllRequests(List<Request> list) {
        try {
            // false = overwrite mode
            BufferedWriter bw = new BufferedWriter(new FileWriter(REQUESTS_FILE, false));

            bw.write(CSV_HEADER);
            bw.newLine();

            for (Request r : list) {
                bw.write(r.toCSV());
                bw.newLine();
            }

            bw.close();
            System.out.println("File updated successfully.");

        } catch (IOException e) {
            System.out.println("ERROR updating file: " + e.getMessage());
        }
    }

    // ── Append one rating entry to ratings.csv ───────────────────────────────
    public static void saveRating(int requestId, String studentName, int rating) {
        try {
            File    file  = new File(RATINGS_FILE);
            boolean isNew = !file.exists();

            BufferedWriter bw = new BufferedWriter(new FileWriter(file, true));

            if (isNew) {
                bw.write("RequestID,StudentName,Rating");
                bw.newLine();
            }

            bw.write(requestId + "," + studentName + "," + rating);
            bw.newLine();
            bw.close();

        } catch (IOException e) {
            System.out.println("ERROR saving rating: " + e.getMessage());
        }
    }

    // ── Append one new staff to staff.csv ────────────────────────────────────
    public static void saveStaff(MaintenanceStaff staff) {
        try {
            File    file  = new File(STAFF_FILE);
            boolean isNew = !file.exists();

            BufferedWriter bw = new BufferedWriter(new FileWriter(file, true));

            if (isNew) {
                bw.write("UserId,Name,Email,Password,Specialization,IsAvailable,CompletedTasks");
                bw.newLine();
            }

            bw.write(staff.getUserId() + "," + staff.getName() + "," + staff.getEmail() + "," +
                     staff.getPassword() + "," + staff.getSpecialization() + "," +
                     staff.isAvailable() + "," + staff.getCompletedTasksCount());
            bw.newLine();
            bw.close();

            System.out.println("Staff saved to file.");

        } catch (IOException e) {
            System.out.println("ERROR saving staff: " + e.getMessage());
        }
    }

    // ── Read all staff from staff.csv ────────────────────────────────────────
    public static List<MaintenanceStaff> loadAllStaff() {
        List<MaintenanceStaff> list = new ArrayList<MaintenanceStaff>();
        File file = new File(STAFF_FILE);

        if (!file.exists()) {
            return list;
        }

        try {
            BufferedReader br      = new BufferedReader(new FileReader(file));
            String         line;
            boolean        isFirst = true;

            while ((line = br.readLine()) != null) {
                if (isFirst) {
                    isFirst = false;
                    continue;
                }

                if (line.trim().isEmpty()) continue;

                String[] col = line.split(",");
                if (col.length < 7) continue;

                int userId = Integer.parseInt(col[0].trim());
                String name = col[1].trim();
                String email = col[2].trim();
                String password = col[3].trim();
                String specialization = col[4].trim();
                boolean isAvailable = Boolean.parseBoolean(col[5].trim());
                int completedTasks = Integer.parseInt(col[6].trim());

                MaintenanceStaff staff = new MaintenanceStaff(userId, name, email, password, specialization);
                staff.setAvailable(isAvailable);
                staff.setCompletedTasksCount(completedTasks);

                list.add(staff);
            }
            br.close();

        } catch (IOException e) {
            System.out.println("ERROR loading staff: " + e.getMessage());
        } catch (NumberFormatException e) {
            System.out.println("ERROR reading number from staff CSV: " + e.getMessage());
        }

        return list;
    }

    // ── Overwrite staff.csv (used after updates) ─────────────────────────────
    public static void updateAllStaff(List<MaintenanceStaff> list) {
        try {
            BufferedWriter bw = new BufferedWriter(new FileWriter(STAFF_FILE, false));

            bw.write("UserId,Name,Email,Password,Specialization,IsAvailable,CompletedTasks");
            bw.newLine();

            for (MaintenanceStaff s : list) {
                bw.write(s.getUserId() + "," + s.getName() + "," + s.getEmail() + "," +
                         s.getPassword() + "," + s.getSpecialization() + "," +
                         s.isAvailable() + "," + s.getCompletedTasksCount());
                bw.newLine();
            }

            bw.close();
        } catch (IOException e) {
            System.out.println("ERROR updating staff file: " + e.getMessage());
        }
    }
}