package com.inspire.ers;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import java.awt.*;
import java.awt.event.*;
import java.awt.Font;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Calendar;
import javax.swing.table.DefaultTableCellRenderer;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import src.com.inspire.ers.DBUtil;

import javax.swing.JFileChooser;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.io.File;
import java.io.FileWriter;


import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.text.ParseException;
import java.util.Locale;


public class PayrollPage extends JFrame {
    private String employeeName;
    private String idNumber;
    
    private DefaultTableModel tableModel;
    private JTable attendanceTable;
//    private JLabel totalLabel;
    private double totalAmount = 0;
    private JLabel totalLabel = new JLabel("Base Total: ₱0.00");
private JLabel overtimeLabel = new JLabel("Total Overtime: 0 minutes");
private JLabel finalTotalLabel = new JLabel("Final Total: ₱0.00");


    // Constants
//    private static final double BASE_SALARY = 818.18; // Salary for 8 hrs
    private Double cachedBaseSalary = null;

    private static final double LATE_PENALTY_PER_MINUTE = 1.71;

    public PayrollPage(String employeeName, String idNumber) {
        this.employeeName = employeeName;
        this.idNumber = idNumber;

        setTitle("Payroll - Inspire");
        setSize(800, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        
        totalLabel = new JLabel("Total: ");
//        add(totalLabel, BorderLayout.SOUTH); //


        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel nameLabel = new JLabel(employeeName, SwingConstants.CENTER);
        nameLabel.setFont(new Font("SansSerif", Font.BOLD, 32));

        JPanel namePanel = new JPanel(new BorderLayout());
        namePanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));
        namePanel.add(nameLabel, BorderLayout.CENTER);

        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        String[] months = new java.text.DateFormatSymbols().getMonths();
        JComboBox<String> monthComboBox = new JComboBox<>();
        for (int i = 0; i < 12; i++) monthComboBox.addItem(months[i]);

        int currentYear = Calendar.getInstance().get(Calendar.YEAR);
        JComboBox<Integer> yearComboBox = new JComboBox<>();
        for (int i = currentYear - 5; i <= currentYear + 5; i++) yearComboBox.addItem(i);

        monthComboBox.setSelectedIndex(Calendar.getInstance().get(Calendar.MONTH));
        yearComboBox.setSelectedItem(currentYear);

        filterPanel.add(new JLabel("Month:"));
        filterPanel.add(monthComboBox);
        filterPanel.add(new JLabel("Year:"));
        filterPanel.add(yearComboBox);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton importButton = new JButton("IMPORT");
        JButton exportButton = new JButton("EXPORT");
        buttonPanel.add(importButton);
        buttonPanel.add(exportButton);

        JPanel controlsPanel = new JPanel(new BorderLayout());
        controlsPanel.add(filterPanel, BorderLayout.WEST);
        controlsPanel.add(buttonPanel, BorderLayout.EAST);

        JPanel topContainer = new JPanel();
        topContainer.setLayout(new BoxLayout(topContainer, BoxLayout.Y_AXIS));
        topContainer.add(namePanel);
        topContainer.add(controlsPanel);

