package com.placement;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;
import java.time.LocalDate;

public class ApplicationManagement {

    private static JFrame frame;
    private static DefaultTableModel model;
    private static JLabel footer;

    // Represents a student in the dropdown
    private static class StudentOption {
        int id;
        String name;

        StudentOption(int id, String name) {
            this.id = id;
            this.name = name;
        }

        @Override
        public String toString() {
            return name;
        }
    }

    // Represents a company in the dropdown
    private static class CompanyOption {
        int id;
        String name;
        String role;

        CompanyOption(int id, String name, String role) {
            this.id = id;
            this.name = name;
            this.role = role;
        }

        @Override
        public String toString() {
            return name + " - " + role;
        }
    }

    public static void openWindow() {

        frame = new JFrame("Placement Applications");
        frame.setSize(1100, 550);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setLocationRelativeTo(null);
        frame.setLayout(new BorderLayout());

        Color navyBlue = new Color(25, 55, 109);
        Color lightBlue = new Color(235, 243, 255);

        // Header
        JLabel title = new JLabel(
                "PLACEMENT APPLICATIONS",
                SwingConstants.CENTER);

        title.setFont(new Font("Arial", Font.BOLD, 24));
        title.setForeground(navyBlue);
        title.setBorder(
                BorderFactory.createEmptyBorder(20, 10, 15, 10));

        // Add Application button
        JButton addButton = new JButton("+ Add Application");
        JButton editButton = new JButton("Edit Selected");
        JButton deleteButton = new JButton("Delete Selected");

        addButton.setFont(new Font("Arial", Font.BOLD, 13));
        addButton.setBackground(navyBlue);
        addButton.setForeground(Color.WHITE);
        addButton.setFocusPainted(false);
        addButton.setCursor(
                new Cursor(Cursor.HAND_CURSOR));

        JPanel buttonPanel = new JPanel(
                new FlowLayout(FlowLayout.RIGHT));

        buttonPanel.setBackground(lightBlue);
        buttonPanel.setBorder(
                BorderFactory.createEmptyBorder(0, 15, 10, 20));

        buttonPanel.add(addButton);
        buttonPanel.add(editButton);
        buttonPanel.add(deleteButton);

        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBackground(lightBlue);
        topPanel.add(title, BorderLayout.NORTH);
        topPanel.add(buttonPanel, BorderLayout.CENTER);

        frame.add(topPanel, BorderLayout.NORTH);

        // Applications table
        String[] columns = {
            "Application ID",
            "Student Name",
            "Company",
            "Role",
            "Applied On",
            "Status",
            "Notes"
        };

        model = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        JTable table = new JTable(model);
        table.setRowHeight(30);
        table.setFont(new Font("Arial", Font.PLAIN, 13));

        table.getTableHeader().setFont(
                new Font("Arial", Font.BOLD, 13));
        table.getTableHeader().setBackground(navyBlue);
        table.getTableHeader().setForeground(Color.WHITE);

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(
                BorderFactory.createEmptyBorder(5, 15, 15, 15));

        frame.add(scrollPane, BorderLayout.CENTER);

        // Footer
        footer = new JLabel("", SwingConstants.CENTER);
        footer.setBorder(
                BorderFactory.createEmptyBorder(10, 10, 15, 10));

        frame.add(footer, BorderLayout.SOUTH);

        addButton.addActionListener(
                e -> showAddApplicationForm());
        editButton.addActionListener(
                e -> editSelectedApplication(table));
        deleteButton.addActionListener(
                e -> deleteSelectedApplication(table));

        refreshApplications();

        frame.setVisible(true);
    }

