package com.inspire.ers;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.List;

public class HomePage extends JFrame {
    private ArrayList<Employee> employees = new ArrayList<>();
    private JLabel employeeCountLabel;
    private JPanel employeeListPanel;
    private JPanel mainPanel;

    public HomePage() {
        setTitle("Inspire ERS - Home");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(800, 600);
        setLocationRelativeTo(null);

        // Main panel
        mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Top panel for search and buttons
        JPanel topPanel = new JPanel(new BorderLayout());
        JTextField searchField = new JTextField(20);
        searchField.setPreferredSize(new Dimension(200, 30));
        topPanel.add(searchField, BorderLayout.EAST);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton addEmployeeBtn = new JButton("ADD EMPLOYEE");
//        JButton payrollBtn = new JButton("PAYROLL");

        buttonPanel.add(addEmployeeBtn);
//        buttonPanel.add(payrollBtn);

        employeeCountLabel = new JLabel("#Employee: 0");
        buttonPanel.add(Box.createHorizontalStrut(50));
        buttonPanel.add(employeeCountLabel);
        topPanel.add(buttonPanel, BorderLayout.WEST);

        // Employee list panel
        employeeListPanel = new JPanel();
        employeeListPanel.setLayout(new BoxLayout(employeeListPanel, BoxLayout.Y_AXIS));

        // Add top panel first
        mainPanel.add(topPanel, BorderLayout.NORTH);
        mainPanel.add(new JPanel(), BorderLayout.CENTER); // placeholder

        add(mainPanel);

        // Button actions
        addEmployeeBtn.addActionListener(e -> {
            EmployeeForm employeeForm = new EmployeeForm(this);
            employeeForm.setVisible(true);
        });

//        payrollBtn.addActionListener(e -> {
//            if (employees.isEmpty()) {
//                JOptionPane.showMessageDialog(this,
//                        "No employees added yet!",
//                        "Error",
//                        JOptionPane.ERROR_MESSAGE);
//                return;
//            }
//
//            String[] employeeNames = employees.stream()
//                    .map(emp -> emp.getFirstName() + " " + emp.getLastName())
//                    .toArray(String[]::new);
//
//            String selectedEmployee = (String) JOptionPane.showInputDialog(
//                    this,
//                    "Select Employee:",
//                    "Payroll",
//                    JOptionPane.QUESTION_MESSAGE,
//                    null,
//                    employeeNames,
//                    employeeNames[0]);
//
//            if (selectedEmployee != null) {
//                PayrollPage payrollPage = new PayrollPage(selectedEmployee);
//                payrollPage.setVisible(true);
//            }
//        });

        searchField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                String searchText = searchField.getText().toLowerCase();
                filterEmployeeList(searchText);
            }
        });

        // Load employees from DB
        loadEmployeesFromDB();
    }

    public void addEmployee(Employee employee) {
        employees.add(employee);
        updateEmployeeList();
        employeeCountLabel.setText("#Employee: " + employees.size());
    }

    private void loadEmployeesFromDB() {
        List<Employee> dbEmployees = EmployeeDAO.fetchAllEmployees();
        employees.addAll(dbEmployees);
        updateEmployeeList();
        employeeCountLabel.setText("#Employee: " + employees.size());
    }

    private void updateEmployeeList() {
        employeeListPanel.removeAll();

        for (Employee employee : employees) {
            employeeListPanel.add(createEmployeePanel(employee));
            employeeListPanel.add(Box.createVerticalStrut(5));
        }

        employeeListPanel.revalidate();
        employeeListPanel.repaint();

        // Replace the CENTER content based on employee count
        Component oldCenter = ((BorderLayout) mainPanel.getLayout()).getLayoutComponent(BorderLayout.CENTER);
        if (oldCenter != null) {
            mainPanel.remove(oldCenter);
        }

        if (employees.size() > 10) {
            JScrollPane scrollPane = new JScrollPane(employeeListPanel);
            scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
            mainPanel.add(scrollPane, BorderLayout.CENTER);
        } else {
            mainPanel.add(employeeListPanel, BorderLayout.CENTER);
        }

        mainPanel.revalidate();
        mainPanel.repaint();
    }

    private JPanel createEmployeePanel(Employee employee) {
    JPanel employeePanel = new JPanel(new BorderLayout());
    employeePanel.setBorder(BorderFactory.createEtchedBorder());

    JLabel nameLabel = new JLabel(employee.getFirstName() + " " + employee.getLastName());
    nameLabel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
    employeePanel.add(nameLabel, BorderLayout.WEST);

    JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
    JButton editBtn = new JButton("Edit");
    JButton payrollBtn = new JButton("Payroll");
    JButton removeBtn = new JButton("Remove");
//    removeBtn.setForeground(Color.WHITE);
//    removeBtn.setBackground(Color.RED);
//    removeBtn.setOpaque(true);
//    removeBtn.setBorderPainted(false); 

    buttonPanel.add(payrollBtn);
    buttonPanel.add(editBtn);
    buttonPanel.add(removeBtn);

    JPanel rightWrapper = new JPanel(new GridBagLayout());
    rightWrapper.add(buttonPanel);
    employeePanel.add(rightWrapper, BorderLayout.EAST);

    editBtn.addActionListener(e -> {
        EmployeeForm editForm = new EmployeeForm(this, employee);
        editForm.setVisible(true);
    });

    payrollBtn.addActionListener(e -> {
        PayrollPage payrollPage = new PayrollPage(employee.getFirstName() + " " + employee.getLastName());
        payrollPage.setVisible(true);
    });

    removeBtn.addActionListener(e -> {
        int confirm = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to remove this employee?",
                "Confirm Remove", JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            EmployeeDAO.softRemoveEmployee(employee.getId()); // ← Update DB only
            refreshEmployeeList(); // ← Reload UI
        }
    });

    return employeePanel;
}

    private void filterEmployeeList(String searchText) {
        employeeListPanel.removeAll();

        for (Employee employee : employees) {
            String fullName = (employee.getFirstName() + " " + employee.getLastName()).toLowerCase();
            if (fullName.contains(searchText)) {
                employeeListPanel.add(createEmployeePanel(employee));
                employeeListPanel.add(Box.createVerticalStrut(5));
            }
        }
        employeeListPanel.revalidate();
        employeeListPanel.repaint();
    }

    public void refreshEmployeeList() {
        employees.clear();
        loadEmployeesFromDB();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new HomePage().setVisible(true);
        });
    }
}
