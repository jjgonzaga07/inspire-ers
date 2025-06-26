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
    private DefaultTableModel tableModel;
    private JTable attendanceTable;
    private JLabel totalLabel;
    private double totalAmount = 0;

    // Constants
    private static final double BASE_SALARY = 818.18; // Salary for 8 hrs
    private static final double LATE_PENALTY_PER_MINUTE = 1.71;

    public PayrollPage(String employeeName) {
        this.employeeName = employeeName;

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

        String[] columns = {"DATE", "TIME-IN", "TIME-OUT", "LATE (mins)", "Paid Amount", "Remarks"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        attendanceTable = new JTable(tableModel);
        loadDataFromDatabase(null, null);
        attendanceTable.setFillsViewportHeight(true);

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

        JButton addButton = new JButton("ADD");
        bottomPanel.add(addButton, BorderLayout.WEST);

        totalLabel = new JLabel("Total: ₱" + String.format("%.2f", totalAmount));
        totalLabel.setHorizontalAlignment(JLabel.RIGHT);
        bottomPanel.add(totalLabel, BorderLayout.EAST);

        mainPanel.add(bottomPanel, BorderLayout.SOUTH);

        addButton.addActionListener(e -> showAddAttendanceDialog());

        add(mainPanel);
        
        ActionListener filterAction = e -> {
            int selectedMonthIndex = monthComboBox.getSelectedIndex(); // 0 = Jan
            int selectedMonth = selectedMonthIndex + 1; // SQL months are 1-based
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
        double paidAmount = Double.parseDouble(data[4].replace("\"₱", "").replace("\"", "").trim());
        String remarks = data[5].replace("\"", "").trim();

        Date date = dateFormat.parse(dateStr);
        Date timeIn = timeFormat.parse(timeInStr);
        Date timeOut = timeFormat.parse(timeOutStr);

        saveToDatabase(date, timeIn, timeOut, lateMinutes, paidAmount, remarks);
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
        double paidAmount = row.getCell(4).getNumericCellValue();
        String remarks = row.getCell(5).toString().trim();

        Date date = dateFormat.parse(dateStr);
        Date timeIn = timeFormat.parse(timeInStr);
        Date timeOut = timeFormat.parse(timeOutStr);

        saveToDatabase(date, timeIn, timeOut, lateMinutes, paidAmount, remarks);
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
            int lateMinutes = calculateLateMinutes(timeIn);

            // 💰 Calculate final salary with late deduction
            double deduction = lateMinutes * LATE_PENALTY_PER_MINUTE;
            double paidAmount = BASE_SALARY - deduction;

            Date timeOut = (Date) timeOutSpinner.getValue();
            String remarks = getRemarks(timeIn, timeOut);

            Object[] rowData = {
                dateFormat.format(dateSpinner.getValue()),
                timeFormat.format(timeIn),
                timeFormat.format(timeOut),
                lateMinutes,
                String.format("₱%.2f", paidAmount),
                remarks
            };


            Date selectedDate = (Date) dateSpinner.getValue(); // retrieve date from spinner

            tableModel.addRow(rowData);
//            totalAmount += paidAmount;
//            totalLabel.setText("Total: ₱" + String.format("%.2f", totalAmount));

            // 🔧 Save to database
            saveToDatabase(selectedDate, timeIn, timeOut, lateMinutes, paidAmount, remarks);

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
        Calendar scheduled = Calendar.getInstance();
        scheduled.setTime(timeIn);
        scheduled.set(Calendar.HOUR_OF_DAY, 9);
        scheduled.set(Calendar.MINUTE, 30);
        scheduled.set(Calendar.SECOND, 0);

        long diffMillis = timeIn.getTime() - scheduled.getTimeInMillis();
        int lateMinutes = (int) (diffMillis / (60 * 1000));

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
        if (isLate) return "[Tardy]";
        else if (isEarlyLeave) return "[Early Leaving]";
        else return "On Time";
     }
    
        private void saveToDatabase(Date date, Date timeIn, Date timeOut, int lateMinutes, double paidAmount, String remarks) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm:ss");


        String sql = "INSERT INTO employee_payroll (employee_name, attendance_date, time_in, time_out, late_minutes, paid_amount, remarks) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, employeeName);
            stmt.setString(2, dateFormat.format(date));
            stmt.setString(3, timeFormat.format(timeIn));
            stmt.setString(4, timeFormat.format(timeOut));

            stmt.setInt(5, lateMinutes);
            stmt.setDouble(6, paidAmount);
            stmt.setString(7, remarks);

            stmt.executeUpdate();

        } catch (Exception ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Database Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void loadDataFromDatabase(Integer filterMonth, Integer filterYear) {
    tableModel.setRowCount(0);
    totalAmount = 0;

    SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy");
    SimpleDateFormat timeFormat = new SimpleDateFormat("hh:mm:ss a");

    String sql = "SELECT attendance_date, time_in, time_out, late_minutes, paid_amount, remarks " +
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
            double paid = rs.getDouble("paid_amount");
            String remarks = rs.getString("remarks");

            tableModel.addRow(new Object[]{
                date,
                timeIn,
                timeOut,
                late,
                String.format("₱%.2f", paid),
                remarks
            });

            totalAmount += paid;
        }

        totalLabel.setText("Total: ₱" + String.format("%.2f", totalAmount));

    } catch (Exception ex) {
        ex.printStackTrace();
        JOptionPane.showMessageDialog(this, "Load Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
    }
}

}