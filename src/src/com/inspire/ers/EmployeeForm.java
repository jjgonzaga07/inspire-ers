package com.inspire.ers;

import javax.swing.*;
import java.awt.*;
import java.util.Date;
<<<<<<< HEAD
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import src.com.inspire.ers.DBUtil;
=======
import java.util.Calendar;
import java.text.SimpleDateFormat;
>>>>>>> uilogin

public class EmployeeForm extends JFrame {
    private HomePage homePage;
    private Employee employee;
    private boolean isEditing;
<<<<<<< HEAD

    // UI Components
    private JTextField firstNameField, lastNameField, middleNameField, idNumberField, emailField;
    private JTextField addressField, cellphoneField, positionField, basicPayField;
    private JTextField execAllowanceField, marketingAllowanceField, monthlySalaryField;
    private JTextField sssField, philHealthField, pagIbigField, tinField, bankAccountField;
    private JSpinner dateHiredSpinner;

    public EmployeeForm(HomePage homePage) {
        this(homePage, null);
    }

=======
    
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
    
>>>>>>> uilogin
    public EmployeeForm(HomePage homePage, Employee employee) {
        this.homePage = homePage;
        this.employee = employee;
        this.isEditing = (employee != null);
<<<<<<< HEAD

=======
        
>>>>>>> uilogin
        setTitle(isEditing ? "Edit Employee" : "Add Employee");
        setSize(800, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
<<<<<<< HEAD

        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Employee Profile Section
        JPanel profilePanel = new JPanel(new GridLayout(0, 2, 10, 10));
        profilePanel.setBorder(BorderFactory.createTitledBorder("EMPLOYEE PROFILE"));

=======
        
        // Main panel
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        // Employee Profile Section
        JPanel profilePanel = new JPanel(new GridLayout(0, 2, 10, 10));
        profilePanel.setBorder(BorderFactory.createTitledBorder("EMPLOYEE PROFILE"));
        
>>>>>>> uilogin
        firstNameField = new JTextField(20);
        lastNameField = new JTextField(20);
        middleNameField = new JTextField(20);
        idNumberField = new JTextField(20);
<<<<<<< HEAD

=======
        
        // Create date spinner
>>>>>>> uilogin
        SpinnerDateModel dateModel = new SpinnerDateModel();
        dateHiredSpinner = new JSpinner(dateModel);
        JSpinner.DateEditor dateEditor = new JSpinner.DateEditor(dateHiredSpinner, "MM/dd/yyyy");
        dateHiredSpinner.setEditor(dateEditor);
<<<<<<< HEAD

        emailField = new JTextField(20);
        addressField = new JTextField(20);
        cellphoneField = new JTextField(20);

=======
        
        emailField = new JTextField(20);
        addressField = new JTextField(20);
        cellphoneField = new JTextField(20);
        
>>>>>>> uilogin
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
<<<<<<< HEAD

        // Salary Panel
        JPanel salaryPanel = new JPanel(new GridLayout(0, 2, 10, 10));
        salaryPanel.setBorder(BorderFactory.createTitledBorder("SALARY"));

=======
        
        // Salary Section
        JPanel salaryPanel = new JPanel(new GridLayout(0, 2, 10, 10));
        salaryPanel.setBorder(BorderFactory.createTitledBorder("SALARY"));
        
>>>>>>> uilogin
        positionField = new JTextField(20);
        basicPayField = new JTextField(20);
        execAllowanceField = new JTextField(20);
        marketingAllowanceField = new JTextField(20);
        monthlySalaryField = new JTextField(20);
<<<<<<< HEAD

=======
        
>>>>>>> uilogin
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
<<<<<<< HEAD

        // Benefits Panel
        JPanel benefitsPanel = new JPanel(new GridLayout(0, 2, 10, 10));
        benefitsPanel.setBorder(BorderFactory.createTitledBorder("BENEFITS"));

=======
        
        // Benefits Section
        JPanel benefitsPanel = new JPanel(new GridLayout(0, 2, 10, 10));
        benefitsPanel.setBorder(BorderFactory.createTitledBorder("BENEFITS"));
        
>>>>>>> uilogin
        sssField = new JTextField(20);
        philHealthField = new JTextField(20);
        pagIbigField = new JTextField(20);
        tinField = new JTextField(20);
        bankAccountField = new JTextField(20);
<<<<<<< HEAD

=======
        
>>>>>>> uilogin
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
<<<<<<< HEAD

        // Submit Button
        JButton submitButton = new JButton("SUBMIT");
        submitButton.setAlignmentX(Component.CENTER_ALIGNMENT);
=======
        
        // Submit Button
        JButton submitButton = new JButton("SUBMIT");
        submitButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        
>>>>>>> uilogin
        submitButton.addActionListener(e -> {
            if (validateForm()) {
                saveEmployee();
                dispose();
            }
        });
<<<<<<< HEAD

=======
        
        // Add all panels to main panel
>>>>>>> uilogin
        mainPanel.add(profilePanel);
        mainPanel.add(Box.createVerticalStrut(20));
        mainPanel.add(salaryPanel);
        mainPanel.add(Box.createVerticalStrut(20));
        mainPanel.add(benefitsPanel);
        mainPanel.add(Box.createVerticalStrut(20));
        mainPanel.add(submitButton);
<<<<<<< HEAD

        if (isEditing) populateFields();

        add(new JScrollPane(mainPanel));
    }

=======
        
        // If editing, populate fields
        if (isEditing) {
            populateFields();
        }
        
        // Add main panel to frame
        add(new JScrollPane(mainPanel));
    }
    
>>>>>>> uilogin
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
<<<<<<< HEAD

