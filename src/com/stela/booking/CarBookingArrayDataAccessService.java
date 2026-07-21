package com.stela.booking;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class CarBookingArrayDataAccessService implements CarBookingDao {
    private static final List<CarBooking> carBookings = new ArrayList<>();

    @Override
    public List<CarBooking> getBookings() {
        return carBookings;
    }

    @Override
    public Optional<CarBooking> findBookingById(UUID id) {
        return carBookings.stream()
                .filter(carBooking -> carBooking.getId().equals(id))
                .findFirst();
    }

    @Override
    public void saveBooking(CarBooking carBooking) {
        carBookings.add(carBooking);
    }

    @Override
    public boolean deleteBooking(UUID bookingId) {
        Optional<CarBooking> bookingById = findBookingById(bookingId);
        if (bookingById.isPresent() && bookingById.get().getStatus() == BookingStatus.ACTIVE) {
            bookingById.get().setStatus(BookingStatus.CANCELLED);
            return true;
        }
        return false;
    }
}
