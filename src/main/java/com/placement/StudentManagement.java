
package com.placement;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.math.BigDecimal;
import java.sql.*;

public class StudentManagement {

    private static DefaultTableModel model;
    private static JFrame frame;

    public static void openWindow() {

        frame = new JFrame("Manage Students");
        frame.setSize(1150, 550);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setLocationRelativeTo(null);
        frame.setLayout(new BorderLayout());

        Color navyBlue = new Color(25, 55, 109);
        Color lightBlue = new Color(235, 243, 255);

        // Header
        JLabel title = new JLabel(
                "STUDENT RECORDS", SwingConstants.CENTER);

        title.setFont(new Font("Arial", Font.BOLD, 24));
        title.setForeground(navyBlue);
        title.setBorder(
                BorderFactory.createEmptyBorder(20, 10, 15, 10));

        // Action buttons
        JButton addButton = new JButton("+ Add Student");
        JButton editButton = new JButton("Edit Selected");
        JButton deleteButton = new JButton("Delete Selected");

        addButton.setFont(new Font("Arial", Font.BOLD, 13));
        editButton.setFont(new Font("Arial", Font.BOLD, 13));
        deleteButton.setFont(new Font("Arial", Font.BOLD, 13));

        addButton.setBackground(navyBlue);
        editButton.setBackground(navyBlue);
        deleteButton.setBackground(new Color(180, 50, 50));

        for (JButton button : new JButton[]{
                addButton, editButton, deleteButton}) {
            button.setForeground(Color.WHITE);
            button.setFocusPainted(false);
            button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        }

        // Top panel
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBackground(lightBlue);
        topPanel.add(title, BorderLayout.NORTH);

        // Button panel
        JPanel buttonPanel = new JPanel(
                new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.setBackground(lightBlue);
        buttonPanel.setBorder(
                BorderFactory.createEmptyBorder(0, 15, 10, 20));

        buttonPanel.add(addButton);
        buttonPanel.add(editButton);
        buttonPanel.add(deleteButton);

        topPanel.add(buttonPanel, BorderLayout.CENTER);

        topPanel.add(buttonPanel, BorderLayout.CENTER);
        frame.add(topPanel, BorderLayout.NORTH);

        // Student table
        String[] columns = {
            "Student ID",
            "Register Number",
            "Full Name",
            "Department",
            "Email",
            "Phone",
            "CGPA",
            "Graduation Year"
        };

        model = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        JTable table = new JTable(model);
        table.setRowHeight(28);
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
        JLabel footer = new JLabel("", SwingConstants.CENTER);
        footer.setBorder(
                BorderFactory.createEmptyBorder(10, 10, 15, 10));
        frame.add(footer, BorderLayout.SOUTH);

        // Button action
        addButton.addActionListener(e -> showAddStudentForm());
        editButton.addActionListener(e -> editSelectedStudent(table, footer));
        deleteButton.addActionListener(e -> deleteSelectedStudent(table, footer));

        // Load existing records
        refreshStudents(footer);

        frame.setVisible(true);
    }

    private static void refreshStudents(JLabel footer) {

        model.setRowCount(0);

        String sql = "SELECT * FROM students ORDER BY student_id";

        try (
            Connection connection = DBConnection.getConnection();
            Statement statement = connection.createStatement();
            ResultSet result = statement.executeQuery(sql)
        ) {
            while (result.next()) {
                Object[] row = {
                    result.getInt("student_id"),
                    result.getString("register_no"),
                    result.getString("full_name"),
                    result.getString("department"),
                    result.getString("email"),
                    result.getString("phone"),
                    result.getBigDecimal("cgpa"),
                    result.getInt("graduation_year")
                };

                model.addRow(row);
            }

            footer.setText("Total Students: " + model.getRowCount());

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(
                    frame,
                    "Could not load student records.\n" + e.getMessage(),
                    "Database Error",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    private static void showAddStudentForm() {

        JDialog dialog = new JDialog(
                frame, "Add New Student", true);

        dialog.setSize(480, 510);
        dialog.setLocationRelativeTo(frame);
        dialog.setLayout(new BorderLayout());

        Color navyBlue = new Color(25, 55, 109);

        JLabel heading = new JLabel(
                "Enter Student Details", SwingConstants.CENTER);

        heading.setFont(new Font("Arial", Font.BOLD, 20));
        heading.setForeground(navyBlue);
        heading.setBorder(
                BorderFactory.createEmptyBorder(20, 10, 15, 10));

        dialog.add(heading, BorderLayout.NORTH);

        // Form fields
        JPanel form = new JPanel(new GridLayout(7, 2, 10, 12));
        form.setBorder(
                BorderFactory.createEmptyBorder(15, 25, 15, 25));

        JTextField registerField = new JTextField();
        JTextField nameField = new JTextField();
        JTextField departmentField = new JTextField();
        JTextField emailField = new JTextField();
        JTextField phoneField = new JTextField();
        JTextField cgpaField = new JTextField();
        JTextField yearField = new JTextField();

        form.add(new JLabel("Register Number:"));
        form.add(registerField);

        form.add(new JLabel("Full Name:"));
        form.add(nameField);

        form.add(new JLabel("Department:"));
        form.add(departmentField);

        form.add(new JLabel("Email:"));
        form.add(emailField);

        form.add(new JLabel("Phone (optional):"));
        form.add(phoneField);

        form.add(new JLabel("CGPA (0-10):"));
        form.add(cgpaField);

        form.add(new JLabel("Graduation Year:"));
        form.add(yearField);

        dialog.add(form, BorderLayout.CENTER);

        // Form buttons
        JButton saveButton = new JButton("Save Student");
        saveButton.setBackground(navyBlue);
        saveButton.setForeground(Color.WHITE);
        saveButton.setFocusPainted(false);

        JButton cancelButton = new JButton("Cancel");

        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.CENTER));
        buttons.setBorder(
                BorderFactory.createEmptyBorder(5, 10, 15, 10));
        buttons.add(saveButton);
        buttons.add(cancelButton);

        dialog.add(buttons, BorderLayout.SOUTH);

        cancelButton.addActionListener(e -> dialog.dispose());

        saveButton.addActionListener(e -> {

            String registerNo = registerField.getText().trim();
            String fullName = nameField.getText().trim();
            String department = departmentField.getText().trim();
            String email = emailField.getText().trim();
            String phone = phoneField.getText().trim();
            String cgpaText = cgpaField.getText().trim();
            String yearText = yearField.getText().trim();

            // Check required fields
            if (registerNo.isEmpty()
                    || fullName.isEmpty()
                    || department.isEmpty()
                    || email.isEmpty()
                    || cgpaText.isEmpty()
                    || yearText.isEmpty()) {

                JOptionPane.showMessageDialog(
                        dialog,
                        "Please fill in all required fields.",
                        "Missing Information",
                        JOptionPane.WARNING_MESSAGE
                );
                return;
            }

            if (!email.matches("^[^\\s@]+@[^\\s@]+\\.[^\\s@]+$")) {
                JOptionPane.showMessageDialog(
                        dialog,
                        "Please enter a valid email address.",
                        "Invalid Email",
                        JOptionPane.WARNING_MESSAGE
                );
                return;
            }

            BigDecimal cgpa;
            int graduationYear;

            try {
                cgpa = new BigDecimal(cgpaText);

                if (cgpa.compareTo(BigDecimal.ZERO) < 0
                        || cgpa.compareTo(BigDecimal.TEN) > 0
                        || cgpa.scale() > 2) {

                    JOptionPane.showMessageDialog(
                            dialog,
                            "CGPA must be between 0 and 10, with at most 2 decimal places.",
                            "Invalid CGPA",
                            JOptionPane.WARNING_MESSAGE
                    );
                    return;
                }

                graduationYear = Integer.parseInt(yearText);

                if (graduationYear < 2000 || graduationYear > 2100) {
                    JOptionPane.showMessageDialog(
                            dialog,
                            "Please enter a valid graduation year.",
                            "Invalid Year",
                            JOptionPane.WARNING_MESSAGE
                    );
                    return;
                }

            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(
                        dialog,
                        "CGPA must be a number and graduation year must be a whole number.",
                        "Invalid Input",
                        JOptionPane.WARNING_MESSAGE
                );
                return;
            }

            // Check for duplicate register number or email
            String checkSql =
                    "SELECT register_no, email FROM students "
                    + "WHERE register_no = ? OR email = ?";

            try (Connection connection = DBConnection.getConnection()) {

                try (PreparedStatement checkStatement =
                        connection.prepareStatement(checkSql)) {

                    checkStatement.setString(1, registerNo);
                    checkStatement.setString(2, email);

                    try (ResultSet result = checkStatement.executeQuery()) {

                        while (result.next()) {

                            if (result.getString("register_no")
                                    .equalsIgnoreCase(registerNo)) {

                                JOptionPane.showMessageDialog(
                                        dialog,
                                        "This register number already exists!",
                                        "Duplicate Register Number",
                                        JOptionPane.WARNING_MESSAGE
                                );
                                return;
                            }

                            if (result.getString("email")
                                    .equalsIgnoreCase(email)) {

                                JOptionPane.showMessageDialog(
                                        dialog,
                                        "This email address is already registered!",
                                        "Duplicate Email",
                                        JOptionPane.WARNING_MESSAGE
                                );
                                return;
                            }
                        }
                    }
                }

                // Insert new student
                String insertSql =
                        "INSERT INTO students "
                        + "(register_no, full_name, department, email, "
                        + "phone, cgpa, graduation_year) "
                        + "VALUES (?, ?, ?, ?, ?, ?, ?)";

                try (PreparedStatement insertStatement =
                        connection.prepareStatement(insertSql)) {

                    insertStatement.setString(1, registerNo);
                    insertStatement.setString(2, fullName);
                    insertStatement.setString(3, department);
                    insertStatement.setString(4, email);

                    if (phone.isEmpty()) {
                        insertStatement.setNull(5, Types.VARCHAR);
                    } else {
                        insertStatement.setString(5, phone);
                    }

                    insertStatement.setBigDecimal(6, cgpa);
                    insertStatement.setInt(7, graduationYear);

                    insertStatement.executeUpdate();
                }

                JOptionPane.showMessageDialog(
                        dialog,
                        "Student added successfully!",
                        "Success",
                        JOptionPane.INFORMATION_MESSAGE
                );

                dialog.dispose();

                // Refresh the table after insertion
                Component footerComponent =
                        ((BorderLayout) frame.getLayout())
                                .getLayoutComponent(BorderLayout.SOUTH);

                if (footerComponent instanceof JLabel) {
                    refreshStudents((JLabel) footerComponent);
                }

            } catch (SQLIntegrityConstraintViolationException ex) {
                JOptionPane.showMessageDialog(
                        dialog,
                        "The register number or email already exists.",
                        "Duplicate Record",
                        JOptionPane.WARNING_MESSAGE
                );

            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(
                        dialog,
                        "Could not save student.\n" + ex.getMessage(),
                        "Database Error",
                        JOptionPane.ERROR_MESSAGE
                );
            }
        });

        dialog.setVisible(true);
    }

    private static void editSelectedStudent(
            JTable table, JLabel footer) {

        int selectedRow = table.getSelectedRow();

        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(
                    frame,
                    "Please select a student to edit.");
            return;
        }

        int modelRow = table.convertRowIndexToModel(selectedRow);

        int studentId = (int) model.getValueAt(modelRow, 0);

        String currentRegister = model.getValueAt(modelRow, 1).toString();
        String currentName = model.getValueAt(modelRow, 2).toString();
        String currentDepartment = model.getValueAt(modelRow, 3).toString();
        String currentEmail = model.getValueAt(modelRow, 4).toString();

        Object phoneValue = model.getValueAt(modelRow, 5);
        String currentPhone = phoneValue == null ? "" : phoneValue.toString();

        String currentCgpa = model.getValueAt(modelRow, 6).toString();
        String currentYear = model.getValueAt(modelRow, 7).toString();

        JTextField registerField = new JTextField(currentRegister);
        JTextField nameField = new JTextField(currentName);
        JTextField departmentField = new JTextField(currentDepartment);
        JTextField emailField = new JTextField(currentEmail);
        JTextField phoneField = new JTextField(currentPhone);
        JTextField cgpaField = new JTextField(currentCgpa);
        JTextField yearField = new JTextField(currentYear);

        JPanel panel = new JPanel(new GridLayout(7, 2, 8, 8));

        panel.add(new JLabel("Register Number:"));
        panel.add(registerField);
        panel.add(new JLabel("Full Name:"));
        panel.add(nameField);
        panel.add(new JLabel("Department:"));
        panel.add(departmentField);
        panel.add(new JLabel("Email:"));
        panel.add(emailField);
        panel.add(new JLabel("Phone:"));
        panel.add(phoneField);
        panel.add(new JLabel("CGPA:"));
        panel.add(cgpaField);
        panel.add(new JLabel("Graduation Year:"));
        panel.add(yearField);

        int choice = JOptionPane.showConfirmDialog(
                frame, panel, "Edit Student",
                JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.PLAIN_MESSAGE);

        if (choice != JOptionPane.OK_OPTION) {
            return;
        }

        String registerNo = registerField.getText().trim();
        String fullName = nameField.getText().trim();
        String department = departmentField.getText().trim();
        String email = emailField.getText().trim();
        String phone = phoneField.getText().trim();

        if (registerNo.isEmpty() || fullName.isEmpty()
                || department.isEmpty() || email.isEmpty()
                || cgpaField.getText().trim().isEmpty()
                || yearField.getText().trim().isEmpty()) {

            JOptionPane.showMessageDialog(
                    frame, "Please fill in all required fields.");
            return;
        }

        if (!email.matches("^[^\\s@]+@[^\\s@]+\\.[^\\s@]+$")) {
            JOptionPane.showMessageDialog(
                    frame, "Please enter a valid email address.");
            return;
        }

        try {
            java.math.BigDecimal cgpa =
                    new java.math.BigDecimal(cgpaField.getText().trim());

            int year = Integer.parseInt(yearField.getText().trim());

            if (cgpa.compareTo(java.math.BigDecimal.ZERO) < 0
                    || cgpa.compareTo(java.math.BigDecimal.TEN) > 0
                    || cgpa.scale() > 2) {
                JOptionPane.showMessageDialog(
                        frame, "CGPA must be between 0 and 10, with at most 2 decimal places.");
                return;
            }

            if (year < 2000 || year > 2100) {
                JOptionPane.showMessageDialog(
                        frame, "Please enter a valid graduation year.");
                return;
            }

            String checkSql =
                    "SELECT student_id FROM students "
                    + "WHERE (register_no = ? OR email = ?) "
                    + "AND student_id <> ?";

            try (Connection connection = DBConnection.getConnection()) {

                try (PreparedStatement check = connection.prepareStatement(checkSql)) {
                    check.setString(1, registerNo);
                    check.setString(2, email);
                    check.setInt(3, studentId);

                    try (ResultSet result = check.executeQuery()) {
                        if (result.next()) {
                            JOptionPane.showMessageDialog(
                                    frame,
                                    "That register number or email belongs to another student.");
                            return;
                        }
                    }
                }

                String updateSql =
                        "UPDATE students SET register_no=?, full_name=?, "
                        + "department=?, email=?, phone=?, cgpa=?, graduation_year=? "
                        + "WHERE student_id=?";

                try (PreparedStatement update =
                        connection.prepareStatement(updateSql)) {

                    update.setString(1, registerNo);
                    update.setString(2, fullName);
                    update.setString(3, department);
                    update.setString(4, email);

                    if (phone.isEmpty()) {
                        update.setNull(5, java.sql.Types.VARCHAR);
                    } else {
                        update.setString(5, phone);
                    }

                    update.setBigDecimal(6, cgpa);
                    update.setInt(7, year);
                    update.setInt(8, studentId);

                    update.executeUpdate();
                }
            }

            refreshStudents(footer);

            JOptionPane.showMessageDialog(
                    frame, "Student updated successfully!");

        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(
                    frame, "Enter a valid CGPA and graduation year.");
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(
                    frame, "Could not update student:\n" + ex.getMessage());
        }
    }


    private static void deleteSelectedStudent(
            JTable table, JLabel footer) {

        int selectedRow = table.getSelectedRow();

        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(
                    frame,
                    "Please select a student to delete.");
            return;
        }

        int modelRow = table.convertRowIndexToModel(selectedRow);

        int studentId = (int) model.getValueAt(modelRow, 0);
        String studentName = model.getValueAt(modelRow, 2).toString();

        int choice = JOptionPane.showConfirmDialog(
                frame,
                "Are you sure you want to delete " + studentName + "?",
                "Confirm Deletion",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE);

        if (choice != JOptionPane.YES_OPTION) {
            return;
        }

        String sql = "DELETE FROM students WHERE student_id = ?";

        try (Connection connection = DBConnection.getConnection();
            PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setInt(1, studentId);
            statement.executeUpdate();

            refreshStudents(footer);

            JOptionPane.showMessageDialog(
                    frame, "Student deleted successfully!");

        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(
                    frame,
                    "Could not delete student.\n"
                    + "They may have related placement applications.\n"
                    + ex.getMessage(),
                    "Database Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

}
