package com.inspire.ers;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class PayrollCalculator {
    public static BigDecimal computeAdjustedSalary(BigDecimal monthlySalary, int totalAbsent, int halfDays, int totalLateMins) {
        BigDecimal dailyRate = monthlySalary.divide(new BigDecimal("22"), 2, RoundingMode.HALF_UP);
        BigDecimal perMinuteRate = dailyRate.divide(new BigDecimal("480"), 4, RoundingMode.HALF_UP);

        BigDecimal absentDeduction = dailyRate.multiply(new BigDecimal(totalAbsent));
        BigDecimal halfDayDeduction = dailyRate.divide(new BigDecimal("2"), 2, RoundingMode.HALF_UP).multiply(new BigDecimal(halfDays));
        BigDecimal lateDeduction = perMinuteRate.multiply(new BigDecimal(totalLateMins));

        return monthlySalary.subtract(absentDeduction.add(halfDayDeduction).add(lateDeduction));
    }
}