        mainPanel.add(topContainer, BorderLayout.NORTH);
//  String[] columns = {"ID", "DATE", "TIME-IN", "TIME-OUT", "LATE (mins)", "Overtime (min)", "Paid Amount", "Remarks"};
        String[] columns = {"ID", "DATE", "TIME-IN", "TIME-OUT","Overtime (min)", "Paid Amount"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        attendanceTable = new JTable(tableModel);
        loadDataFromDatabase(null, null);
        attendanceTable.setFillsViewportHeight(true);
        attendanceTable.getColumnModel().getColumn(0).setMinWidth(0);
        attendanceTable.getColumnModel().getColumn(0).setMaxWidth(0);
        attendanceTable.getColumnModel().getColumn(0).setWidth(0);


        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        for (int i = 0; i < attendanceTable.getColumnCount(); i++) {
            attendanceTable.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
        }

        JScrollPane scrollPane = new JScrollPane(attendanceTable);
        mainPanel.add(scrollPane, BorderLayout.CENTER);

        //        JPanel bottomPanel = new JPanel(new BorderLayout());

        //        JButton addButton = new JButton("ADD");
        //        bottomPanel.add(addButton, BorderLayout.WEST);
        //        
        //        JButton exportButton = new JButton("EXPORT");
        //        bottomPanel.add(exportButton, BorderLayout.CENTER);
        //        
        //        JButton importButton = new JButton("IMPORT");
        //bottomPanel.add(importButton, BorderLayout.WEST);

                importButton.addActionListener(e -> importFile());
                exportButton.addActionListener(e -> exportToCSV());

        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.add(totalLabel);
        bottomPanel.add(overtimeLabel);
        bottomPanel.add(finalTotalLabel);
        add(bottomPanel, BorderLayout.SOUTH);

        // Create a left-aligned panel for both buttons
        JPanel westButtons = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        JButton addButton = new JButton("ADD");
        JButton editButton = new JButton("EDIT");
        JButton removeButton = new JButton("REMOVE");
        westButtons.add(addButton);
        westButtons.add(editButton);
        westButtons.add(removeButton);

        bottomPanel.add(westButtons, BorderLayout.WEST);

        totalLabel = new JLabel("Total: ₱" + String.format("%.2f", totalAmount));
        totalLabel.setHorizontalAlignment(SwingConstants.RIGHT);
        bottomPanel.add(totalLabel, BorderLayout.EAST);


        mainPanel.add(bottomPanel, BorderLayout.SOUTH);

        addButton.addActionListener(e -> showAddAttendanceDialog());
        editButton.addActionListener(e -> showEditDialog());
        removeButton.addActionListener(e -> removeSelectedRow());

        add(mainPanel);
        
        ActionListener filterAction = e -> {
            int selectedMonthIndex = monthComboBox.getSelectedIndex();
            int selectedMonth = selectedMonthIndex + 1;
            int selectedYear = (int) yearComboBox.getSelectedItem();

            loadDataFromDatabase(selectedMonth, selectedYear);
        };

        monthComboBox.addActionListener(filterAction);
        yearComboBox.addActionListener(filterAction);
    }
    
        private void exportToCSV() {
         JFileChooser fileChooser = new JFileChooser();
         fileChooser.setDialogTitle("Save as");
         fileChooser.setFileFilter(new FileNameExtensionFilter("CSV files", "csv"));

         int userSelection = fileChooser.showSaveDialog(this);
         if (userSelection == JFileChooser.APPROVE_OPTION) {
             File fileToSave = fileChooser.getSelectedFile();

             // Ensure .csv extension
             String filePath = fileToSave.getAbsolutePath();
             if (!filePath.toLowerCase().endsWith(".csv")) {
                 filePath += ".csv";
                 fileToSave = new File(filePath);
             }

             try (FileWriter fw = new FileWriter(fileToSave)) {
                 TableModel model = attendanceTable.getModel();

                 // Write headers
                 for (int i = 0; i < model.getColumnCount(); i++) {
                     fw.write("\"" + model.getColumnName(i) + "\"");
                     if (i != model.getColumnCount() - 1) fw.write(",");
                 }
                 fw.write("\n");

                 // Write data rows
                 for (int row = 0; row < model.getRowCount(); row++) {
                     for (int col = 0; col < model.getColumnCount(); col++) {
                         Object value = model.getValueAt(row, col);
                    String cellText = value != null ? value.toString().replace("\"", "\"\"") : "";

                    // Remove peso sign from Amount Paid column
                    String columnName = model.getColumnName(col).toLowerCase();
                    if (columnName.contains("amount") || columnName.contains("paid")) {
                        cellText = cellText.replace("₱", "").trim();
                    }

                    // ✅ Special formatting for Excel for date/time columns
                    if (columnName.contains("date") || columnName.contains("time")) {
                        cellText = "\t" + cellText;
                    }

                    fw.write("\"" + cellText + "\"");
                    if (col != model.getColumnCount() - 1) fw.write(",");
                }
                fw.write("\n");
            }
            

            fw.flush();
            JOptionPane.showMessageDialog(this, "CSV file exported successfully:\n" + fileToSave.getAbsolutePath());
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error exporting CSV: " + ex.getMessage());
        }
    }
}
   