    private boolean validateForm() {
        if (firstNameField.getText().trim().isEmpty() ||
            lastNameField.getText().trim().isEmpty() ||
            idNumberField.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please fill in all required fields!", "Validation Error", JOptionPane.ERROR_MESSAGE);
=======
    
    private boolean validateForm() {
        // Add validation logic here
        if (firstNameField.getText().trim().isEmpty() ||
            lastNameField.getText().trim().isEmpty() ||
            idNumberField.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this,
                "Please fill in all required fields!",
                "Validation Error",
                JOptionPane.ERROR_MESSAGE);
>>>>>>> uilogin
            return false;
        }
        return true;
    }
<<<<<<< HEAD

    private void saveEmployee() {
    if (employee == null) employee = new Employee();

    employee.setFirstName(firstNameField.getText().trim());
    employee.setLastName(lastNameField.getText().trim());
    employee.setMiddleName(middleNameField.getText().trim());
    employee.setIdNumber(idNumberField.getText().trim());
    employee.setDateHired((Date) dateHiredSpinner.getValue());
    employee.setEmailAddress(emailField.getText().trim());
    employee.setCurrentAddress(addressField.getText().trim());
    employee.setCellphoneNo(cellphoneField.getText().trim());
    employee.setPosition(positionField.getText().trim());
    employee.setBasicPay(Double.parseDouble(basicPayField.getText().trim()));
    employee.setExecutiveAllowance(Double.parseDouble(execAllowanceField.getText().trim()));
    employee.setMarketingTranspoAllowance(Double.parseDouble(marketingAllowanceField.getText().trim()));
    employee.setMonthlySalary(Double.parseDouble(monthlySalaryField.getText().trim()));
    employee.setSssNumber(sssField.getText().trim());
    employee.setPhilHealthNumber(philHealthField.getText().trim());
    employee.setPagIbigNumber(pagIbigField.getText().trim());
    employee.setTinNumber(tinField.getText().trim());
    employee.setBankAccount(bankAccountField.getText().trim());

    try (Connection conn = DBUtil.getConnection()) {
        String sql;

        if (isEditing) {
            // UPDATE existing employee
            sql = "UPDATE employees SET first_name=?, last_name=?, middle_name=?, id_number=?, date_hired=?, email=?, address=?, cellphone=?, position=?, basic_pay=?, exec_allowance=?, marketing_allowance=?, monthly_salary=?, sss=?, philhealth=?, pagibig=?, tin=?, bank_account=? WHERE id=?";
        } else {
            // INSERT new employee
            sql = "INSERT INTO employees (first_name, last_name, middle_name, id_number, date_hired, email, address, cellphone, position, basic_pay, exec_allowance, marketing_allowance, monthly_salary, sss, philhealth, pagibig, tin, bank_account) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        }

        PreparedStatement stmt = conn.prepareStatement(sql);

        // Common parameter bindings
        stmt.setString(1, employee.getFirstName());
        stmt.setString(2, employee.getLastName());
        stmt.setString(3, employee.getMiddleName());
        stmt.setString(4, employee.getIdNumber());
        stmt.setDate(5, new java.sql.Date(employee.getDateHired().getTime()));
        stmt.setString(6, employee.getEmailAddress());
        stmt.setString(7, employee.getCurrentAddress());
        stmt.setString(8, employee.getCellphoneNo());
        stmt.setString(9, employee.getPosition());
        stmt.setDouble(10, employee.getBasicPay());
        stmt.setDouble(11, employee.getExecutiveAllowance());
        stmt.setDouble(12, employee.getMarketingTranspoAllowance());
        stmt.setDouble(13, employee.getMonthlySalary());
        stmt.setString(14, employee.getSssNumber());
        stmt.setString(15, employee.getPhilHealthNumber());
        stmt.setString(16, employee.getPagIbigNumber());
        stmt.setString(17, employee.getTinNumber());
        stmt.setString(18, employee.getBankAccount());

        if (isEditing) {
            stmt.setInt(19, employee.getId()); // use the employee's ID for update
        }

        stmt.executeUpdate();

        JOptionPane.showMessageDialog(this,
                isEditing ? "Employee updated successfully!" : "Employee saved successfully!");

    } catch (SQLException ex) {
        ex.printStackTrace();
        JOptionPane.showMessageDialog(this,
                "Error saving employee: " + ex.getMessage(),
                "Database Error", JOptionPane.ERROR_MESSAGE);
    }

    if (isEditing) {
        homePage.refreshEmployeeList(); // reload all employees from DB
    } else {
        homePage.addEmployee(employee); // add only if it's a new one
    }
    }
}
=======
    
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
>>>>>>> uilogin
