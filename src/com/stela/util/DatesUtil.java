package com.stela.util;

import com.stela.booking.BookingStatus;
import com.stela.booking.CarBooking;
import com.stela.car.Car;

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

    /**
     * @param allBookings all present car bookings
     * @param car         - the car we search for
     * @param startDate   - the start of the period for which we should look for availability
     * @param endDate     - the end of the period
     * @return if the car is not already booked for this time
     */
    public static boolean isCarAvailableForPeriod(CarBooking[] allBookings, Car car, LocalDate startDate, LocalDate endDate) {
        for (CarBooking booking : allBookings) {
            if (booking.getCar().equals(car) && booking.getStatus().equals(BookingStatus.ACTIVE)) {

                //we already are checking if the end date is after start date and that start day is after today
                boolean hasNoOverlap = endDate.isBefore(booking.getStartDate())
                        || startDate.isAfter(booking.getEndDate());

                if (!hasNoOverlap) {
                    return false;
                }
            }
        }
        return true; // if there are no booking then the car is free
    }
}
