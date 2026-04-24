package com.hostel.gui;

import com.hostel.collections.AnalyticsEngine;
import com.hostel.collections.RequestManager;
import com.hostel.exceptions.DuplicateRequestException;
import com.hostel.exceptions.InvalidRoomException;
import com.hostel.requests.CarpentryRequest;
import com.hostel.requests.ElectricalRequest;
import com.hostel.requests.OtherRequest;
import com.hostel.requests.PlumbingRequest;
import com.hostel.requests.Request;
import com.hostel.models.MaintenanceStaff;
import com.hostel.utils.RequestIDGenerator;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

/**
 * MainApp.java
 * -------------
 * The main GUI for the Hostel Maintenance Request Tracker.
 * Built using Java Swing.
 *
 * Modified for Demo Mode: No login screen. Instead, a persistent top 
 * navigation bar lets the presenter seamlessly toggle between views.
 */
public class MainApp extends JFrame {
    private static final long serialVersionUID = 1L;

    // ── App-wide shared objects ───────────────────────────────────────────────
    private RequestManager requestManager;
    private AnalyticsEngine analyticsEngine;

    // ── Card layout to switch between screens ────────────────────────────────
    private CardLayout cardLayout;
    private JPanel mainPanel;

    // Screen name constants
    private static final String SCREEN_STUDENT = "STUDENT";
    private static final String SCREEN_WARDEN = "WARDEN";
    private static final String SCREEN_WORKER = "WORKER";

    // ── Global Context UI Elements ────────────────────────────────────────────
    private JComboBox<String> viewToggleCombo;
    private JPanel contextPanel;
    private CardLayout contextCardLayout;
    private JTextField simRoomField;
    private JComboBox<String> simWorkerCombo;
    private Runnable refreshAnalyticsAction;

    // ── Colour theme ──────────────────────────────────────────────────────────
    private static final Color COLOR_BG = new Color(245, 247, 250);
    private static final Color COLOR_PRIMARY = new Color(37, 99, 235);
    private static final Color COLOR_SUCCESS = new Color(22, 163, 74);
    private static final Color COLOR_DANGER = new Color(220, 38, 38);
    private static final Color COLOR_WARNING = new Color(234, 179, 8);
    private static final Color COLOR_WHITE = Color.WHITE;
    private static final Color COLOR_CARD = new Color(255, 255, 255);
    private static final Color COLOR_TEXT = new Color(30, 30, 30);
    private static final Color COLOR_BORDER = new Color(220, 220, 230);

    // ── Fonts ─────────────────────────────────────────────────────────────────
    private static final Font FONT_HEADER = new Font("Segoe UI", Font.BOLD, 15);
    private static final Font FONT_BODY = new Font("Segoe UI", Font.PLAIN, 13);
    private static final Font FONT_SMALL = new Font("Segoe UI", Font.PLAIN, 11);
    private static final Font FONT_BUTTON = new Font("Segoe UI", Font.BOLD, 13);

    // ══════════════════════════════════════════════════════════════════════════
    // CONSTRUCTOR
    // ══════════════════════════════════════════════════════════════════════════
    public MainApp() {
        requestManager = new RequestManager();
        analyticsEngine = new AnalyticsEngine(requestManager);

        setTitle("Hostel Maintenance Tracker - DEMO MODE");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null); // centre on screen
        setBackground(COLOR_BG);
        setLayout(new BorderLayout());

        // Top Navigation Bar
        add(buildTopNavigationBar(), BorderLayout.NORTH);

