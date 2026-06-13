package com.stela.util;

import java.time.LocalDate;

public class DatesUtil {

    public static boolean isValidBookingPeriod(LocalDate startDate, LocalDate endDate) {
        return !startDate.isAfter(endDate) && !startDate.isBefore(LocalDate.now());
    }
}
