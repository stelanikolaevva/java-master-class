package com.stela.util;

import java.time.LocalDate;

public class DatesUtil {
    /**
     * @param startDate - user input
     * @param endDate   - user input
     * @return true if the range is valid: start is not after end and not in the past (today allowed)
     */
    public static boolean areDatesValid(LocalDate startDate, LocalDate endDate) {
        return !startDate.isAfter(endDate) && !startDate.isBefore(LocalDate.now());
    }
}
