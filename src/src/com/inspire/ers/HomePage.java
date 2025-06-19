package com.inspire.ers;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;

public class HomePage extends JFrame {
    private ArrayList<Employee> employees = new ArrayList<>();
    private JLabel employeeCountLabel;
    private JPanel employeeListPanel;
    
    public HomePage() {
        setTitle("Inspire ERS - Home");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(800, 600);
        setLocationRelativeTo(null);
        
        // Main panel
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        // Top panel for search
        JPanel topPanel = new JPanel(new BorderLayout());
        JTextField searchField = new JTextField(20);
        searchField.setPreferredSize(new Dimension(200, 30));
        topPanel.add(searchField, BorderLayout.EAST);
        
        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        
        JButton addEmployeeBtn = new JButton("ADD EMPLOYEE");
        JButton payrollBtn = new JButton("PAYROLL");
        
        buttonPanel.add(addEmployeeBtn);
        buttonPanel.add(payrollBtn);
        
        // Employee count label
        employeeCountLabel = new JLabel("#Employee: 0");
        buttonPanel.add(Box.createHorizontalStrut(50));
        buttonPanel.add(employeeCountLabel);
        
        topPanel.add(buttonPanel, BorderLayout.WEST);
        
        // Employee list panel
        employeeListPanel = new JPanel();
        employeeListPanel.setLayout(new BoxLayout(employeeListPanel, BoxLayout.Y_AXIS));
        JScrollPane scrollPane = new JScrollPane(employeeListPanel);
        
        // Add components to main panel
        mainPanel.add(topPanel, BorderLayout.NORTH);
        mainPanel.add(scrollPane, BorderLayout.CENTER);
        
        // Add action listeners
        addEmployeeBtn.addActionListener(e -> {
            EmployeeForm employeeForm = new EmployeeForm(this);
            employeeForm.setVisible(true);
        });
        
        payrollBtn.addActionListener(e -> {
            if (employees.isEmpty()) {
                JOptionPane.showMessageDialog(this,
                    "No employees added yet!",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
                return;
            }
            // Show employee selection dialog for payroll
            String[] employeeNames = employees.stream()
                .map(emp -> emp.getFirstName() + " " + emp.getLastName())
                .toArray(String[]::new);
            
            String selectedEmployee = (String) JOptionPane.showInputDialog(
                this,
                "Select Employee:",
                "Payroll",
                JOptionPane.QUESTION_MESSAGE,
                null,
                employeeNames,
                employeeNames[0]);
                
            if (selectedEmployee != null) {
                PayrollPage payrollPage = new PayrollPage(selectedEmployee);
                payrollPage.setVisible(true);
            }
        });
        
        searchField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                String searchText = searchField.getText().toLowerCase();
                filterEmployeeList(searchText);
            }
        });
        
        add(mainPanel);
    }
    
    public void addEmployee(Employee employee) {
        employees.add(employee);
        updateEmployeeList();
        employeeCountLabel.setText("#Employee: " + employees.size());
    }
    
    private void updateEmployeeList() {
        employeeListPanel.removeAll();
        
        for (Employee employee : employees) {
            JPanel employeePanel = new JPanel(new BorderLayout());
            employeePanel.setBorder(BorderFactory.createEtchedBorder());
            
            JLabel nameLabel = new JLabel(employee.getFirstName() + " " + employee.getLastName());
            nameLabel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
            
            JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
            JButton editBtn = new JButton("Edit Information");
            JButton payrollBtn = new JButton("Payroll");
            
            buttonPanel.add(editBtn);
            buttonPanel.add(payrollBtn);
            
            employeePanel.add(nameLabel, BorderLayout.WEST);
            employeePanel.add(buttonPanel, BorderLayout.EAST);
            
            editBtn.addActionListener(e -> {
                EmployeeForm editForm = new EmployeeForm(this, employee);
                editForm.setVisible(true);
            });
            
            payrollBtn.addActionListener(e -> {
                PayrollPage payrollPage = new PayrollPage(employee.getFirstName() + " " + employee.getLastName());
                payrollPage.setVisible(true);
            });
            
            employeeListPanel.add(employeePanel);
            employeeListPanel.add(Box.createVerticalStrut(5));
        }
        
        employeeListPanel.revalidate();
        employeeListPanel.repaint();
    }
    
    private void filterEmployeeList(String searchText) {
        employeeListPanel.removeAll();
        
        for (Employee employee : employees) {
            String fullName = (employee.getFirstName() + " " + employee.getLastName()).toLowerCase();
            if (fullName.contains(searchText)) {
                // Add matching employee panel (similar to updateEmployeeList)
                JPanel employeePanel = new JPanel(new BorderLayout());
                // ... (same code as in updateEmployeeList for creating employee panel)
                employeeListPanel.add(employeePanel);
                employeeListPanel.add(Box.createVerticalStrut(5));
            }
        }
        
        employeeListPanel.revalidate();
        employeeListPanel.repaint();
    }
} 