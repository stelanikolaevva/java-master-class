package com.stela.util;

import com.stela.booking.CarBooking;
import com.stela.car.Car;

import java.time.LocalDate;
import java.util.UUID;

import static com.stela.util.DatesUtil.isCarAvailableForPeriod;

public class ArraysUtil {

    /**
     * Prints the string output in a format without braces and commas
     *
     * @param output - the default toString of an array
     */
    public static void printFormatedArrayOutput(String output) {
        //Just for formating purposes as we are using Arrays
        System.out.println(
                output.replace("[", "")
                        .replace("]", "")
                        .replace(",", ""));
    }

    /**
     * used for array initialization
     *
     * @param bookings  all car bookings
     * @param allCars   all cars
     * @param startDate the start date of the period we should check for availability
     * @param endDate   - the end date
     * @return - total available cars count
     */
    public static int getAvailableCarsCount(CarBooking[] bookings, Car[] allCars, LocalDate startDate, LocalDate endDate) {
        int count = 0;
        for (Car car : allCars) {
            if (isCarAvailableForPeriod(bookings, car, startDate, endDate)) {
                count++;
            }
        }
        return count;
    }

    /**
     * used for array initialization
     *
     * @param availableCars total cars array
     * @return the count of the electric cars
     */
    public static int getElectricCarsCount(Car[] availableCars) {
        int count = 0;
        for (Car car : availableCars) {
            if (car.isElectric()) {
                count++;
            }
        }
        return count;
    }

    /**
     * used for array initialization
     *
     * @param bookings - all car bookings
     * @param userId   - user which car we should search for
     * @return - the number of cars he had booked
     */
    public static int getUserCarsCount(CarBooking[] bookings, UUID userId) {
        int count = 0;
        for (CarBooking b : bookings) {
            if (b.getUser().getId().equals(userId)) {
                count++;
            }
        }
        return count;
    }
}
