package com.stela.booking;

import java.util.Arrays;
import java.util.UUID;

public class CarBookingDao {
    private static int maxIndex = 1;
    private static CarBooking[] carBookings = new CarBooking[maxIndex];

    private static int currentIndex = 0;

    /**
     * @return an array of bookings
     */
    public CarBooking[] getBookings() {
        CarBooking[] actualBookings = new CarBooking[currentIndex];
        if (currentIndex != 0) {
            for (int i = 0; i < currentIndex; i++) {
                actualBookings[i] = carBookings[i];
            }
        }
        return actualBookings;
    }

    /**
     * @param id the booking id
     * @return the Booking object with that id or null if not present
     */
    public CarBooking findBookingById(UUID id) {
        for (CarBooking booking : carBookings) {
            if (booking != null && booking.getId().equals(id)) {
                return booking;
            }
        }
        return null;
    }


    /**
     * @param carBooking the new booking that will be added in the system
     */
    public void saveBooking(CarBooking carBooking) {
        if (currentIndex == maxIndex) {
            maxIndex = maxIndex * 2;
            carBookings = Arrays.copyOf(carBookings, maxIndex);
        }
        carBookings[currentIndex++] = carBooking;
    }

    /**
     * @param booking - the booking id that will be set to status CANCELLED
     */
    public void deleteBooking(CarBooking booking) {
        booking.setStatus(BookingStatus.CANCELLED);
    }
}
