package com.placement;

import javax.swing.*;
import java.awt.*;

public class Main {
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> createWindow());
    }

    private static void createWindow() {

        JFrame frame = new JFrame("Student Placement Management System");
        frame.setSize(1000, 650);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null);
        frame.setLayout(new BorderLayout());

        // Theme colours
        Color navyBlue = new Color(25, 55, 109);
        Color lightBlue = new Color(235, 243, 255);
        Color white = Color.WHITE;

        // ===== HEADER =====
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(navyBlue);
        header.setBorder(
                BorderFactory.createEmptyBorder(25, 30, 25, 30));

        JLabel title = new JLabel("STUDENT PLACEMENT MANAGEMENT SYSTEM");
        title.setForeground(white);
        title.setFont(new Font("Arial", Font.BOLD, 23));

        JLabel subtitle = new JLabel("Your Placement Journey Starts Here");
        subtitle.setForeground(new Color(220, 230, 255));
        subtitle.setFont(new Font("Arial", Font.PLAIN, 14));

        JPanel heading = new JPanel();
        heading.setLayout(new BoxLayout(heading, BoxLayout.Y_AXIS));
        heading.setBackground(navyBlue);
        heading.add(title);
        heading.add(Box.createVerticalStrut(8));
        heading.add(subtitle);

        header.add(heading, BorderLayout.WEST);
        frame.add(header, BorderLayout.NORTH);

        // ===== MAIN CONTENT =====
        JPanel mainPanel = new JPanel(new GridBagLayout());
        mainPanel.setBackground(lightBlue);

        JPanel content = new JPanel();
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        content.setBackground(lightBlue);

        JLabel welcome = new JLabel("Welcome, Student!");
        welcome.setFont(new Font("Arial", Font.BOLD, 28));
        welcome.setForeground(navyBlue);
        welcome.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel description = new JLabel(
                "Manage student details, companies and applications.");
        description.setFont(new Font("Arial", Font.PLAIN, 15));
        description.setForeground(Color.DARK_GRAY);
        description.setAlignmentX(Component.CENTER_ALIGNMENT);

        content.add(welcome);
        content.add(Box.createVerticalStrut(10));
        content.add(description);
        content.add(Box.createVerticalStrut(35));

        // Navigation buttons
        JButton studentsButton = createButton("Manage Students", navyBlue);
        JButton companiesButton = createButton("Manage Companies", navyBlue);
        JButton applicationsButton = createButton("View Applications", navyBlue);
        JButton exitButton = createButton("Exit", new Color(180, 50, 50));

        content.add(studentsButton);
        content.add(Box.createVerticalStrut(15));
        content.add(companiesButton);
        content.add(Box.createVerticalStrut(15));
        content.add(applicationsButton);
        content.add(Box.createVerticalStrut(15));
        content.add(exitButton);

        // Temporary messages until we implement each feature
        studentsButton.addActionListener(e ->
        StudentManagement.openWindow());

        companiesButton.addActionListener(e ->
        CompanyManagement.openWindow());

        applicationsButton.addActionListener(e ->
        ApplicationManagement.openWindow());

        exitButton.addActionListener(e -> {
            int choice = JOptionPane.showConfirmDialog(
                    frame,
                    "Are you sure you want to exit?",
                    "Confirm Exit",
                    JOptionPane.YES_NO_OPTION
            );

            if (choice == JOptionPane.YES_OPTION) {
                frame.dispose();
            }
        });

        mainPanel.add(content);
        frame.add(mainPanel, BorderLayout.CENTER);

        // ===== FOOTER =====
        JLabel footer = new JLabel(
                "BE CSE Project | Java Swing | JDBC | MySQL",
                SwingConstants.CENTER
        );

        footer.setFont(new Font("Arial", Font.PLAIN, 12));
        footer.setForeground(Color.GRAY);
        footer.setBorder(
                BorderFactory.createEmptyBorder(15, 10, 15, 10));

        frame.add(footer, BorderLayout.SOUTH);

        frame.setVisible(true);
    }

    private static JButton createButton(String text, Color background) {

        JButton button = new JButton(text);
        button.setFont(new Font("Arial", Font.BOLD, 15));
        button.setForeground(Color.WHITE);
        button.setBackground(background);
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setAlignmentX(Component.CENTER_ALIGNMENT);
        button.setMaximumSize(new Dimension(280, 48));
        button.setPreferredSize(new Dimension(280, 48));
        button.setBorder(
                BorderFactory.createEmptyBorder(10, 20, 10, 20));

        return button;
    }
}
