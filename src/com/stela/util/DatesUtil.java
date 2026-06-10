package com.stela.util;

import java.time.LocalDate;

public class DatesUtil {
    /**
     * @param startDate - user input
     * @param endDate   - user input
     * @return true if the start date is after the end date, same dates are allowed
     */
    public static boolean isStartDateAfterEndDate(LocalDate startDate, LocalDate endDate) {
        return startDate.isAfter(endDate);
    }
}
