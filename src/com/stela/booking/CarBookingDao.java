package com.stela.booking;

import java.util.UUID;

import static com.stela.util.MenuUtil.MAX_INDEX;

public class CarBookingDao {
    private static final CarBooking[] carBookings = new CarBooking[MAX_INDEX];
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
     * @return the Booking object with that id
     * @throws BookingNotFoundException - if no such booking is found
     */
    public CarBooking findBookingById(UUID id) throws BookingNotFoundException {
        for (CarBooking booking : carBookings) {
            if (booking != null && booking.getId().equals(id)) {
                return booking;
            }
        }
        throw new BookingNotFoundException("Booking with id %s not found!", id);
    }


    /**
     * @param carBooking the new booking that will be added in the system
     */
    public void saveBooking(CarBooking carBooking) {
        if (currentIndex == MAX_INDEX) {
            throw new IllegalArgumentException("Booking storage full!");
        }
        carBookings[currentIndex++] = carBooking;
    }

    /**
     * @param bookingId - the booking id that will be set to status CANCELLED
     * @throws BookingNotFoundException - if no such booking exists or if it already canceled
     */
    public void deleteBooking(UUID bookingId) throws BookingNotFoundException {
        for (CarBooking carBooking : carBookings) {
            if (carBooking != null && carBooking.getId().equals(bookingId)) {
                if (carBooking.getStatus().equals(BookingStatus.CANCELLED)) {
                    throw new BookingNotFoundException("Booking %s already cancelled", bookingId);
                }
                carBooking.setStatus(BookingStatus.CANCELLED);
                return;
            }
        }
        throw new BookingNotFoundException("Booking with id %s not found!", bookingId);
    }
}
