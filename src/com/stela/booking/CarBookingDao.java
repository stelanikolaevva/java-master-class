package com.stela.booking;

import java.util.Optional;
import java.util.UUID;

public interface CarBookingDao {
    CarBooking[] getBookings();

    Optional<CarBooking> findBookingById(UUID id);

    void saveBooking(CarBooking carBooking);

    boolean deleteBooking(UUID bookingId);
}