        // Main Content Area
        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);

        mainPanel.add(buildStudentPanel(), SCREEN_STUDENT);
        mainPanel.add(buildWardenPanel(), SCREEN_WARDEN);
        mainPanel.add(buildWorkerPanel(), SCREEN_WORKER);

        add(mainPanel, BorderLayout.CENTER);
        
        // Initialize to Student View
        cardLayout.show(mainPanel, SCREEN_STUDENT);
        contextCardLayout.show(contextPanel, SCREEN_STUDENT);
        refreshAllData();

        setVisible(true);
    }

    // ══════════════════════════════════════════════════════════════════════════
    // HELPER METHODS
    // ══════════════════════════════════════════════════════════════════════════
    private JButton makeButton(String text, Color bg, Color fg) {
        JButton btn = new JButton(text);
        btn.setFont(FONT_BUTTON);
        btn.setBackground(bg);
        btn.setForeground(fg);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setBorder(new EmptyBorder(8, 16, 8, 16));
        btn.setOpaque(true);
        return btn;
    }

    private JTextField makeField(int cols) {
        JTextField f = new JTextField(cols);
        f.setFont(FONT_BODY);
        f.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(COLOR_BORDER, 1, true),
                new EmptyBorder(6, 10, 6, 10)));
        return f;
    }

    private JLabel makeLabel(String text, Font font, Color color) {
        JLabel lbl = new JLabel(text);
        lbl.setFont(font);
        lbl.setForeground(color);
        return lbl;
    }

    // ══════════════════════════════════════════════════════════════════════════
    // GLOBAL NAVIGATION BAR
    // ══════════════════════════════════════════════════════════════════════════
    private JPanel buildTopNavigationBar() {
        JPanel navBar = new JPanel(new BorderLayout());
        navBar.setBackground(COLOR_PRIMARY);
        navBar.setBorder(new EmptyBorder(12, 20, 12, 20));

        // Left Side: Toggle View
        JPanel leftPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        leftPanel.setBackground(COLOR_PRIMARY);
        leftPanel.add(makeLabel("DEMO VIEW: ", FONT_HEADER, COLOR_WHITE));
        
        viewToggleCombo = new JComboBox<>(new String[]{"Student", "Warden", "Maintenance Worker"});
        viewToggleCombo.setFont(FONT_BODY);
        leftPanel.add(viewToggleCombo);

        // Right Side: Dynamic Context
        contextCardLayout = new CardLayout();
        contextPanel = new JPanel(contextCardLayout);
        contextPanel.setBackground(COLOR_PRIMARY);

        // -- Context: Student
        JPanel studentContext = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        studentContext.setBackground(COLOR_PRIMARY);
        studentContext.add(makeLabel("Simulated Room Number:", FONT_BODY, COLOR_WHITE));
        simRoomField = makeField(8);
        simRoomField.setText("A101");
        studentContext.add(simRoomField);

        // -- Context: Warden (Empty)
        JPanel wardenContext = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        wardenContext.setBackground(COLOR_PRIMARY);

        // -- Context: Worker
        JPanel workerContext = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        workerContext.setBackground(COLOR_PRIMARY);
        workerContext.add(makeLabel("Simulated Worker:", FONT_BODY, COLOR_WHITE));
        simWorkerCombo = new JComboBox<>();
        simWorkerCombo.addActionListener(e -> {
            if (simWorkerCombo.getSelectedItem() != null) {
                refreshWorkerPanel();
            }
        });
        workerContext.add(simWorkerCombo);

        contextPanel.add(studentContext, SCREEN_STUDENT);
        contextPanel.add(wardenContext, SCREEN_WARDEN);
        contextPanel.add(workerContext, SCREEN_WORKER);

        // Apply Button
        JButton refreshBtn = makeButton("Refresh Data", COLOR_WHITE, COLOR_PRIMARY);
        refreshBtn.addActionListener(e -> refreshAllData());

        JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        rightPanel.setBackground(COLOR_PRIMARY);
        rightPanel.add(contextPanel);
        rightPanel.add(refreshBtn);

        navBar.add(leftPanel, BorderLayout.WEST);
        navBar.add(rightPanel, BorderLayout.EAST);

        // Action listener for toggle
        viewToggleCombo.addActionListener(e -> {
            String selected = (String) viewToggleCombo.getSelectedItem();
            if ("Student".equals(selected)) {
                cardLayout.show(mainPanel, SCREEN_STUDENT);
                contextCardLayout.show(contextPanel, SCREEN_STUDENT);
            } else if ("Warden".equals(selected)) {
                cardLayout.show(mainPanel, SCREEN_WARDEN);
                contextCardLayout.show(contextPanel, SCREEN_WARDEN);
            } else if ("Maintenance Worker".equals(selected)) {
                cardLayout.show(mainPanel, SCREEN_WORKER);
                contextCardLayout.show(contextPanel, SCREEN_WORKER);
            }
            refreshAllData();
        });

        return navBar;
    }

    private void refreshAllData() {
        updateStaffDropdowns();
        refreshStudentPanel();
        refreshWardenTable();
        refreshWorkerPanel();
        
        if (refreshAnalyticsAction != null) {
            refreshAnalyticsAction.run();
        }
        // Re-populating staff combo ensures if warden added staff, it shows up for worker selection.
    }

    private String getSimulatedRoom() {
        return simRoomField.getText().trim();
    }

    private String getSimulatedWorker() {
        return (String) simWorkerCombo.getSelectedItem();
    }

    // ══════════════════════════════════════════════════════════════════════════
    // SCREEN - STUDENT PANEL
    // ══════════════════════════════════════════════════════════════════════════

    private DefaultTableModel studentTableModel;
    private JLabel studentWelcomeLabel;

    private JPanel buildStudentPanel() {
        JPanel screen = new JPanel(new BorderLayout(0, 0));
        screen.setBackground(COLOR_BG);

        // Tab pane
        JTabbedPane tabs = new JTabbedPane();
        tabs.setFont(FONT_BODY);
        tabs.setBackground(COLOR_BG);

        tabs.addTab("📋 Submit Request", buildSubmitTab());
        tabs.addTab("📂 My Requests", buildMyRequestsTab());
        tabs.addTab("⭐ Rate Service", buildRatingTab());

        screen.add(tabs, BorderLayout.CENTER);
        return screen;
    }

    // ── Tab: Submit a new request ──
    private JPanel buildSubmitTab() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(COLOR_BG);
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));

        JPanel form = new JPanel(new GridBagLayout());
        form.setBackground(COLOR_CARD);
        form.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(COLOR_BORDER, 1, true),
                new EmptyBorder(30, 40, 30, 40)));

        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(8, 8, 8, 8);
        c.anchor = GridBagConstraints.WEST;
        c.fill = GridBagConstraints.HORIZONTAL;

        c.gridx = 0; c.gridy = 0; c.gridwidth = 2;
        form.add(makeLabel("Submit a Maintenance Request", FONT_HEADER, COLOR_TEXT), c);
        c.gridwidth = 1;

        // Request Type
        c.gridx = 0; c.gridy = 1;
        form.add(makeLabel("Request Type:", FONT_BODY, COLOR_TEXT), c);
        String[] types = { "Electrical", "Plumbing", "Carpentry", "Other" };
        JComboBox<String> typeCombo = new JComboBox<>(types);
        typeCombo.setFont(FONT_BODY);
        c.gridx = 1;
        form.add(typeCombo, c);

        // Specific Issue
        c.gridx = 0; c.gridy = 2;
        JLabel issueTitle = makeLabel("Specific Issue:", FONT_BODY, COLOR_TEXT);
        form.add(issueTitle, c);
        JComboBox<String> specificCombo = new JComboBox<>(new String[]{"Fan not working", "Light fused", "Switch broken", "Other"});
        specificCombo.setFont(FONT_BODY);
        c.gridx = 1;
        form.add(specificCombo, c);

        // Description
        c.gridx = 0; c.gridy = 3;
        JLabel descTitle = makeLabel("Description (Optional):", FONT_BODY, COLOR_TEXT);
        form.add(descTitle, c);
        
        // Add action listener to specificCombo to handle the description requirement correctly
        specificCombo.addActionListener(e -> {
            String selectedSpecific = (String) specificCombo.getSelectedItem();
            String selectedType = (String) typeCombo.getSelectedItem();
            if ("Other".equals(selectedType) || "Other".equals(selectedSpecific) || "Custom (Write below)".equals(selectedSpecific)) {
                descTitle.setText("Description (Required):");
            } else {
                descTitle.setText("Description (Optional):");
            }
        });

        JTextArea descArea = new JTextArea(3, 18);
        descArea.setFont(FONT_BODY);
        descArea.setLineWrap(true);
        descArea.setWrapStyleWord(true);
        descArea.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(COLOR_BORDER, 1),
                new EmptyBorder(6, 8, 6, 8)));
        JScrollPane descScroll = new JScrollPane(descArea);
        c.gridx = 1;
        form.add(descScroll, c);

        // Type action listener to update specific combo
        typeCombo.addActionListener(e -> {
            String selected = (String) typeCombo.getSelectedItem();
            // We temporarily remove the listener to avoid rapid consecutive triggers during remove/add
            java.awt.event.ActionListener[] listeners = specificCombo.getActionListeners();
            for (java.awt.event.ActionListener al : listeners) {
                specificCombo.removeActionListener(al);
            }
            
            specificCombo.removeAllItems();
            if ("Electrical".equals(selected)) {
                specificCombo.addItem("Fan not working");
                specificCombo.addItem("Light fused");
                specificCombo.addItem("Switch broken");
                specificCombo.addItem("Other");
            } else if ("Plumbing".equals(selected)) {
                specificCombo.addItem("Leaking tap");
                specificCombo.addItem("Blocked pipe");
                specificCombo.addItem("No water supply");
                specificCombo.addItem("Other");
            } else if ("Carpentry".equals(selected)) {
                specificCombo.addItem("Broken chair");
                specificCombo.addItem("Door hinge loose");
                specificCombo.addItem("Bed repair");
                specificCombo.addItem("Other");
            } else {
                specificCombo.addItem("Custom (Write below)");
            }
            
            for (java.awt.event.ActionListener al : listeners) {
                specificCombo.addActionListener(al);
            }
            
            // Manually trigger the listener once after adding all items
            if (listeners.length > 0) {
                listeners[0].actionPerformed(new java.awt.event.ActionEvent(specificCombo, java.awt.event.ActionEvent.ACTION_PERFORMED, ""));
            }
        });

        // Photo Path
        c.gridx = 0; c.gridy = 4;
        form.add(makeLabel("Photo Path (optional):", FONT_BODY, COLOR_TEXT), c);
        JTextField photoField = makeField(18);
        photoField.setText("photos/");
        c.gridx = 1;
        form.add(photoField, c);

        // Submit button
        JButton submitBtn = makeButton("Submit Request", COLOR_PRIMARY, COLOR_WHITE);
        c.gridx = 0; c.gridy = 5; c.gridwidth = 2; c.anchor = GridBagConstraints.CENTER; c.fill = GridBagConstraints.NONE;
        form.add(submitBtn, c);

        JLabel statusLabel = makeLabel("", FONT_SMALL, COLOR_SUCCESS);
        c.gridy = 6;
        form.add(statusLabel, c);

        submitBtn.addActionListener(e -> {
            String room = getSimulatedRoom();
            if (room.isEmpty()) {
                statusLabel.setForeground(COLOR_DANGER);
                statusLabel.setText("✘ Please enter a Simulated Room Number in the Top Bar.");
                return;
            }

            String type = (String) typeCombo.getSelectedItem();
            String specific = (String) specificCombo.getSelectedItem();
            String customDesc = descArea.getText().trim();
            String photo = photoField.getText().trim();

            String finalDesc = "";
            if ("Other".equals(type) || "Other".equals(specific) || "Custom (Write below)".equals(specific)) {
                if (customDesc.isEmpty()) {
                    statusLabel.setForeground(COLOR_DANGER);
                    statusLabel.setText("✘ Please enter a description for 'Other'.");
                    return;
                }
                finalDesc = customDesc;
            } else {
                finalDesc = specific + (customDesc.isEmpty() ? "" : " - " + customDesc);
            }

            int newId = RequestIDGenerator.getInstance().generateID();
            Request request = null;

            if ("Electrical".equals(type)) {
                request = new ElectricalRequest(newId, "Student", room, finalDesc, photo);
            } else if ("Plumbing".equals(type)) {
                request = new PlumbingRequest(newId, "Student", room, finalDesc, photo);
            } else if ("Carpentry".equals(type)) {
                request = new CarpentryRequest(newId, "Student", room, finalDesc, photo);
            } else {
                request = new OtherRequest(newId, "Student", room, finalDesc, photo);
            }

            try {
                requestManager.addRequest(request);
                statusLabel.setForeground(COLOR_SUCCESS);
                statusLabel.setText("✔ Request submitted! ID: REQ-" + newId);
                descArea.setText("");
                photoField.setText("photos/");
                refreshStudentPanel(); // auto-refresh table
            } catch (InvalidRoomException ex) {
                statusLabel.setForeground(COLOR_DANGER);
                statusLabel.setText("✘ " + ex.getMessage());
            } catch (DuplicateRequestException ex) {
                statusLabel.setForeground(COLOR_WARNING);
                statusLabel.setText("✘ " + ex.getMessage());
            }
        });

        panel.add(form);
        return panel;
    }

    // ── Tab: View my requests ──
    private JPanel buildMyRequestsTab() {
        JPanel panel = new JPanel(new BorderLayout(0, 10));
        panel.setBackground(COLOR_BG);
        panel.setBorder(new EmptyBorder(16, 16, 16, 16));

        String[] cols = { "ID", "Type", "Room", "Description", "Priority", "Status", "Staff", "Submitted" };
        studentTableModel = new DefaultTableModel(cols, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };

        JTable table = new JTable(studentTableModel);
        table.setFont(FONT_BODY);
        table.setRowHeight(28);
        table.getTableHeader().setFont(FONT_BODY);
        table.getTableHeader().setBackground(COLOR_PRIMARY);
        table.getTableHeader().setForeground(COLOR_WHITE);
        table.setSelectionBackground(new Color(219, 234, 254));
        table.setGridColor(COLOR_BORDER);

        JScrollPane scroll = new JScrollPane(table);

        studentWelcomeLabel = makeLabel("Requests for Room: ", FONT_HEADER, COLOR_TEXT);
        panel.add(studentWelcomeLabel, BorderLayout.NORTH);
        panel.add(scroll, BorderLayout.CENTER);

        return panel;
    }

    // ── Tab: Rate a completed request ──
    private JPanel buildRatingTab() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(COLOR_BG);

        JPanel form = new JPanel(new GridBagLayout());
        form.setBackground(COLOR_CARD);
        form.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(COLOR_BORDER, 1, true),
                new EmptyBorder(30, 40, 30, 40)));

        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(10, 8, 10, 8);
        c.anchor = GridBagConstraints.WEST;
        c.fill = GridBagConstraints.HORIZONTAL;

        c.gridx = 0; c.gridy = 0; c.gridwidth = 2;
        form.add(makeLabel("Rate a Completed Request", FONT_HEADER, COLOR_TEXT), c);
        c.gridwidth = 1;

        c.gridx = 0; c.gridy = 1;
        form.add(makeLabel("Request ID:", FONT_BODY, COLOR_TEXT), c);
        JTextField idField = makeField(10);
        c.gridx = 1;
        form.add(idField, c);

        c.gridx = 0; c.gridy = 2;
        form.add(makeLabel("Rating (1 to 5):", FONT_BODY, COLOR_TEXT), c);
        String[] stars = { "5 ⭐⭐⭐⭐⭐", "4 ⭐⭐⭐⭐", "3 ⭐⭐⭐", "2 ⭐⭐", "1 ⭐" };
        JComboBox<String> ratingCombo = new JComboBox<>(stars);
        ratingCombo.setFont(FONT_BODY);
        c.gridx = 1;
        form.add(ratingCombo, c);

        JButton rateBtn = makeButton("Submit Rating", COLOR_SUCCESS, COLOR_WHITE);
        JLabel rateLabel = makeLabel("", FONT_SMALL, COLOR_SUCCESS);

        c.gridx = 0; c.gridy = 3; c.gridwidth = 2; c.anchor = GridBagConstraints.CENTER; c.fill = GridBagConstraints.NONE;
        form.add(rateBtn, c);
        c.gridy = 4;
        form.add(rateLabel, c);

        rateBtn.addActionListener(e -> {
            try {
                int id = Integer.parseInt(idField.getText().trim());
                int starVal = 5 - ratingCombo.getSelectedIndex();
                boolean ok = requestManager.submitRating(id, starVal);
                if (ok) {
                    rateLabel.setForeground(COLOR_SUCCESS);
                    rateLabel.setText("✔ Thank you! Rating submitted.");
                } else {
                    rateLabel.setForeground(COLOR_DANGER);
                    rateLabel.setText("✘ Could not submit. Must be COMPLETED.");
                }
            } catch (NumberFormatException ex) {
                rateLabel.setForeground(COLOR_DANGER);
                rateLabel.setText("✘ Valid ID required.");
            }
        });

        panel.add(form);
        return panel;
    }

    private void refreshStudentPanel() {
        String room = getSimulatedRoom();
        if (studentWelcomeLabel != null) {
            studentWelcomeLabel.setText("Requests for Room: " + room);
        }
        if (studentTableModel != null) {
            studentTableModel.setRowCount(0);
            List<Request> myRequests = requestManager.getRequestsByRoomNumber(room);
            for (Request r : myRequests) {
                studentTableModel.addRow(new Object[] {
                        "REQ-" + r.getRequestId(),
                        r.getRequestType(),
                        r.getRoomNumber(),
                        r.getDescription(),
                        r.getPriority(),
                        r.getStatus(),
                        r.getAssignedStaffName(),
                        r.getSubmittedAt()
                });
            }
        }
    }

    // ══════════════════════════════════════════════════════════════════════════
    // SCREEN - WARDEN PANEL
    // ══════════════════════════════════════════════════════════════════════════

    private DefaultTableModel wardenTableModel;

    private JPanel buildWardenPanel() {
        JPanel screen = new JPanel(new BorderLayout(0, 0));
        screen.setBackground(COLOR_BG);

        JTabbedPane tabs = new JTabbedPane();
        tabs.setFont(FONT_BODY);
        tabs.addTab("📋 All Requests", buildWardenRequestsTab());
        tabs.addTab("👥 Add Staff", buildAddStaffTab());
        tabs.addTab("📊 Analytics", buildAnalyticsTab());

        screen.add(tabs, BorderLayout.CENTER);
        return screen;
    }

    // ── Tab: All requests (warden view) ──
    private JComboBox<String> assignStaffCombo;
    
    private JPanel buildWardenRequestsTab() {
        JPanel panel = new JPanel(new BorderLayout(0, 10));
        panel.setBackground(COLOR_BG);
        panel.setBorder(new EmptyBorder(16, 16, 16, 16));

        String[] cols = { "ID", "Room", "Type", "Priority", "Status", "Staff", "Submitted" };
        wardenTableModel = new DefaultTableModel(cols, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };

        JTable table = new JTable(wardenTableModel);
        table.setFont(FONT_BODY);
        table.setRowHeight(28);
        table.getTableHeader().setFont(FONT_BODY);
        table.getTableHeader().setBackground(COLOR_SUCCESS);
        table.getTableHeader().setForeground(COLOR_WHITE);
        table.setSelectionBackground(new Color(220, 252, 231));
        table.setGridColor(COLOR_BORDER);

        JScrollPane scroll = new JScrollPane(table);

        JPanel actionPanel = new JPanel();
        actionPanel.setLayout(new BoxLayout(actionPanel, BoxLayout.Y_AXIS));
        actionPanel.setBackground(COLOR_BG);
        actionPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(
                javax.swing.BorderFactory.createLineBorder(COLOR_BORDER), 
                "Assign Request", 
                javax.swing.border.TitledBorder.LEFT, 
                javax.swing.border.TitledBorder.TOP, 
                FONT_HEADER, 
                COLOR_TEXT));

        JPanel row1 = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 10));
        row1.setBackground(COLOR_BG);
        
        JLabel idLabel = makeLabel("Request ID:", FONT_BODY, COLOR_TEXT);
        JTextField idField = makeField(8);

        JLabel staffLabel = makeLabel("Staff:", FONT_BODY, COLOR_TEXT);
        assignStaffCombo = new JComboBox<>();
        JLabel availableStaffLabel = makeLabel("", FONT_SMALL, COLOR_TEXT);

        row1.add(idLabel);
        row1.add(idField);
        row1.add(staffLabel);
        row1.add(assignStaffCombo);
        row1.add(availableStaffLabel);

        JPanel row2 = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 10));
        row2.setBackground(COLOR_BG);

        JButton assignBtn = makeButton("Assign", COLOR_PRIMARY, COLOR_WHITE);
        JLabel actionStatus = makeLabel("", FONT_SMALL, COLOR_SUCCESS);

        assignBtn.addActionListener(e -> {
            try {
                int id = Integer.parseInt(idField.getText().trim());
                String staff = (String) assignStaffCombo.getSelectedItem();
                if (staff == null || staff.isEmpty() || staff.equals("Select a request first") || staff.equals("No workers available")) {
                    actionStatus.setForeground(COLOR_DANGER);
                    actionStatus.setText("✘ Please select a valid staff member.");
                    return;
                }
                boolean ok = requestManager.assignRequest(id, staff);
                if (ok) {
                    actionStatus.setForeground(COLOR_SUCCESS);
                    actionStatus.setText("✔ Request REQ-" + id + " assigned to " + staff);
                    refreshWardenTable();
                } else {
                    actionStatus.setForeground(COLOR_DANGER);
                    actionStatus.setText("✘ Could not assign. Check ID/Status.");
                }
            } catch (NumberFormatException ex) {
                actionStatus.setForeground(COLOR_DANGER);
                actionStatus.setText("✘ Valid ID required.");
            }
        });

        row2.add(assignBtn);
        row2.add(actionStatus);

        actionPanel.add(row1);
        actionPanel.add(row2);

        table.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && table.getSelectedRow() != -1) {
                int row = table.getSelectedRow();
                String reqIdStr = wardenTableModel.getValueAt(row, 0).toString();
                String reqType = wardenTableModel.getValueAt(row, 2).toString();
                
                idField.setText(reqIdStr.replace("REQ-", ""));
                
                assignStaffCombo.removeAllItems();
                java.util.List<com.hostel.models.MaintenanceStaff> staffList = requestManager.getAllStaff();
                int count = 0;
                for (com.hostel.models.MaintenanceStaff s : staffList) {
                    if (s.getSpecialization().equalsIgnoreCase(reqType)) {
                        assignStaffCombo.addItem(s.getName());
                        count++;
                    }
                }
                
                if (count > 0) {
                    availableStaffLabel.setText("(" + count + " available " + reqType + " workers)");
                    availableStaffLabel.setForeground(COLOR_SUCCESS);
                } else {
                    assignStaffCombo.addItem("No workers available");
                    availableStaffLabel.setText("(0 available)");
                    availableStaffLabel.setForeground(COLOR_DANGER);
                }
            }
        });

        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.setBackground(COLOR_BG);
        bottomPanel.setBorder(new javax.swing.border.EmptyBorder(10, 0, 0, 0));
        bottomPanel.add(actionPanel, BorderLayout.CENTER);

        panel.add(makeLabel("All Maintenance Requests:", FONT_HEADER, COLOR_TEXT), BorderLayout.NORTH);
        panel.add(scroll, BorderLayout.CENTER);
        panel.add(bottomPanel, BorderLayout.SOUTH);

        return panel;
    }

    // ── Tab: Add Staff (warden view) ──
    private JPanel buildAddStaffTab() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(COLOR_BG);

        JPanel form = new JPanel(new GridBagLayout());
        form.setBackground(COLOR_CARD);
        form.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(COLOR_BORDER, 1, true),
                new EmptyBorder(30, 40, 30, 40)));

        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(10, 8, 10, 8);
        c.anchor = GridBagConstraints.WEST;
        c.fill = GridBagConstraints.HORIZONTAL;

        c.gridx = 0; c.gridy = 0; c.gridwidth = 2;
        form.add(makeLabel("Add Maintenance Worker", FONT_HEADER, COLOR_TEXT), c);
        c.gridwidth = 1;

        c.gridx = 0; c.gridy = 1;
        form.add(makeLabel("Staff Name:", FONT_BODY, COLOR_TEXT), c);
        JTextField nameField = makeField(18);
        c.gridx = 1;
        form.add(nameField, c);

        c.gridx = 0; c.gridy = 2;
        form.add(makeLabel("Specialization:", FONT_BODY, COLOR_TEXT), c);
        JComboBox<String> specCombo = new JComboBox<>(new String[]{"Electrical", "Plumbing", "Carpentry", "General"});
        c.gridx = 1;
        form.add(specCombo, c);

        JButton addBtn = makeButton("Add Worker", COLOR_SUCCESS, COLOR_WHITE);
        JLabel statusLabel = makeLabel("", FONT_SMALL, COLOR_SUCCESS);

        c.gridx = 0; c.gridy = 3; c.gridwidth = 2; c.anchor = GridBagConstraints.CENTER; c.fill = GridBagConstraints.NONE;
        form.add(addBtn, c);
        c.gridy = 4;
        form.add(statusLabel, c);

        addBtn.addActionListener(e -> {
            String name = nameField.getText().trim();
            String spec = (String) specCombo.getSelectedItem();
            if (name.isEmpty()) {
                statusLabel.setForeground(COLOR_DANGER);
                statusLabel.setText("✘ Name cannot be empty.");
                return;
            }
            int newId = RequestIDGenerator.getInstance().generateID();
            MaintenanceStaff newStaff = new MaintenanceStaff(newId, name, name.toLowerCase().replace(" ", "") + "@hostel.com", "password123", spec);
            requestManager.addStaff(newStaff);
            updateStaffDropdowns();
            
            statusLabel.setForeground(COLOR_SUCCESS);
            statusLabel.setText("✔ Added " + name + " (" + spec + ")");
            nameField.setText("");
        });

        panel.add(form);
        return panel;
    }

    // ── Tab: Analytics ──
    private JPanel buildAnalyticsTab() {
        JPanel panel = new JPanel(new BorderLayout(0, 16));
        panel.setBackground(COLOR_BG);
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));

        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(COLOR_CARD);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(COLOR_BORDER, 1, true),
                new EmptyBorder(28, 36, 28, 36)));

        card.add(makeLabel("📊 Analytics Dashboard", FONT_HEADER, COLOR_TEXT));
        card.add(Box.createVerticalStrut(20));

        refreshAnalyticsAction = () -> {
            card.removeAll();
            card.add(makeLabel("📊 Analytics Dashboard", FONT_HEADER, COLOR_TEXT));
            card.add(Box.createVerticalStrut(20));
            List<String> fresh = analyticsEngine.getFullSummary();
            for (String line : fresh) {
                JPanel row = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 6));
                row.setBackground(COLOR_CARD);
                row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));
                row.add(makeLabel(line, FONT_BODY, COLOR_TEXT));
                card.add(row);
                JSeparator sep = new JSeparator();
                sep.setForeground(COLOR_BORDER);
                sep.setMaximumSize(new Dimension(Integer.MAX_VALUE, 1));
                card.add(sep);
            }
            card.add(Box.createVerticalStrut(16));
            JButton rBtn = makeButton("🔄 Refresh", COLOR_PRIMARY, COLOR_WHITE);
            // Must re-attach the listener because we wipe the card!
            rBtn.addActionListener(ev -> refreshAllData()); 
            card.add(rBtn);
            card.revalidate();
            card.repaint();
        };

        JButton refreshBtn = makeButton("🔄 Refresh", COLOR_PRIMARY, COLOR_WHITE);
        refreshBtn.addActionListener(e -> refreshAnalyticsAction.run());
        refreshAnalyticsAction.run();

        panel.add(card, BorderLayout.NORTH);
        return panel;
    }

    private void refreshWardenTable() {
        if (wardenTableModel == null) return;
        wardenTableModel.setRowCount(0);
        List<Request> all = requestManager.getAllRequests();
        for (Request r : all) {
            wardenTableModel.addRow(new Object[] {
                    "REQ-" + r.getRequestId(),
                    r.getRoomNumber(),
                    r.getRequestType(),
                    r.getPriority(),
                    r.getStatus(),
                    r.getAssignedStaffName(),
                    r.getSubmittedAt()
            });
        }
    }

    // ══════════════════════════════════════════════════════════════════════════
    // SCREEN - WORKER PANEL
    // ══════════════════════════════════════════════════════════════════════════

    private DefaultTableModel workerTableModel;
    private JLabel workerWelcomeLabel;

    private JPanel buildWorkerPanel() {
        JPanel screen = new JPanel(new BorderLayout(0, 0));
        screen.setBackground(COLOR_BG);

        JPanel panel = new JPanel(new BorderLayout(0, 10));
        panel.setBackground(COLOR_BG);
        panel.setBorder(new EmptyBorder(16, 16, 16, 16));

        String[] cols = { "ID", "Room", "Type", "Description", "Priority", "Status", "Submitted" };
        workerTableModel = new DefaultTableModel(cols, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };

        JTable table = new JTable(workerTableModel);
        table.setFont(FONT_BODY);
        table.setRowHeight(28);
        table.getTableHeader().setFont(FONT_BODY);
        table.getTableHeader().setBackground(COLOR_WARNING);
        table.getTableHeader().setForeground(COLOR_TEXT);
        table.setGridColor(COLOR_BORDER);

        JScrollPane scroll = new JScrollPane(table);

        JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 8));
        actionPanel.setBackground(COLOR_BG);

        JLabel idLabel = makeLabel("Request ID:", FONT_BODY, COLOR_TEXT);
        JTextField idField = makeField(8);

        JButton completeBtn = makeButton("Mark Done", COLOR_SUCCESS, COLOR_WHITE);
        JLabel actionStatus = makeLabel("", FONT_SMALL, COLOR_SUCCESS);

        completeBtn.addActionListener(e -> {
            try {
                int id = Integer.parseInt(idField.getText().trim());
                Request r = requestManager.getRequestById(id);
                String currentWorker = getSimulatedWorker();
                if (r == null || currentWorker == null || !currentWorker.equalsIgnoreCase(r.getAssignedStaffName())) {
                    actionStatus.setForeground(COLOR_DANGER);
                    actionStatus.setText("✘ Not assigned to " + currentWorker);
                    return;
                }
                boolean ok = requestManager.completeRequest(id);
                if (ok) {
                    actionStatus.setForeground(COLOR_SUCCESS);
                    actionStatus.setText("✔ Marked Done!");
                    refreshWorkerPanel();
                } else {
                    actionStatus.setForeground(COLOR_DANGER);
                    actionStatus.setText("✘ Could not complete.");
                }
            } catch (NumberFormatException ex) {
                actionStatus.setForeground(COLOR_DANGER);
                actionStatus.setText("✘ Enter a valid numeric ID.");
            }
        });

        actionPanel.add(idLabel);
        actionPanel.add(idField);
        actionPanel.add(completeBtn);

        JPanel statusRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 4));
        statusRow.setBackground(COLOR_BG);
        statusRow.add(actionStatus);

        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.setBackground(COLOR_BG);
        bottomPanel.add(actionPanel, BorderLayout.NORTH);
        bottomPanel.add(statusRow, BorderLayout.SOUTH);

        workerWelcomeLabel = makeLabel("My Assigned Tasks", FONT_HEADER, COLOR_TEXT);
        panel.add(workerWelcomeLabel, BorderLayout.NORTH);
        panel.add(scroll, BorderLayout.CENTER);
        panel.add(bottomPanel, BorderLayout.SOUTH);

        screen.add(panel, BorderLayout.CENTER);
        return screen;
    }

    private void refreshWorkerPanel() {
        String worker = getSimulatedWorker();
        if (workerWelcomeLabel != null) {
            workerWelcomeLabel.setText("Tasks Assigned To: " + (worker == null ? "None" : worker));
        }
        if (workerTableModel != null) {
            workerTableModel.setRowCount(0);
            if (worker != null) {
                List<Request> all = requestManager.getAllRequests();
                for (Request r : all) {
                    if (worker.equalsIgnoreCase(r.getAssignedStaffName())) {
                        workerTableModel.addRow(new Object[] {
                                "REQ-" + r.getRequestId(),
                                r.getRoomNumber(),
                                r.getRequestType(),
                                r.getDescription(),
                                r.getPriority(),
                                r.getStatus(),
                                r.getSubmittedAt()
                        });
                    }
                }
            }
        }
    }

    // ── Shared Dropdown Updater ──
    private void updateStaffDropdowns() {
        if (simWorkerCombo != null) {
            String currentSelected = (String) simWorkerCombo.getSelectedItem();
            simWorkerCombo.removeAllItems();
            java.util.List<com.hostel.models.MaintenanceStaff> staffList = requestManager.getAllStaff();
            for (com.hostel.models.MaintenanceStaff s : staffList) {
                simWorkerCombo.addItem(s.getName());
            }
            if (currentSelected != null) {
                simWorkerCombo.setSelectedItem(currentSelected);
            }
        }
        
        if (assignStaffCombo != null && assignStaffCombo.getItemCount() == 0) {
            assignStaffCombo.addItem("Select a request first");
        }
    }

    // ══════════════════════════════════════════════════════════════════════════
    // MAIN - Entry point
    // ══════════════════════════════════════════════════════════════════════════
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new MainApp());
    }
}