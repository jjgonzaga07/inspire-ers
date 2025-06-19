package com.inspire.ers;

import javax.swing.*;
import java.awt.*;
import java.util.Date;
import java.util.Calendar;
import java.text.SimpleDateFormat;

public class EmployeeForm extends JFrame {
    private HomePage homePage;
    private Employee employee;
    private boolean isEditing;
    
    private JTextField firstNameField;
    private JTextField lastNameField;
    private JTextField middleNameField;
    private JTextField idNumberField;
    private JSpinner dateHiredSpinner;
    private JTextField emailField;
    private JTextField addressField;
    private JTextField cellphoneField;
    private JTextField positionField;
    private JTextField basicPayField;
    private JTextField execAllowanceField;
    private JTextField marketingAllowanceField;
    private JTextField monthlySalaryField;
    private JTextField sssField;
    private JTextField philHealthField;
    private JTextField pagIbigField;
    private JTextField tinField;
    private JTextField bankAccountField;
    
    public EmployeeForm(HomePage homePage) {
        this(homePage, null);
    }
    
    public EmployeeForm(HomePage homePage, Employee employee) {
        this.homePage = homePage;
        this.employee = employee;
        this.isEditing = (employee != null);
        
        setTitle(isEditing ? "Edit Employee" : "Add Employee");
        setSize(800, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        
        // Main panel
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        // Employee Profile Section
        JPanel profilePanel = new JPanel(new GridLayout(0, 2, 10, 10));
        profilePanel.setBorder(BorderFactory.createTitledBorder("EMPLOYEE PROFILE"));
        
        firstNameField = new JTextField(20);
        lastNameField = new JTextField(20);
        middleNameField = new JTextField(20);
        idNumberField = new JTextField(20);
        
        // Create date spinner
        SpinnerDateModel dateModel = new SpinnerDateModel();
        dateHiredSpinner = new JSpinner(dateModel);
        JSpinner.DateEditor dateEditor = new JSpinner.DateEditor(dateHiredSpinner, "MM/dd/yyyy");
        dateHiredSpinner.setEditor(dateEditor);
        
        emailField = new JTextField(20);
        addressField = new JTextField(20);
        cellphoneField = new JTextField(20);
        
        profilePanel.add(new JLabel("First Name"));
        profilePanel.add(firstNameField);
        profilePanel.add(new JLabel("Last Name"));
        profilePanel.add(lastNameField);
        profilePanel.add(new JLabel("Middle Name"));
        profilePanel.add(middleNameField);
        profilePanel.add(new JLabel("ID Number"));
        profilePanel.add(idNumberField);
        profilePanel.add(new JLabel("Date Hired"));
        profilePanel.add(dateHiredSpinner);
        profilePanel.add(new JLabel("Email Address"));
        profilePanel.add(emailField);
        profilePanel.add(new JLabel("Current Address"));
        profilePanel.add(addressField);
        profilePanel.add(new JLabel("Cellphone No."));
        profilePanel.add(cellphoneField);
        
        // Salary Section
        JPanel salaryPanel = new JPanel(new GridLayout(0, 2, 10, 10));
        salaryPanel.setBorder(BorderFactory.createTitledBorder("SALARY"));
        
        positionField = new JTextField(20);
        basicPayField = new JTextField(20);
        execAllowanceField = new JTextField(20);
        marketingAllowanceField = new JTextField(20);
        monthlySalaryField = new JTextField(20);
        
        salaryPanel.add(new JLabel("Position"));
        salaryPanel.add(positionField);
        salaryPanel.add(new JLabel("Basic Pay"));
        salaryPanel.add(basicPayField);
        salaryPanel.add(new JLabel("Executive Allowance"));
        salaryPanel.add(execAllowanceField);
        salaryPanel.add(new JLabel("Marketing/Transpo Allowance"));
        salaryPanel.add(marketingAllowanceField);
        salaryPanel.add(new JLabel("Monthly Salary"));
        salaryPanel.add(monthlySalaryField);
        
        // Benefits Section
        JPanel benefitsPanel = new JPanel(new GridLayout(0, 2, 10, 10));
        benefitsPanel.setBorder(BorderFactory.createTitledBorder("BENEFITS"));
        
        sssField = new JTextField(20);
        philHealthField = new JTextField(20);
        pagIbigField = new JTextField(20);
        tinField = new JTextField(20);
        bankAccountField = new JTextField(20);
        
        benefitsPanel.add(new JLabel("SSS Number"));
        benefitsPanel.add(sssField);
        benefitsPanel.add(new JLabel("PhilHealth Number"));
        benefitsPanel.add(philHealthField);
        benefitsPanel.add(new JLabel("Pag-IBIG Number"));
        benefitsPanel.add(pagIbigField);
        benefitsPanel.add(new JLabel("TIN Number"));
        benefitsPanel.add(tinField);
        benefitsPanel.add(new JLabel("Bank Account"));
        benefitsPanel.add(bankAccountField);
        
        // Submit Button
        JButton submitButton = new JButton("SUBMIT");
        submitButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        submitButton.addActionListener(e -> {
            if (validateForm()) {
                saveEmployee();
                dispose();
            }
        });
        
        // Add all panels to main panel
        mainPanel.add(profilePanel);
        mainPanel.add(Box.createVerticalStrut(20));
        mainPanel.add(salaryPanel);
        mainPanel.add(Box.createVerticalStrut(20));
        mainPanel.add(benefitsPanel);
        mainPanel.add(Box.createVerticalStrut(20));
        mainPanel.add(submitButton);
        
        // If editing, populate fields
        if (isEditing) {
            populateFields();
        }
        
        // Add main panel to frame
        add(new JScrollPane(mainPanel));
    }
    
    private void populateFields() {
        firstNameField.setText(employee.getFirstName());
        lastNameField.setText(employee.getLastName());
        middleNameField.setText(employee.getMiddleName());
        idNumberField.setText(employee.getIdNumber());
        dateHiredSpinner.setValue(employee.getDateHired());
        emailField.setText(employee.getEmailAddress());
        addressField.setText(employee.getCurrentAddress());
        cellphoneField.setText(employee.getCellphoneNo());
        positionField.setText(employee.getPosition());
        basicPayField.setText(String.valueOf(employee.getBasicPay()));
        execAllowanceField.setText(String.valueOf(employee.getExecutiveAllowance()));
        marketingAllowanceField.setText(String.valueOf(employee.getMarketingTranspoAllowance()));
        monthlySalaryField.setText(String.valueOf(employee.getMonthlySalary()));
        sssField.setText(employee.getSssNumber());
        philHealthField.setText(employee.getPhilHealthNumber());
        pagIbigField.setText(employee.getPagIbigNumber());
        tinField.setText(employee.getTinNumber());
        bankAccountField.setText(employee.getBankAccount());
    }
    
    private boolean validateForm() {
        // Add validation logic here
        if (firstNameField.getText().trim().isEmpty() ||
            lastNameField.getText().trim().isEmpty() ||
            idNumberField.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this,
                "Please fill in all required fields!",
                "Validation Error",
                JOptionPane.ERROR_MESSAGE);
            return false;
        }
        return true;
    }
    
