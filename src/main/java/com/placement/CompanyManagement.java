package com.placement;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.math.BigDecimal;
import java.sql.*;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;

public class CompanyManagement {

    private static JFrame frame;
    private static DefaultTableModel model;
    private static JLabel footer;

    public static void openWindow() {

        frame = new JFrame("Manage Companies");
        frame.setSize(1100, 550);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setLocationRelativeTo(null);
        frame.setLayout(new BorderLayout());

        Color navyBlue = new Color(25, 55, 109);
        Color lightBlue = new Color(235, 243, 255);

        // Header
        JLabel title = new JLabel(
                "COMPANY MANAGEMENT", SwingConstants.CENTER);

        title.setFont(new Font("Arial", Font.BOLD, 24));
        title.setForeground(navyBlue);
        title.setBorder(
                BorderFactory.createEmptyBorder(20, 10, 15, 10));

        // Add Company button
        // Action buttons
JButton addButton = new JButton("+ Add Company");
JButton editButton = new JButton("Edit Selected");
JButton deleteButton = new JButton("Delete Selected");



addButton.setBackground(navyBlue);
editButton.setBackground(navyBlue);
deleteButton.setBackground(new Color(180, 50, 50));

for (JButton button : new JButton[]{
        addButton, editButton, deleteButton}) {

    button.setFont(new Font("Arial", Font.BOLD, 13));
    button.setForeground(Color.WHITE);
    button.setFocusPainted(false);
    button.setCursor(new Cursor(Cursor.HAND_CURSOR));
}

    // Button panel
    JPanel buttonPanel = new JPanel(
            new FlowLayout(FlowLayout.RIGHT));

    buttonPanel.setBackground(lightBlue);
    buttonPanel.setBorder(
            BorderFactory.createEmptyBorder(0, 15, 10, 20));

    buttonPanel.add(addButton);
    buttonPanel.add(editButton);
    buttonPanel.add(deleteButton);

    // Top panel
    JPanel topPanel = new JPanel(new BorderLayout());
    topPanel.setBackground(lightBlue);
    topPanel.add(title, BorderLayout.NORTH);
    topPanel.add(buttonPanel, BorderLayout.CENTER);

    frame.add(topPanel, BorderLayout.NORTH);

        // Company table
        String[] columns = {
            "Company ID",
            "Company Name",
            "Role Offered",
            "Minimum CGPA",
            "Package (LPA)",
            "Location",
            "Application Deadline"
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

        // Button action
        addButton.addActionListener(e -> showAddCompanyForm());
        editButton.addActionListener(
            e -> editSelectedCompany(table));
        deleteButton.addActionListener(
            e -> deleteSelectedCompany(table));

        // Load existing companies
        refreshCompanies();

        frame.setVisible(true);
    }

    // Load companies from MySQL
    private static void refreshCompanies() {

        model.setRowCount(0);

        String sql = """
                SELECT company_id, company_name, role_offered,
                       minimum_cgpa, package_lpa, location,
                       application_deadline
                FROM companies
                ORDER BY company_id
                """;

        try (
            Connection connection = DBConnection.getConnection();
            Statement statement = connection.createStatement();
            ResultSet result = statement.executeQuery(sql)
        ) {
            while (result.next()) {

                Object[] row = {
                    result.getInt("company_id"),
                    result.getString("company_name"),
                    result.getString("role_offered"),
                    result.getBigDecimal("minimum_cgpa"),
                    result.getBigDecimal("package_lpa"),
                    result.getString("location"),
                    result.getDate("application_deadline")
                };

                model.addRow(row);
            }

            footer.setText(
                    "Total Companies: " + model.getRowCount());

        } catch (SQLException e) {

            JOptionPane.showMessageDialog(
                    frame,
                    "Could not load companies.\n" + e.getMessage(),
                    "Database Error",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    // Add Company form
    private static void showAddCompanyForm() {

        JDialog dialog = new JDialog(
                frame, "Add New Company", true);

        dialog.setSize(480, 460);
        dialog.setLocationRelativeTo(frame);
        dialog.setLayout(new BorderLayout());

        Color navyBlue = new Color(25, 55, 109);

        JLabel heading = new JLabel(
                "Enter Company Details", SwingConstants.CENTER);

        heading.setFont(new Font("Arial", Font.BOLD, 20));
        heading.setForeground(navyBlue);
        heading.setBorder(
                BorderFactory.createEmptyBorder(20, 10, 15, 10));

        dialog.add(heading, BorderLayout.NORTH);

        // Form fields
        JPanel form = new JPanel(new GridLayout(6, 2, 10, 12));

        form.setBorder(
                BorderFactory.createEmptyBorder(15, 25, 15, 25));

        JTextField nameField = new JTextField();
        JTextField roleField = new JTextField();
        JTextField cgpaField = new JTextField();
        JTextField packageField = new JTextField();
        JTextField locationField = new JTextField();
        JTextField deadlineField = new JTextField();

        form.add(new JLabel("Company Name:"));
        form.add(nameField);

        form.add(new JLabel("Role Offered:"));
        form.add(roleField);

        form.add(new JLabel("Minimum CGPA:"));
        form.add(cgpaField);

        form.add(new JLabel("Package (LPA):"));
        form.add(packageField);

        form.add(new JLabel("Location (optional):"));
        form.add(locationField);

        form.add(new JLabel("Deadline (YYYY-MM-DD):"));
        form.add(deadlineField);

        dialog.add(form, BorderLayout.CENTER);

        // Buttons
        JButton saveButton = new JButton("Save Company");
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

        cancelButton.addActionListener(e -> dialog.dispose());

        saveButton.addActionListener(e -> {

            String companyName = nameField.getText().trim();
            String role = roleField.getText().trim();
            String cgpaText = cgpaField.getText().trim();
            String packageText = packageField.getText().trim();
            String location = locationField.getText().trim();
            String deadlineText = deadlineField.getText().trim();

            // Required fields
            if (companyName.isEmpty()
                    || role.isEmpty()
                    || cgpaText.isEmpty()
                    || packageText.isEmpty()) {

                JOptionPane.showMessageDialog(
                        dialog,
                        "Please fill in the company name, role, minimum CGPA, and package.",
                        "Missing Information",
                        JOptionPane.WARNING_MESSAGE
                );
                return;
            }

            BigDecimal minimumCgpa;
            BigDecimal packageLpa;
            java.sql.Date deadline = null;

            // Validate numbers
            try {
                minimumCgpa = new BigDecimal(cgpaText);
                packageLpa = new BigDecimal(packageText);

                if (minimumCgpa.compareTo(BigDecimal.ZERO) < 0
                        || minimumCgpa.compareTo(BigDecimal.TEN) > 0
                        || minimumCgpa.scale() > 2) {

                    JOptionPane.showMessageDialog(
                            dialog,
                            "Minimum CGPA must be between 0 and 10, with at most 2 decimal places.",
                            "Invalid CGPA",
                            JOptionPane.WARNING_MESSAGE
                    );
                    return;
                }

                if (packageLpa.compareTo(BigDecimal.ZERO) <= 0
                        || packageLpa.scale() > 2
                        || packageLpa.precision() - packageLpa.scale() > 4) {

                    JOptionPane.showMessageDialog(
                            dialog,
                            "Package must be positive and fit the database's decimal format.",
                            "Invalid Package",
                            JOptionPane.WARNING_MESSAGE
                    );
                    return;
                }

            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(
                        dialog,
                        "Enter valid numbers for CGPA and package.",
                        "Invalid Input",
                        JOptionPane.WARNING_MESSAGE
                );
                return;
            }

            // Validate optional deadline
            if (!deadlineText.isEmpty()) {
                try {
                    deadline = java.sql.Date.valueOf(
                            LocalDate.parse(deadlineText));
                } catch (DateTimeParseException ex) {
                    JOptionPane.showMessageDialog(
                            dialog,
                            "Enter the deadline in YYYY-MM-DD format, e.g. 2027-01-31.",
                            "Invalid Deadline",
                            JOptionPane.WARNING_MESSAGE
                    );
                    return;
                }
            }

            // Check for duplicate company name
            String checkSql =
                    "SELECT company_id FROM companies "
                    + "WHERE LOWER(company_name) = LOWER(?)";

            try (Connection connection = DBConnection.getConnection()) {

                try (PreparedStatement check =
                        connection.prepareStatement(checkSql)) {

                    check.setString(1, companyName);

                    try (ResultSet result = check.executeQuery()) {
                        if (result.next()) {
                            JOptionPane.showMessageDialog(
                                    dialog,
                                    "A company with this name already exists!",
                                    "Duplicate Company",
                                    JOptionPane.WARNING_MESSAGE
                            );
                            return;
                        }
                    }
                }

                // Insert company
                String insertSql =
                        "INSERT INTO companies "
                        + "(company_name, role_offered, minimum_cgpa, "
                        + "package_lpa, location, application_deadline) "
                        + "VALUES (?, ?, ?, ?, ?, ?)";

                try (PreparedStatement insert =
                        connection.prepareStatement(insertSql)) {

                    insert.setString(1, companyName);
                    insert.setString(2, role);
                    insert.setBigDecimal(3, minimumCgpa);
                    insert.setBigDecimal(4, packageLpa);

                    if (location.isEmpty()) {
                        insert.setNull(5, Types.VARCHAR);
                    } else {
                        insert.setString(5, location);
                    }

                    if (deadline == null) {
                        insert.setNull(6, Types.DATE);
                    } else {
                        insert.setDate(6, deadline);
                    }

                    insert.executeUpdate();
                }

                JOptionPane.showMessageDialog(
                        dialog,
                        "Company added successfully!",
                        "Success",
                        JOptionPane.INFORMATION_MESSAGE
                );

                dialog.dispose();
                refreshCompanies();

            } catch (SQLIntegrityConstraintViolationException ex) {
                JOptionPane.showMessageDialog(
                        dialog,
                        "This company may already exist in the database.",
                        "Duplicate Company",
                        JOptionPane.WARNING_MESSAGE
                );

            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(
                        dialog,
                        "Could not save company.\n" + ex.getMessage(),
                        "Database Error",
                        JOptionPane.ERROR_MESSAGE
                );
            }
        });

        dialog.setVisible(true);
    }

   private static void editSelectedCompany(JTable table) {

    int selectedRow = table.getSelectedRow();

    if (selectedRow == -1) {
        JOptionPane.showMessageDialog(
                frame,
                "Please select a company to edit.",
                "No Company Selected",
                JOptionPane.WARNING_MESSAGE);
        return;
    }

    int row = table.convertRowIndexToModel(selectedRow);

    int companyId = ((Number) model.getValueAt(row, 0)).intValue();

    String currentName = model.getValueAt(row, 1).toString();
    String currentRole = model.getValueAt(row, 2).toString();
    String currentCgpa = model.getValueAt(row, 3).toString();
    String currentPackage = model.getValueAt(row, 4).toString();

    Object locationValue = model.getValueAt(row, 5);
    String currentLocation =
            locationValue == null ? "" : locationValue.toString();

    Object deadlineValue = model.getValueAt(row, 6);
    String currentDeadline =
            deadlineValue == null ? "" : deadlineValue.toString();

    JTextField nameField = new JTextField(currentName);
    JTextField roleField = new JTextField(currentRole);
    JTextField cgpaField = new JTextField(currentCgpa);
    JTextField packageField = new JTextField(currentPackage);
    JTextField locationField = new JTextField(currentLocation);
    JTextField deadlineField = new JTextField(currentDeadline);

    JPanel panel = new JPanel(new GridLayout(6, 2, 8, 8));

    panel.add(new JLabel("Company Name:"));
    panel.add(nameField);

    panel.add(new JLabel("Role Offered:"));
    panel.add(roleField);

    panel.add(new JLabel("Minimum CGPA:"));
    panel.add(cgpaField);

    panel.add(new JLabel("Package (LPA):"));
    panel.add(packageField);

    panel.add(new JLabel("Location:"));
    panel.add(locationField);

    panel.add(new JLabel("Deadline (YYYY-MM-DD):"));
    panel.add(deadlineField);

    int choice = JOptionPane.showConfirmDialog(
            frame,
            panel,
            "Edit Company",
            JOptionPane.OK_CANCEL_OPTION,
            JOptionPane.PLAIN_MESSAGE);

    if (choice != JOptionPane.OK_OPTION) {
        return;
    }

    String name = nameField.getText().trim();
    String role = roleField.getText().trim();
    String cgpaText = cgpaField.getText().trim();
    String packageText = packageField.getText().trim();
    String location = locationField.getText().trim();
    String deadlineText = deadlineField.getText().trim();

    if (name.isEmpty() || role.isEmpty()
            || cgpaText.isEmpty() || packageText.isEmpty()) {

        JOptionPane.showMessageDialog(
                frame,
                "Company name, role, minimum CGPA, and package are required.");
        return;
    }

    java.math.BigDecimal cgpa;
    java.math.BigDecimal packageLpa;
    java.sql.Date deadline = null;

    try {
        cgpa = new java.math.BigDecimal(cgpaText);
        packageLpa = new java.math.BigDecimal(packageText);

        if (cgpa.compareTo(java.math.BigDecimal.ZERO) < 0
                || cgpa.compareTo(java.math.BigDecimal.TEN) > 0
                || cgpa.scale() > 2) {

            JOptionPane.showMessageDialog(
                    frame,
                    "Minimum CGPA must be between 0 and 10, with at most 2 decimal places.");
            return;
        }

        if (packageLpa.compareTo(java.math.BigDecimal.ZERO) <= 0
                || packageLpa.scale() > 2
                || packageLpa.precision() - packageLpa.scale() > 4) {

            JOptionPane.showMessageDialog(
                    frame,
                    "Enter a positive package that fits the database format.");
            return;
        }

        if (!deadlineText.isEmpty()) {
            deadline = java.sql.Date.valueOf(
                    java.time.LocalDate.parse(deadlineText));
        }

    } catch (NumberFormatException ex) {
        JOptionPane.showMessageDialog(
                frame,
                "Enter valid numbers for CGPA and package.");
        return;

    } catch (IllegalArgumentException ex) {
        JOptionPane.showMessageDialog(
                frame,
                "Enter a valid deadline in YYYY-MM-DD format.");
        return;
    }

    try (Connection connection = DBConnection.getConnection()) {

        // Check whether another company has the same name
        String checkSql =
                "SELECT company_id FROM companies "
                + "WHERE LOWER(company_name) = LOWER(?) "
                + "AND company_id <> ?";

        try (PreparedStatement check =
                connection.prepareStatement(checkSql)) {

            check.setString(1, name);
            check.setInt(2, companyId);

            try (ResultSet result = check.executeQuery()) {
                if (result.next()) {
                    JOptionPane.showMessageDialog(
                            frame,
                            "Another company already uses this name.",
                            "Duplicate Company",
                            JOptionPane.WARNING_MESSAGE);
                    return;
                }
            }
        }

        // Update the selected company
        String updateSql =
                "UPDATE companies SET company_name=?, role_offered=?, "
                + "minimum_cgpa=?, package_lpa=?, location=?, "
                + "application_deadline=? WHERE company_id=?";

        try (PreparedStatement update =
                connection.prepareStatement(updateSql)) {

            update.setString(1, name);
            update.setString(2, role);
            update.setBigDecimal(3, cgpa);
            update.setBigDecimal(4, packageLpa);

            if (location.isEmpty()) {
                update.setNull(5, java.sql.Types.VARCHAR);
            } else {
                update.setString(5, location);
            }

            if (deadline == null) {
                update.setNull(6, java.sql.Types.DATE);
            } else {
                update.setDate(6, deadline);
            }

            update.setInt(7, companyId);
            update.executeUpdate();
        }

        refreshCompanies();

        JOptionPane.showMessageDialog(
                frame,
                "Company updated successfully!",
                "Success",
                JOptionPane.INFORMATION_MESSAGE);

    } catch (SQLException ex) {
        JOptionPane.showMessageDialog(
                frame,
                "Could not update company.\n" + ex.getMessage(),
                "Database Error",
                JOptionPane.ERROR_MESSAGE);
    }
}

private static void deleteSelectedCompany(JTable table) {

    int selectedRow = table.getSelectedRow();

    if (selectedRow == -1) {
        JOptionPane.showMessageDialog(
                frame,
                "Please select a company to delete.",
                "No Company Selected",
                JOptionPane.WARNING_MESSAGE);
        return;
    }

    int row = table.convertRowIndexToModel(selectedRow);

    int companyId = ((Number) model.getValueAt(row, 0)).intValue();
    String companyName = model.getValueAt(row, 1).toString();

    int choice = JOptionPane.showConfirmDialog(
            frame,
            "Are you sure you want to delete "
                    + companyName + "?\n"
                    + "This action cannot be undone.",
            "Confirm Company Deletion",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.WARNING_MESSAGE);

    if (choice != JOptionPane.YES_OPTION) {
        return;
    }

    String sql =
            "DELETE FROM companies WHERE company_id = ?";

    try (Connection connection = DBConnection.getConnection();
         PreparedStatement statement =
                 connection.prepareStatement(sql)) {

        statement.setInt(1, companyId);

        int affectedRows = statement.executeUpdate();

        if (affectedRows > 0) {
            refreshCompanies();

            JOptionPane.showMessageDialog(
                    frame,
                    "Company deleted successfully!",
                    "Success",
                    JOptionPane.INFORMATION_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(
                    frame,
                    "The company was not found. Refresh the table and try again.");
        }

    } catch (SQLIntegrityConstraintViolationException ex) {

        JOptionPane.showMessageDialog(
                frame,
                "This company has related placement applications "
                        + "and cannot be deleted.\n\n"
                        + "Keep the company record so the application "
                        + "history remains intact.",
                "Company Has Applications",
                JOptionPane.WARNING_MESSAGE);

    } catch (SQLException ex) {

        JOptionPane.showMessageDialog(
                frame,
                "Could not delete company.\n" + ex.getMessage(),
                "Database Error",
                JOptionPane.ERROR_MESSAGE);
    }
}
}