    // Load applications and their related names
    private static void refreshApplications() {

        model.setRowCount(0);

        String sql = """
                SELECT a.application_id,
                       s.full_name,
                       c.company_name,
                       c.role_offered,
                       a.applied_on,
                       a.status,
                       a.notes
                FROM applications a
                JOIN students s ON a.student_id = s.student_id
                JOIN companies c ON a.company_id = c.company_id
                ORDER BY a.application_id
                """;

        try (
            Connection connection = DBConnection.getConnection();
            Statement statement = connection.createStatement();
            ResultSet result = statement.executeQuery(sql)
        ) {
            while (result.next()) {

                Object[] row = {
                    result.getInt("application_id"),
                    result.getString("full_name"),
                    result.getString("company_name"),
                    result.getString("role_offered"),
                    result.getDate("applied_on"),
                    result.getString("status"),
                    result.getString("notes")
                };

                model.addRow(row);
            }

            footer.setText(
                    "Total Applications: " + model.getRowCount());

        } catch (SQLException e) {

            JOptionPane.showMessageDialog(
                    frame,
                    "Could not load applications.\n" + e.getMessage(),
                    "Database Error",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    // Load dropdown options from MySQL
    private static void loadStudents(
            JComboBox<StudentOption> studentBox)
            throws SQLException {

        String sql =
                "SELECT student_id, full_name "
                + "FROM students ORDER BY full_name";

        try (
            Connection connection = DBConnection.getConnection();
            PreparedStatement statement =
                    connection.prepareStatement(sql);
            ResultSet result = statement.executeQuery()
        ) {
            while (result.next()) {
                studentBox.addItem(
                        new StudentOption(
                                result.getInt("student_id"),
                                result.getString("full_name")
                        )
                );
            }
        }
    }

    private static void loadCompanies(
            JComboBox<CompanyOption> companyBox)
            throws SQLException {

        String sql =
                "SELECT company_id, company_name, role_offered "
                + "FROM companies ORDER BY company_name";

        try (
            Connection connection = DBConnection.getConnection();
            PreparedStatement statement =
                    connection.prepareStatement(sql);
            ResultSet result = statement.executeQuery()
        ) {
            while (result.next()) {
                companyBox.addItem(
                        new CompanyOption(
                                result.getInt("company_id"),
                                result.getString("company_name"),
                                result.getString("role_offered")
                        )
                );
            }
        }
    }

    // Add Application form
    private static void showAddApplicationForm() {

        JDialog dialog = new JDialog(
                frame, "Add Placement Application", true);

        dialog.setSize(500, 390);
        dialog.setLocationRelativeTo(frame);
        dialog.setLayout(new BorderLayout());

        Color navyBlue = new Color(25, 55, 109);

        JLabel heading = new JLabel(
                "Enter Application Details",
                SwingConstants.CENTER);

        heading.setFont(new Font("Arial", Font.BOLD, 20));
        heading.setForeground(navyBlue);
        heading.setBorder(
                BorderFactory.createEmptyBorder(20, 10, 15, 10));

        dialog.add(heading, BorderLayout.NORTH);

        // Dropdowns
        JComboBox<StudentOption> studentBox =
                new JComboBox<>();

        JComboBox<CompanyOption> companyBox =
                new JComboBox<>();

        JComboBox<String> statusBox = new JComboBox<>(
                new String[]{
                    "Applied",
                    "Shortlisted",
                    "Interview",
                    "Selected",
                    "Rejected"
                });

        JTextField dateField =
                new JTextField(LocalDate.now().toString());

        JTextField notesField = new JTextField();

        // Load student and company options
        try {
            loadStudents(studentBox);
            loadCompanies(companyBox);

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(
                    dialog,
                    "Could not load students or companies.\n"
                            + e.getMessage(),
                    "Database Error",
                    JOptionPane.ERROR_MESSAGE
            );
            dialog.dispose();
            return;
        }

        JPanel form = new JPanel(
                new GridLayout(5, 2, 10, 12));

        form.setBorder(
                BorderFactory.createEmptyBorder(15, 25, 15, 25));

        form.add(new JLabel("Select Student:"));
        form.add(studentBox);

        form.add(new JLabel("Select Company:"));
        form.add(companyBox);

        form.add(new JLabel("Applied On (YYYY-MM-DD):"));
        form.add(dateField);

        form.add(new JLabel("Application Status:"));
        form.add(statusBox);

        form.add(new JLabel("Notes (optional):"));
        form.add(notesField);

        dialog.add(form, BorderLayout.CENTER);

        // Buttons
        JButton saveButton = new JButton("Save Application");
        saveButton.setBackground(navyBlue);
        saveButton.setForeground(Color.WHITE);
        saveButton.setFocusPainted(false);

        JButton cancelButton = new JButton("Cancel");

        JPanel buttons = new JPanel(
                new FlowLayout(FlowLayout.CENTER));

        buttons.setBorder(
                BorderFactory.createEmptyBorder(5, 10, 15, 10));

        buttons.add(saveButton);
        buttons.add(cancelButton);

        dialog.add(buttons, BorderLayout.SOUTH);

        cancelButton.addActionListener(
                e -> dialog.dispose());

        saveButton.addActionListener(e -> {

            StudentOption student =
                    (StudentOption) studentBox.getSelectedItem();

            CompanyOption company =
                    (CompanyOption) companyBox.getSelectedItem();

            if (student == null || company == null) {
                JOptionPane.showMessageDialog(
                        dialog,
                        "Add at least one student and one company first.",
                        "Missing Records",
                        JOptionPane.WARNING_MESSAGE
                );
                return;
            }

            java.sql.Date appliedOn;

            try {
                appliedOn = java.sql.Date.valueOf(
                        LocalDate.parse(
                                dateField.getText().trim()));

            } catch (Exception ex) {
                JOptionPane.showMessageDialog(
                        dialog,
                        "Enter a valid date in YYYY-MM-DD format.",
                        "Invalid Date",
                        JOptionPane.WARNING_MESSAGE
                );
                return;
            }

            String status =
                    (String) statusBox.getSelectedItem();

            String notes = notesField.getText().trim();

            // Prevent duplicate applications for the same pair
            String checkSql =
                    "SELECT application_id FROM applications "
                    + "WHERE student_id = ? AND company_id = ?";

            try (Connection connection =
                    DBConnection.getConnection()) {

                try (PreparedStatement check =
                        connection.prepareStatement(checkSql)) {

                    check.setInt(1, student.id);
                    check.setInt(2, company.id);

                    try (ResultSet result =
                            check.executeQuery()) {

                        if (result.next()) {
                            JOptionPane.showMessageDialog(
                                    dialog,
                                    "This student already has an application "
                                            + "for this company.",
                                    "Duplicate Application",
                                    JOptionPane.WARNING_MESSAGE
                            );
                            return;
                        }
                    }
                }

                // Insert application
                String insertSql =
                        "INSERT INTO applications "
                        + "(student_id, company_id, applied_on, "
                        + "status, notes) VALUES (?, ?, ?, ?, ?)";

                try (PreparedStatement insert =
                        connection.prepareStatement(insertSql)) {

                    insert.setInt(1, student.id);
                    insert.setInt(2, company.id);
                    insert.setDate(3, appliedOn);
                    insert.setString(4, status);

                    if (notes.isEmpty()) {
                        insert.setNull(5, Types.VARCHAR);
                    } else {
                        insert.setString(5, notes);
                    }

                    insert.executeUpdate();
                }

                JOptionPane.showMessageDialog(
                        dialog,
                        "Application added successfully!",
                        "Success",
                        JOptionPane.INFORMATION_MESSAGE
                );

                dialog.dispose();
                refreshApplications();

            } catch (SQLIntegrityConstraintViolationException ex) {
                JOptionPane.showMessageDialog(
                        dialog,
                        "This application conflicts with an existing record.",
                        "Duplicate Application",
                        JOptionPane.WARNING_MESSAGE
                );

            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(
                        dialog,
                        "Could not save application.\n"
                                + ex.getMessage(),
                        "Database Error",
                        JOptionPane.ERROR_MESSAGE
                );
            }
        });

        dialog.setVisible(true);
    }

    private static void editSelectedApplication(JTable table) {

        int selectedRow = table.getSelectedRow();

        if (selectedRow == -1) {
                JOptionPane.showMessageDialog(
                        frame,
                        "Please select an application to edit.");
                return;
        }

        int modelRow = table.convertRowIndexToModel(selectedRow);

        int applicationId =
                (int) model.getValueAt(modelRow, 0);

        String currentStatus =
                model.getValueAt(modelRow, 5).toString();

        Object notesValue = model.getValueAt(modelRow, 6);

        String currentNotes =
                notesValue == null ? "" : notesValue.toString();

        String[] statuses = {
                "Applied",
                "Shortlisted",
                "Interview",
                "Selected",
                "Rejected"
        };

        JComboBox<String> statusBox =
                new JComboBox<>(statuses);

        statusBox.setSelectedItem(currentStatus);

        JTextField notesField = new JTextField(currentNotes);

        JPanel panel = new JPanel(new GridLayout(2, 2, 10, 10));

        panel.add(new JLabel("Application Status:"));
        panel.add(statusBox);

        panel.add(new JLabel("Notes:"));
        panel.add(notesField);

        int choice = JOptionPane.showConfirmDialog(
                frame,
                panel,
                "Edit Application",
                JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.PLAIN_MESSAGE);

        if (choice != JOptionPane.OK_OPTION) {
                return;
        }

        String newStatus =
                (String) statusBox.getSelectedItem();

        String newNotes = notesField.getText().trim();

        if (newNotes.length() > 255) {
                JOptionPane.showMessageDialog(
                        frame,
                        "Notes cannot exceed 255 characters.");
                return;
        }

        String sql =
                "UPDATE applications SET status = ?, notes = ? "
                + "WHERE application_id = ?";

        try (Connection connection = DBConnection.getConnection();
                PreparedStatement statement =
                        connection.prepareStatement(sql)) {

                statement.setString(1, newStatus);

                if (newNotes.isEmpty()) {
                statement.setNull(2, Types.VARCHAR);
                } else {
                statement.setString(2, newNotes);
                }

                statement.setInt(3, applicationId);

                statement.executeUpdate();

                refreshApplications();

                JOptionPane.showMessageDialog(
                        frame,
                        "Application updated successfully!");

        } catch (SQLException e) {
                JOptionPane.showMessageDialog(
                        frame,
                        "Could not update application.\n"
                                + e.getMessage(),
                        "Database Error",
                        JOptionPane.ERROR_MESSAGE);
                 }
        }

        private static void deleteSelectedApplication(JTable table) {

    int selectedRow = table.getSelectedRow();

    if (selectedRow == -1) {
        JOptionPane.showMessageDialog(
                frame,
                "Please select an application to delete.");
        return;
    }

    int modelRow = table.convertRowIndexToModel(selectedRow);

    int applicationId =
            (int) model.getValueAt(modelRow, 0);

    String studentName =
            model.getValueAt(modelRow, 1).toString();

    String companyName =
            model.getValueAt(modelRow, 2).toString();

    int choice = JOptionPane.showConfirmDialog(
            frame,
            "Delete the application for "
                    + studentName + " at " + companyName + "?",
            "Confirm Deletion",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.WARNING_MESSAGE);

    if (choice != JOptionPane.YES_OPTION) {
        return;
    }

    String sql =
            "DELETE FROM applications WHERE application_id = ?";

    try (Connection connection = DBConnection.getConnection();
         PreparedStatement statement =
                 connection.prepareStatement(sql)) {

        statement.setInt(1, applicationId);

        int deleted = statement.executeUpdate();

        if (deleted > 0) {
            refreshApplications();

            JOptionPane.showMessageDialog(
                    frame,
                    "Application deleted successfully!");
        } else {
            JOptionPane.showMessageDialog(
                    frame,
                    "Application was not found.");
        }

    } catch (SQLException e) {
        JOptionPane.showMessageDialog(
                frame,
                "Could not delete application.\n"
                        + e.getMessage(),
                "Database Error",
                JOptionPane.ERROR_MESSAGE);
    }
}

}