    private void saveEmployee() {
        if (employee == null) {
            employee = new Employee();
        }
        
        employee.setFirstName(firstNameField.getText().trim());
        employee.setLastName(lastNameField.getText().trim());
        employee.setMiddleName(middleNameField.getText().trim());
        employee.setIdNumber(idNumberField.getText().trim());
        employee.setDateHired((Date) dateHiredSpinner.getValue());
        employee.setEmailAddress(emailField.getText().trim());
        employee.setCurrentAddress(addressField.getText().trim());
        employee.setCellphoneNo(cellphoneField.getText().trim());
        employee.setPosition(positionField.getText().trim());
        
        try {
            employee.setBasicPay(Double.parseDouble(basicPayField.getText().trim()));
            employee.setExecutiveAllowance(Double.parseDouble(execAllowanceField.getText().trim()));
            employee.setMarketingTranspoAllowance(Double.parseDouble(marketingAllowanceField.getText().trim()));
            employee.setMonthlySalary(Double.parseDouble(monthlySalaryField.getText().trim()));
        } catch (NumberFormatException e) {
            // Handle parsing errors if needed
        }
        
        employee.setSssNumber(sssField.getText().trim());
        employee.setPhilHealthNumber(philHealthField.getText().trim());
        employee.setPagIbigNumber(pagIbigField.getText().trim());
        employee.setTinNumber(tinField.getText().trim());
        employee.setBankAccount(bankAccountField.getText().trim());
        
        if (!isEditing) {
            homePage.addEmployee(employee);
        }
    }
} 