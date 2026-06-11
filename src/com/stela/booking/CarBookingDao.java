package com.stela.booking;

import java.util.Arrays;
import java.util.Optional;
import java.util.UUID;

public class CarBookingDao {
    private static int maxIndex = 16;
    private static CarBooking[] carBookings = new CarBooking[maxIndex];

    private static int currentIndex = 0;

    /**
     * @return an array of all added bookings
     */
    public CarBooking[] getBookings() {
        return Arrays.copyOf(carBookings, currentIndex);
    }

    /**
     * @param id the booking id
     * @return an Optional with the booking, or empty if none
     */
    public Optional<CarBooking> findBookingById(UUID id) {
        for (CarBooking booking : carBookings) {
            if (booking != null && booking.getId().equals(id)) {
                return Optional.of(booking);
            }
        }
        return Optional.empty();
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
     * @param bookingId that will be canceled
     * @return true if booking is canceled
     */
    public boolean deleteBooking(UUID bookingId) {
        Optional<CarBooking> bookingById = findBookingById(bookingId);
        if (bookingById.isPresent() && bookingById.get().getStatus() == BookingStatus.ACTIVE) {
            bookingById.get().setStatus(BookingStatus.CANCELLED);
            return true;
        }
        return false;
    }
}
