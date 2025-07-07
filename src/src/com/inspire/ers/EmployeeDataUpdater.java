package com.inspire.ers;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import src.com.inspire.ers.DBUtil;

public class EmployeeDataUpdater {
    
    public static boolean insertPayrollOnly(String[] data) {
    String id = data[0].trim();
    try (Connection conn = DBUtil.getConnection()) {
        String checkSQL = "SELECT COUNT(*) FROM payroll WHERE id_number = ? AND pay_date = ?";
        try (PreparedStatement checkStmt = conn.prepareStatement(checkSQL)) {
            checkStmt.setString(1, id);
            checkStmt.setString(2, data[21]); // pay_date
            ResultSet rs = checkStmt.executeQuery();
            rs.next();
            int count = rs.getInt(1);
            rs.close();

            if (count > 0) {
                System.out.println("Payroll already exists for ID " + id + " and date " + data[21]);
                return false;
            }
        }

        String insertSQL = "INSERT INTO payroll (" +
            "id_number, refreshment, mins, total_late, absent, half_day, " +
            "total_absent, ot_hours, ot_pay, number_of_days, daily, per_hour, per_minute, " +
            "pay_date, cutoff_start, cutoff_end) " +
            "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (PreparedStatement stmt = conn.prepareStatement(insertSQL)) {
            stmt.setString(1, id);
            stmt.setString(2, data[6]);
            stmt.setString(3, data[7]);
            stmt.setString(4, data[8]);
            stmt.setString(5, data[9]);
            stmt.setString(6, data[11]);
            stmt.setString(7, data[13]);
            stmt.setString(8, data[14]);
            stmt.setString(9, data[15]);
            stmt.setString(10, data[17]);
            stmt.setString(11, data[18]);
            stmt.setString(12, data[19]);
            stmt.setString(13, data[20]);
            stmt.setString(14, data[21]); // pay_date
            stmt.setString(15, data[22]); // cutoff_start
            stmt.setString(16, data[23]); // cutoff_end
            int rows = stmt.executeUpdate();
            System.out.println("Inserted new payroll row for ID: " + id);
            return rows > 0;
        }

    } catch (SQLException e) {
        e.printStackTrace();
        return false;
    }
}


    public static boolean updateEmployeeData(String[] data) {
        String id = data[0].trim();
        System.out.println("Updating DB for ID: " + id);

        try (Connection conn = DBUtil.getConnection()) {
            conn.setAutoCommit(false); // Start transaction

            // Split name into last and first name
            String[] nameParts = data[1].split(",", 2);
            String lastName = nameParts.length > 0 ? nameParts[0].trim() : "";
            String firstName = nameParts.length > 1 ? nameParts[1].trim() : "";

            // === Update EMPLOYEES Table ===
            String updateEmployeeSQL = "UPDATE employees SET " +
                    "last_name = ?, first_name = ?, " +
                    "monthly_salary = ?, basic_pay = ?, exec_allowance = ?, " +
                    "bank_account = ?, position = ? " +
                    "WHERE id_number = ?";

            try (PreparedStatement empStmt = conn.prepareStatement(updateEmployeeSQL)) {
                empStmt.setString(1, lastName);
                empStmt.setString(2, firstName);
                empStmt.setString(3, data[16]); // monthly_salary
                empStmt.setString(4, data[4]);  // basic_pay
                empStmt.setString(5, data[5]);  // exec_allowance
                empStmt.setString(6, data[3]);  // bank_account
                empStmt.setString(7, data[2]);  // position
                empStmt.setString(8, id);       // id_number
                int rows = empStmt.executeUpdate();
                System.out.println("Employees table rows updated: " + rows);
            }

            // === PAYROLL Table ===
            String checkSQL = "SELECT COUNT(*) FROM payroll WHERE id_number = ? AND pay_date = ?";
            try (PreparedStatement checkStmt = conn.prepareStatement(checkSQL)) {
                checkStmt.setString(1, id);
                checkStmt.setString(2, data[21]);
                ResultSet rs = checkStmt.executeQuery();
                rs.next();
                int count = rs.getInt(1);
                rs.close();

                if (count > 0) {
                    // Update payroll record
                    String updatePayrollSQL = "UPDATE payroll SET " +
                    "refreshment = ?, mins = ?, total_late = ?, absent = ?, half_day = ?, " +
                    "total_absent = ?, ot_hours = ?, ot_pay = ?, number_of_days = ?, daily = ?, " +
                    "per_hour = ?, per_minute = ?, pay_date = ?, cutoff_start = ?, cutoff_end = ? " +
                    "WHERE id_number = ?";


                    try (PreparedStatement payrollStmt = conn.prepareStatement(updatePayrollSQL)) {
                        payrollStmt.setString(1, data[6]);
                        payrollStmt.setString(2, data[7]);
                        payrollStmt.setString(3, data[8]);
                        payrollStmt.setString(4, data[9]);
                        payrollStmt.setString(5, data[11]); // half_day
                        payrollStmt.setString(6, data[13]); // total_absent
                        payrollStmt.setString(7, data[14]);
                        payrollStmt.setString(8, data[15]);
                        payrollStmt.setString(9, data[17]);
                        payrollStmt.setString(10, data[18]);
                        payrollStmt.setString(11, data[19]);
                        payrollStmt.setString(12, data[20]);
                        payrollStmt.setString(13, data[21]); // pay_date
                        payrollStmt.setString(14, data[22]); // cutoff_start
                        payrollStmt.setString(15, data[23]); // cutoff_end
                        payrollStmt.setString(16, id);
                        int rows = payrollStmt.executeUpdate();
                        System.out.println("Payroll table rows updated: " + rows);
                    }

                } else {
                    // Insert new payroll record
                    String insertPayrollSQL = "INSERT INTO payroll (" +
                    "id_number, refreshment, mins, total_late, absent, half_day, " +
                    "total_absent, ot_hours, ot_pay, number_of_days, daily, per_hour, per_minute, " +  // comma added
                    "pay_date, cutoff_start, cutoff_end) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";


                    try (PreparedStatement insertStmt = conn.prepareStatement(insertPayrollSQL)) {
                        insertStmt.setString(1, id);
                        insertStmt.setString(2, data[6]);
                        insertStmt.setString(3, data[7]);
                        insertStmt.setString(4, data[8]);
                        insertStmt.setString(5, data[9]);
                        insertStmt.setString(6, data[11]);
                        insertStmt.setString(7, data[13]);
                        insertStmt.setString(8, data[14]);
                        insertStmt.setString(9, data[15]);
                        insertStmt.setString(10, data[17]);
                        insertStmt.setString(11, data[18]);
                        insertStmt.setString(12, data[19]);
                        insertStmt.setString(13, data[20]);
                        insertStmt.setString(14, data[21]); // pay_date
                        insertStmt.setString(15, data[22]); // cutoff_start
                        insertStmt.setString(16, data[23]); // cutoff_end
                        int rows = insertStmt.executeUpdate();
                        System.out.println("Payroll table rows inserted: " + rows);
                    }
                }
            }
            conn.commit(); // Commit transaction
            return true;

        } catch (SQLException ex) {
            ex.printStackTrace();
            return false;
        }
    }
}