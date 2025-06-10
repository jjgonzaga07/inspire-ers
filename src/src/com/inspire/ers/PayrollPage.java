package com.inspire.ers;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Calendar;
import javax.swing.table.DefaultTableCellRenderer;

public class PayrollPage extends JFrame {
    private String employeeName;
    private DefaultTableModel tableModel;
    private JTable attendanceTable;
    private JLabel totalLabel;
    private double totalAmount = 0;
    
    public PayrollPage(String employeeName) {
        this.employeeName = employeeName;
        
        setTitle("Payroll - " + employeeName);
        setSize(800, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        
        // Main panel
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        // Employee name label
        JLabel nameLabel = new JLabel("EMPLOYEE NAME: " + employeeName);
        nameLabel.setFont(new Font("Arial", Font.BOLD, 16));
        mainPanel.add(nameLabel, BorderLayout.NORTH);
        
        // Table
        String[] columns = {"DATE", "TIME-IN", "TIME-OUT", "LATE", "Paid Amount"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        attendanceTable = new JTable(tableModel);
        attendanceTable.setFillsViewportHeight(true);
        
        // Center align all columns
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        for (int i = 0; i < attendanceTable.getColumnCount(); i++) {
            attendanceTable.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
        }
        
        JScrollPane scrollPane = new JScrollPane(attendanceTable);
        mainPanel.add(scrollPane, BorderLayout.CENTER);
        
        // Bottom panel
        JPanel bottomPanel = new JPanel(new BorderLayout());
        
        // Add button
        JButton addButton = new JButton("ADD");
        bottomPanel.add(addButton, BorderLayout.WEST);
        
        // Total amount
        totalLabel = new JLabel("total: " + String.format("%.2f", totalAmount));
        totalLabel.setHorizontalAlignment(JLabel.RIGHT);
        bottomPanel.add(totalLabel, BorderLayout.EAST);
        
        mainPanel.add(bottomPanel, BorderLayout.SOUTH);
        
        // Add button action
        addButton.addActionListener(e -> showAddAttendanceDialog());
        
        add(mainPanel);
    }
    
    private void showAddAttendanceDialog() {
        JDialog dialog = new JDialog(this, "Add Attendance", true);
        dialog.setSize(400, 300);
        dialog.setLocationRelativeTo(this);
        
        JPanel panel = new JPanel(new GridLayout(0, 2, 10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        // Date
        SpinnerDateModel dateModel = new SpinnerDateModel();
        JSpinner dateSpinner = new JSpinner(dateModel);
        JSpinner.DateEditor dateEditor = new JSpinner.DateEditor(dateSpinner, "MM/dd/yyyy");
        dateSpinner.setEditor(dateEditor);
        
        panel.add(new JLabel("DATE"));
        panel.add(dateSpinner);
        
        // Time in
        JSpinner timeInSpinner = new JSpinner(new SpinnerDateModel());
        JSpinner.DateEditor timeInEditor = new JSpinner.DateEditor(timeInSpinner, "HH:mm:ss a");
        timeInSpinner.setEditor(timeInEditor);
        panel.add(new JLabel("TIME-IN"));
        panel.add(timeInSpinner);
        
        // Time out
        JSpinner timeOutSpinner = new JSpinner(new SpinnerDateModel());
        JSpinner.DateEditor timeOutEditor = new JSpinner.DateEditor(timeOutSpinner, "HH:mm:ss a");
        timeOutSpinner.setEditor(timeOutEditor);
        panel.add(new JLabel("TIME-OUT"));
        panel.add(timeOutSpinner);
        
        // Submit button
        JButton submitButton = new JButton("SUBMIT");
        submitButton.addActionListener(e -> {
            SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy");
            SimpleDateFormat timeFormat = new SimpleDateFormat("hh:mm:ss a");
            
            Date timeIn = (Date) timeInSpinner.getValue();
            int lateMinutes = calculateLateMinutes(timeIn);
            
            // Calculate paid amount (example calculation)
            double paidAmount = 850.0; // Base amount
            if (lateMinutes > 0) {
                // You can implement your own late deduction logic here
                // paidAmount = paidAmount - (lateMinutes * deductionRate);
            }
            
            Object[] rowData = {
                dateFormat.format(dateSpinner.getValue()),
                timeFormat.format(timeInSpinner.getValue()),
                timeFormat.format(timeOutSpinner.getValue()),
                lateMinutes,
                String.format("%.2f", paidAmount)
            };
            
            tableModel.addRow(rowData);
            totalAmount += paidAmount;
            totalLabel.setText("total: " + String.format("%.2f", totalAmount));
            
            dialog.dispose();
        });
        
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        buttonPanel.add(submitButton);
        
        dialog.setLayout(new BorderLayout());
        dialog.add(panel, BorderLayout.CENTER);
        dialog.add(buttonPanel, BorderLayout.SOUTH);
        
        dialog.setVisible(true);
    }
    
    private int calculateLateMinutes(Date timeIn) {
        // Set the reference time (9:30 AM)
        Calendar reference = Calendar.getInstance();
        reference.setTime(timeIn);
        reference.set(Calendar.HOUR_OF_DAY, 9);
        reference.set(Calendar.MINUTE, 30);
        reference.set(Calendar.SECOND, 0);
        
        // Calculate the difference in minutes
        long diff = timeIn.getTime() - reference.getTime().getTime();
        int minutes = (int) (diff / (60 * 1000));
        
        return Math.max(0, minutes); // Return 0 if not late
    }
} 