   private void importFile() {
    JFileChooser fileChooser = new JFileChooser();
    fileChooser.setDialogTitle("Import File");
    fileChooser.setFileFilter(new FileNameExtensionFilter("Excel or CSV files", "xlsx", "csv"));

    int userSelection = fileChooser.showOpenDialog(this);
    if (userSelection == JFileChooser.APPROVE_OPTION) {
        File selectedFile = fileChooser.getSelectedFile();
        String fileName = selectedFile.getName().toLowerCase();

        try {
            if (fileName.endsWith(".xlsx")) {
                importFromExcel(selectedFile);
            } else if (fileName.endsWith(".csv")) {
                importFromCSV(selectedFile);
            } else {
                JOptionPane.showMessageDialog(this, "Unsupported file type.");
            }

            // Reload table data after import
            tableModel.setRowCount(0);
            totalAmount = 0;
            loadDataFromDatabase(null, null);

        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Import Error: " + e.getMessage());
        }
    }
}
   
   private void importFromCSV(File file) throws IOException, ParseException, SQLException {
    BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8));
    String line;
    boolean firstLine = true;

    SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy");
    SimpleDateFormat timeFormat = new SimpleDateFormat("hh:mm:ss a", Locale.ENGLISH);

    while ((line = reader.readLine()) != null) {
        if (firstLine) {
            firstLine = false; // Skip header
            continue;
        }

        String[] data = line.split(",");

        if (data.length < 6) continue;

        String dateStr = data[0].replace("\"", "").trim();
        String timeInStr = data[1].replace("\"", "").trim();
        String timeOutStr = data[2].replace("\"", "").trim();
        int lateMinutes = Integer.parseInt(data[3].replace("\"", "").trim());
        int overtimeMinutes = Integer.parseInt(data[4].replace("\"", "").trim());
        double paidAmount = Double.parseDouble(data[4].replace("\"₱", "").replace("\"", "").trim());
        String remarks = data[5].replace("\"", "").trim();

        Date date = dateFormat.parse(dateStr);
        Date timeIn = timeFormat.parse(timeInStr);
        Date timeOut = timeFormat.parse(timeOutStr);

        saveToDatabase(date, timeIn, timeOut, lateMinutes, overtimeMinutes, paidAmount, remarks);

    }

    reader.close();
}

   private void importFromExcel(File file) throws IOException, ParseException, SQLException {
    FileInputStream fis = new FileInputStream(file);
    Workbook workbook = new XSSFWorkbook(fis);
    Sheet sheet = workbook.getSheetAt(0);

    SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy");
    SimpleDateFormat timeFormat = new SimpleDateFormat("hh:mm:ss a", Locale.ENGLISH);

    for (int i = 1; i <= sheet.getLastRowNum(); i++) { // Skip header
        Row row = sheet.getRow(i);
        if (row == null) continue;

        String dateStr = row.getCell(0).toString().trim();
        String timeInStr = row.getCell(1).toString().trim();
        String timeOutStr = row.getCell(2).toString().trim();
        int lateMinutes = (int) row.getCell(3).getNumericCellValue();
        int overtimeMinutes = (int) row.getCell(4).getNumericCellValue();
        double paidAmount = row.getCell(4).getNumericCellValue();
        String remarks = row.getCell(5).toString().trim();

        Date date = dateFormat.parse(dateStr);
        Date timeIn = timeFormat.parse(timeInStr);
        Date timeOut = timeFormat.parse(timeOutStr);

       saveToDatabase(date, timeIn, timeOut, lateMinutes, overtimeMinutes, paidAmount, remarks);

    }

    workbook.close();
    fis.close();
}


    private void showAddAttendanceDialog() {
        JDialog dialog = new JDialog(this, "Add Attendance", true);
        dialog.setSize(400, 300);
        dialog.setLocationRelativeTo(this);

        JPanel panel = new JPanel(new GridLayout(0, 2, 10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        SpinnerDateModel dateModel = new SpinnerDateModel();
        JSpinner dateSpinner = new JSpinner(dateModel);
        JSpinner.DateEditor dateEditor = new JSpinner.DateEditor(dateSpinner, "MM/dd/yyyy");
        dateSpinner.setEditor(dateEditor);

        panel.add(new JLabel("DATE"));
        panel.add(dateSpinner);

        JSpinner timeInSpinner = new JSpinner(new SpinnerDateModel());
        JSpinner.DateEditor timeInEditor = new JSpinner.DateEditor(timeInSpinner, "hh:mm:ss a");
        timeInSpinner.setEditor(timeInEditor);
        panel.add(new JLabel("TIME-IN"));
        panel.add(timeInSpinner);

        JSpinner timeOutSpinner = new JSpinner(new SpinnerDateModel());
        JSpinner.DateEditor timeOutEditor = new JSpinner.DateEditor(timeOutSpinner, "hh:mm:ss a");
        timeOutSpinner.setEditor(timeOutEditor);
        panel.add(new JLabel("TIME-OUT"));
        panel.add(timeOutSpinner);

        JButton submitButton = new JButton("SUBMIT");
        submitButton.addActionListener(e -> {
            SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy");
            SimpleDateFormat timeFormat = new SimpleDateFormat("hh:mm:ss a");

            Date timeIn = (Date) timeInSpinner.getValue();
              Date timeOut = (Date) timeOutSpinner.getValue();
           
              
              int lateMinutes = calculateLateMinutes(timeIn);              
             int overtimeMinutes = calculateOvertimeMinutes(timeIn, timeOut); // 🔧 NEW
         


            double baseSalary = getBaseSalaryFromDB(); // dynamic rate per minute
            double deduction = lateMinutes * LATE_PENALTY_PER_MINUTE;
            double paidAmount = (baseSalary * 480) - deduction;

            String remarks = getRemarks(timeIn, timeOut);
            
            Object[] rowData = {
                null, // placeholder for hidden ID (will be refreshed later)
                dateFormat.format(dateSpinner.getValue()),
                timeFormat.format(timeIn),
                timeFormat.format(timeOut),
                lateMinutes,
                 overtimeMinutes,
                String.format("₱%.2f", paidAmount),
                remarks
         };

            tableModel.addRow(rowData);
//            totalAmount += paidAmount;
//            totalLabel.setText("Total: ₱" + String.format("%.2f", totalAmount));
  Date selectedDate = (Date) dateSpinner.getValue(); // retrieve date from spinner
            // 🔧 Save to database
        saveToDatabase(selectedDate, timeIn, timeOut, lateMinutes, overtimeMinutes, paidAmount, remarks);


            dialog.dispose();

                    });
        
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        buttonPanel.add(submitButton);

        dialog.setLayout(new BorderLayout());
        dialog.add(panel, BorderLayout.CENTER);
        dialog.add(buttonPanel, BorderLayout.SOUTH);

        dialog.setVisible(true);
    }
    

private int calculateOvertimeMinutes(Date timeIn, Date timeOut) {
    Calendar inCal = Calendar.getInstance();
    inCal.setTime(timeIn);

    Calendar outCal = Calendar.getInstance();
    outCal.setTime(timeOut);

    long millisWorked = outCal.getTimeInMillis() - inCal.getTimeInMillis();
    int totalMinutesWorked = (int) (millisWorked / (1000 * 60));

    int requiredMinutes = 9 * 60; // 9 hours = 540 minutes
    int overtimeMinutes = totalMinutesWorked - requiredMinutes;

    return Math.max(0, overtimeMinutes); // Only return positive overtime
}



    
    private void showEditDialog() {
    int selectedRow = attendanceTable.getSelectedRow();
    int selectedId = (int) tableModel.getValueAt(selectedRow, 0); // hidden id

    if (selectedRow == -1) {
        JOptionPane.showMessageDialog(this, "Please select a row to edit.");
        return;
    }

    JDialog dialog = new JDialog(this, "Edit Attendance", true);
    dialog.setSize(400, 300);
    dialog.setLocationRelativeTo(this);

    JPanel panel = new JPanel(new GridLayout(0, 2, 10, 10));
    panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

    // Load selected values
    String dateStr = tableModel.getValueAt(selectedRow, 0).toString();
    String timeInStr = tableModel.getValueAt(selectedRow, 1).toString();
    String timeOutStr = tableModel.getValueAt(selectedRow, 2).toString();

    SpinnerDateModel dateModel = new SpinnerDateModel();
    JSpinner dateSpinner = new JSpinner(dateModel);
    JSpinner.DateEditor dateEditor = new JSpinner.DateEditor(dateSpinner, "MM/dd/yyyy");
    dateSpinner.setEditor(dateEditor);

    SpinnerDateModel inModel = new SpinnerDateModel();
    JSpinner timeInSpinner = new JSpinner(inModel);
    timeInSpinner.setEditor(new JSpinner.DateEditor(timeInSpinner, "hh:mm:ss a"));

    SpinnerDateModel outModel = new SpinnerDateModel();
    JSpinner timeOutSpinner = new JSpinner(outModel);
    timeOutSpinner.setEditor(new JSpinner.DateEditor(timeOutSpinner, "hh:mm:ss a"));

    try {
        SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy");
        SimpleDateFormat timeFormat = new SimpleDateFormat("hh:mm:ss a");

        dateSpinner.setValue(dateFormat.parse(dateStr));
        timeInSpinner.setValue(timeFormat.parse(timeInStr));
        timeOutSpinner.setValue(timeFormat.parse(timeOutStr));
    } catch (Exception ex) {
        ex.printStackTrace();
    }

    panel.add(new JLabel("DATE"));
    panel.add(dateSpinner);
    panel.add(new JLabel("TIME-IN"));
    panel.add(timeInSpinner);
    panel.add(new JLabel("TIME-OUT"));
    panel.add(timeOutSpinner);

    JButton submitButton = new JButton("SAVE");
    submitButton.addActionListener(e -> {
        try {
            Date date = (Date) dateSpinner.getValue();
            Date timeIn = (Date) timeInSpinner.getValue();
            Date timeOut = (Date) timeOutSpinner.getValue();

        long workedMillis = timeOut.getTime() - timeIn.getTime();
        int totalMinutesWorked = (int) (workedMillis / (60 * 1000));
        int overtimeMinutes = Math.max(totalMinutesWorked - 540, 0); // 540 = 9 hours
        double perMinute = getBaseSalaryFromDB();
        double paidAmount = perMinute * totalMinutesWorked;



            String remarks = getRemarks(timeIn, timeOut);

            // Update JTable
            SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy");
            SimpleDateFormat timeFormat = new SimpleDateFormat("hh:mm:ss a");

            tableModel.setValueAt(dateFormat.format(date), selectedRow, 0);
            tableModel.setValueAt(timeFormat.format(timeIn), selectedRow, 1);
            tableModel.setValueAt(timeFormat.format(timeOut), selectedRow, 2);
//            tableModel.setValueAt(lateMinutes, selectedRow, 3);
            tableModel.setValueAt(formatMinutesToHHMM(overtimeMinutes), selectedRow, 4); // if this is your OT column

            tableModel.setValueAt(String.format("₱%.2f", paidAmount), selectedRow, 4);
            tableModel.setValueAt(remarks, selectedRow, 5);

            // Update in DB
            updatePayrollInDatabase(selectedId, date, timeIn, timeOut,overtimeMinutes, paidAmount, remarks);

            loadDataFromDatabase(null, null); // refresh
            dialog.dispose();
        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(dialog, "Edit Error: " + ex.getMessage());
        }
    });

    JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
    buttonPanel.add(submitButton);

    dialog.setLayout(new BorderLayout());
    dialog.add(panel, BorderLayout.CENTER);
    dialog.add(buttonPanel, BorderLayout.SOUTH);
    dialog.setVisible(true);
}
    
      private void updatePayrollInDatabase(int id, Date date, Date timeIn, Date timeOut, int lateMinutes, double paidAmount, String remarks) {
    SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm:ss");

    String sql = "UPDATE employee_payroll SET attendance_date = ?, time_in = ?, time_out = ?, late_minutes = ?, paid_amount = ?, remarks = ? WHERE id = ?";

    try (Connection conn = DBUtil.getConnection();
         PreparedStatement stmt = conn.prepareStatement(sql)) {

        stmt.setString(1, new SimpleDateFormat("yyyy-MM-dd").format(date));
        stmt.setString(2, timeFormat.format(timeIn));
        stmt.setString(3, timeFormat.format(timeOut));
        stmt.setInt(4, lateMinutes);
        stmt.setDouble(5, paidAmount);
        stmt.setString(6, remarks);
        stmt.setInt(7, id);

        stmt.executeUpdate();
    } catch (Exception ex) {
        ex.printStackTrace();
        JOptionPane.showMessageDialog(this, "Update Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
    }
}

private void removeSelectedRow() {
    int selectedRow = attendanceTable.getSelectedRow();
    if (selectedRow == -1) {
        JOptionPane.showMessageDialog(this, "Please select a row to remove.");
        return;
    }

    int confirm = JOptionPane.showConfirmDialog(this, "Are you sure you want to delete this row?", "Confirm Deletion", JOptionPane.YES_NO_OPTION);
    if (confirm != JOptionPane.YES_OPTION) return;

    try {
        int selectedId = (int) tableModel.getValueAt(selectedRow, 0); // hidden ID
        deleteFromDatabase(selectedId);
        tableModel.removeRow(selectedRow);
        JOptionPane.showMessageDialog(this, "Row deleted successfully.");
        loadDataFromDatabase(null, null); // refresh table & total
    } catch (Exception ex) {
        ex.printStackTrace();
        JOptionPane.showMessageDialog(this, "Delete Error: " + ex.getMessage());
    }
}

private void deleteFromDatabase(int id) {
    String sql = "DELETE FROM employee_payroll WHERE id = ?";
    try (Connection conn = DBUtil.getConnection();
         PreparedStatement stmt = conn.prepareStatement(sql)) {

        stmt.setInt(1, id);
        stmt.executeUpdate();
    } catch (Exception ex) {
        ex.printStackTrace();
        JOptionPane.showMessageDialog(this, "Database Delete Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
    }
}

private double getBaseSalaryFromDB() {
    if (cachedBaseSalary != null) return cachedBaseSalary;

    String sql = "SELECT basic_pay FROM employees WHERE id = ?";
    try (Connection conn = DBUtil.getConnection();
         PreparedStatement stmt = conn.prepareStatement(sql)) {
        
        stmt.setString(1, idNumber);
        var rs = stmt.executeQuery();
        
        if (rs.next()) {
            double basicPay = rs.getDouble("basic_pay");
            cachedBaseSalary = basicPay / 22.0 / 480.0;
            
            System.out.println("Fetched basic_pay for ID " + idNumber + ": " + basicPay);
            System.out.println("Computed base salary per minute: " + cachedBaseSalary);
            return cachedBaseSalary;
        } else {
            JOptionPane.showMessageDialog(this, "Employee not found.");
        }
    } catch (Exception ex) {
        ex.printStackTrace();
        JOptionPane.showMessageDialog(this, "Error fetching salary: " + ex.getMessage());
    }

    return 818.18;
}

private double fetchExecAllowance() {
    String sql = "SELECT exec_allowance FROM employees WHERE id = ?";
    try (Connection conn = DBUtil.getConnection();
         PreparedStatement stmt = conn.prepareStatement(sql)) {
        
        stmt.setString(1, idNumber);
        var rs = stmt.executeQuery();
        if (rs.next()) {
            double allowance = rs.getDouble("exec_allowance");
            System.out.println("Exec Allowance: " + allowance);
            return allowance;
        }
    } catch (Exception ex) {
        ex.printStackTrace();
        JOptionPane.showMessageDialog(this, "Error fetching exec_allowance: " + ex.getMessage());
    }
    return 0.0;
}



    private int calculateLateMinutes(Date timeIn) {
        Calendar scheduled = Calendar.getInstance();
        scheduled.setTime(timeIn);
        scheduled.set(Calendar.HOUR_OF_DAY, 9);
        scheduled.set(Calendar.MINUTE, 30);
        scheduled.set(Calendar.SECOND, 0);

        long diffMillis = timeIn.getTime() - scheduled.getTimeInMillis();
      int lateMinutes = 0; // No need for penalty


        return Math.max(0, lateMinutes);
    }
    
        private String getRemarks(Date timeIn, Date timeOut) {
        Calendar calIn = Calendar.getInstance();
        calIn.setTime(timeIn);

        Calendar calOut = Calendar.getInstance();
        calOut.setTime(timeOut);

        // Scheduled times
        Calendar scheduledIn = (Calendar) calIn.clone();
        scheduledIn.set(Calendar.HOUR_OF_DAY, 9);
        scheduledIn.set(Calendar.MINUTE, 30);
        scheduledIn.set(Calendar.SECOND, 0);

        Calendar scheduledOut = (Calendar) calOut.clone();
        scheduledOut.set(Calendar.HOUR_OF_DAY, 18); // 6:30 PM is 18:30
        scheduledOut.set(Calendar.MINUTE, 30);
        scheduledOut.set(Calendar.SECOND, 0);

        boolean isLate = timeIn.after(scheduledIn.getTime());
        boolean isEarlyLeave = timeOut.before(scheduledOut.getTime());

//        if (isLate && isEarlyLeave) return "Tardy, Early Leaving";
//        else if (isLate) return "Tardy";
        if (isLate) return "Tardy";
        else if (isEarlyLeave) return "Early Leaving";
        else return "On Time";
     }
    
       private void saveToDatabase(Date date, Date timeIn, Date timeOut, int lateMinutes, int overtimeMinutes, double paidAmount, String remarks)
        {
           SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
           SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm:ss");

        String sql = "INSERT INTO employee_payroll (employee_name, attendance_date, time_in, time_out, late_minutes, overtime_minutes, paid_amount, remarks, id_number) " +
             "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";


           try (Connection conn = DBUtil.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql)) {

               stmt.setString(1, employeeName);
                // insert ID number
               stmt.setString(2, dateFormat.format(date));
               stmt.setString(3, timeFormat.format(timeIn));
               stmt.setString(4, timeFormat.format(timeOut));
              stmt.setInt(5, lateMinutes);
            stmt.setInt(6, overtimeMinutes);
            stmt.setDouble(7, paidAmount);
            stmt.setString(8, remarks);
            stmt.setString(9, idNumber);


        stmt.executeUpdate();
System.out.println("Saving to DB - ID: " + idNumber + ", Name: " + employeeName);

    } catch (Exception ex) {
        ex.printStackTrace();
        JOptionPane.showMessageDialog(this, "Database Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
    }
}

  private String formatMinutesToHHMM(int minutes) {
    int hours = minutes / 60;
    int mins = minutes % 60;
    return String.format("%02d:%02d", hours, mins);
}
    
   private void loadDataFromDatabase(Integer filterMonth, Integer filterYear) {
    tableModel.setRowCount(0);
    totalAmount = 0;
    int totalOvertimeMinutes = 0;

    SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy");
    SimpleDateFormat timeFormat = new SimpleDateFormat("hh:mm:ss a");

    String sql = "SELECT id, attendance_date, time_in, time_out, late_minutes, overtime_minutes, paid_amount, remarks " +
                 "FROM employee_payroll WHERE employee_name = ?";

    if (filterMonth != null && filterYear != null) {
        sql += " AND MONTH(attendance_date) = ? AND YEAR(attendance_date) = ?";
    }

    try (Connection conn = DBUtil.getConnection();
         PreparedStatement stmt = conn.prepareStatement(sql)) {

        stmt.setString(1, employeeName);
        if (filterMonth != null && filterYear != null) {
            stmt.setInt(2, filterMonth);
            stmt.setInt(3, filterYear);
        }

        var rs = stmt.executeQuery();

        while (rs.next()) {
            String date = dateFormat.format(rs.getDate("attendance_date"));
            String timeIn = timeFormat.format(rs.getTime("time_in"));
            String timeOut = timeFormat.format(rs.getTime("time_out"));
            int late = rs.getInt("late_minutes");
            int overtime = rs.getInt("overtime_minutes");
        
            double paid = rs.getDouble("paid_amount");
            String remarks = rs.getString("remarks");
            int id = rs.getInt("id");

            tableModel.addRow(new Object[]{
                id,
                date,
                timeIn,
                timeOut,
//                late,
                overtime, // ✅ Clean formatted display
                String.format("₱%.2f", paid),
//                remarks
            });


            totalAmount += paid;
            totalOvertimeMinutes += overtime;
        }

        // ✅ Fetch and add exec_allowance
        double execAllowance = fetchExecAllowance();
        totalAmount += execAllowance;

        // ✅ Compute overtime pay (assume ₱2 per minute overtime — adjust as needed)
        double overtimeRatePerMinute = 2.0;
        double totalOvertimePay = totalOvertimeMinutes * overtimeRatePerMinute;

        // ✅ Compute final total including overtime pay
        double finalTotal = totalAmount + totalOvertimePay;

        // ✅ Update UI Labels
        totalLabel.setText("Base Total: ₱" + String.format("%.2f", totalAmount));
        overtimeLabel.setText("Total Overtime: " + totalOvertimeMinutes + " minutes (₱" + String.format("%.2f", totalOvertimePay) + ")");
        finalTotalLabel.setText("Final Total: ₱" + String.format("%.2f", finalTotal));

    } catch (Exception ex) {
        ex.printStackTrace();
        JOptionPane.showMessageDialog(this, "Load Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
    }
}
}