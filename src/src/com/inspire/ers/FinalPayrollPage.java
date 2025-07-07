package com.inspire.ers;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.List;

public class FinalPayrollPage extends JFrame {

    private JTable table;
    private DefaultTableModel model;

    public FinalPayrollPage() {
        setTitle("Final Payroll Page");
        setSize(1200, 600);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // === Header Panel ===
        JPanel headerPanel = new JPanel(new GridLayout(2, 1));
        headerPanel.setBackground(Color.WHITE);

        JLabel companyLabel = new JLabel("Inspire Next Global Solutions Inc.", SwingConstants.CENTER);
        companyLabel.setFont(new Font("Serif", Font.BOLD, 20));

        JLabel titleLabel = new JLabel("EMPLOYEE PAYROLL SUMMARY", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Serif", Font.PLAIN, 16));

        headerPanel.add(companyLabel);
        headerPanel.add(titleLabel);
        add(headerPanel, BorderLayout.NORTH);

        // === Table Setup ===
        String[] columns = {
            "ID NUMBER", "EMPLOYEE NAME", "DEPARTMENT/POSITION", "BANK",
            "BASIC PAY", "ALLOWANCE", "REFRESHMENT", "MINS",
            "TOTAL LATE", "ABSENT", " ", "HALF DAY",
            " ", "TOTAL ABSENT", "OT HOURS", "OT PAY",
            "NET PAY", "NUMBER OF DAYS", "DAILY",
            "PER HOUR", "PER MINUTE",
            "PAY DATE", "CUTOFF START", "CUTOFF END"
        };

        model = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return true;
            }
        };

        table = new JTable(model);
        table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        int[] columnWidths = {
            100, 150, 180, 100,
            100, 100, 100, 60,
            100, 80, 40, 80,
            40, 100, 80, 80,
            100, 130, 80,
            90, 100,
            100, 100, 100
        };

        for (int i = 0; i < columnWidths.length; i++) {
            TableColumn column = table.getColumnModel().getColumn(i);
            column.setPreferredWidth(columnWidths[i]);
        }

        JScrollPane scrollPane = new JScrollPane(table,
            JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
            JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        add(scrollPane, BorderLayout.CENTER);

        // === Month Filter Dropdown ===
        String[] months = {
            "Add New Payroll Entry", "01 - January", "02 - February", "03 - March", "04 - April",
            "05 - May", "06 - June", "07 - July", "08 - August", "09 - September",
            "10 - October", "11 - November", "12 - December"
        };

        JComboBox<String> monthCombo = new JComboBox<>(months);
        JPanel topFilterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        topFilterPanel.add(new JLabel("Filter by Month:"));
        topFilterPanel.add(monthCombo);
        add(topFilterPanel, BorderLayout.BEFORE_FIRST_LINE);

        monthCombo.addActionListener(e -> {
            int selectedIndex = monthCombo.getSelectedIndex();
            if (selectedIndex == 0) {
                openAddPayrollDialog();
            } else {
                String selectedMonth = String.format("%02d", selectedIndex);
                refreshTable(selectedMonth);
            }
        });

        refreshTable("");

        JButton saveButton = new JButton("Save Changes");
        saveButton.addActionListener(this::handleSaveChanges);
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        bottomPanel.add(saveButton);
        add(bottomPanel, BorderLayout.SOUTH);
    }

    private void openAddPayrollDialog() {
        JTextField payDateField = new JTextField("2025-08-31");
        JTextField cutoffStart = new JTextField();
        JTextField cutoffEnd = new JTextField();

        JPanel panel = new JPanel(new GridLayout(0, 1));
        panel.add(new JLabel("Pay Date (YYYY-MM-DD):"));
        panel.add(payDateField);
        panel.add(new JLabel("Cutoff Start:"));
        panel.add(cutoffStart);
        panel.add(new JLabel("Cutoff End:"));
        panel.add(cutoffEnd);

        int result = JOptionPane.showConfirmDialog(this, panel, "Add Payroll for ALL Employees", JOptionPane.OK_CANCEL_OPTION);
        if (result == JOptionPane.OK_OPTION) {
            String payDate = payDateField.getText().trim();
            String cutoffStartStr = cutoffStart.getText().trim();
            String cutoffEndStr = cutoffEnd.getText().trim();

            if (payDate.isEmpty() || cutoffStartStr.isEmpty() || cutoffEndStr.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please fill in all fields.");
                return;
            }

            List<String[]> employees = EmployeeDataFetcher.fetchAllEmployeesOnly();

            for (String[] emp : employees) {
                String[] newRow = new String[24];
                newRow[0] = emp[0]; // ID
                newRow[1] = emp[1]; // Name
                newRow[2] = emp[2]; // Dept/Position
                newRow[3] = emp[3]; // Bank
                newRow[4] = emp[4]; // Basic Pay
                newRow[5] = emp[5]; // Allowance
                newRow[6] = "0";
                newRow[7] = "0";
                newRow[8] = "0";
                newRow[9] = "0";
                newRow[10] = "";
                newRow[11] = "0";
                newRow[12] = "";
                newRow[13] = "0";
                newRow[14] = "0";
                newRow[15] = "0";
                newRow[16] = emp[6]; // Monthly Salary
                newRow[17] = "0";
                newRow[18] = "0";
                newRow[19] = "0";
                newRow[20] = "0";
                newRow[21] = payDate;
                newRow[22] = cutoffStartStr;
                newRow[23] = cutoffEndStr;

                model.addRow(newRow);
                boolean inserted = EmployeeDataUpdater.insertPayrollOnly(newRow);
                if (!inserted) {
                    System.err.println("Payroll insert failed for: " + newRow[0]);
                }
            }

            JOptionPane.showMessageDialog(this, "Payroll entries for " + payDate + " added.");
        }
    }

    private void refreshTable(String monthFilter) {
        model.setRowCount(0);
        List<String[]> employeeData = EmployeeDataFetcher.fetchEmployeeData(monthFilter);
        for (String[] row : employeeData) {
            model.addRow(row);
        }
    }

    private void handleSaveChanges(ActionEvent e) {
        int selectedRow = table.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a row to edit.");
            return;
        }

        if (table.isEditing()) {
            table.getCellEditor().stopCellEditing();
        }

        String[] updatedData = new String[table.getColumnCount()];
        for (int i = 0; i < updatedData.length; i++) {
            Object value = table.getValueAt(selectedRow, i);
            updatedData[i] = (value != null) ? value.toString().trim() : "";
        }

        boolean success = EmployeeDataUpdater.updateEmployeeData(updatedData);
        if (success) {
            JOptionPane.showMessageDialog(this, "Update successful.");
        } else {
            JOptionPane.showMessageDialog(this, "Update failed.");
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            FinalPayrollPage finalPayrollPage = new FinalPayrollPage();
            finalPayrollPage.setVisible(true);
        });
    }
}
