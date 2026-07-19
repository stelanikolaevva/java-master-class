package com.stela.booking;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class CarBookingFileDataAccessService implements CarBookingDao {
    private final Path path;

    public CarBookingFileDataAccessService(Path path) {
        this.path = path;
    }

    @Override
    public List<CarBooking> getBookings() {
        return getAllBookings();
    }

    @Override
    public Optional<CarBooking> findBookingById(UUID id) {
        return getAllBookings().stream()
                .filter(carBooking -> carBooking.getCarId().equals(id))
                .findFirst();
    }

    @Override
    public void saveBooking(CarBooking carBooking) {
        List<CarBooking> allBookings = getAllBookings();
        allBookings.add(carBooking);
        writeAll(allBookings);
    }

    @Override
    public boolean deleteBooking(UUID bookingId) {
        List<CarBooking> allBookings = getAllBookings();

        Optional<CarBooking> bookingToCancel = allBookings.stream()
                .filter(booking -> booking.getId().equals(bookingId))
                .filter(booking -> booking.getStatus() == BookingStatus.ACTIVE)
                .findFirst();

        if (bookingToCancel.isEmpty()) {
            return false;
        }

        bookingToCancel.get().setStatus(BookingStatus.CANCELLED);
        writeAll(allBookings);
        return true;
    }

    private List<CarBooking> getAllBookings() {
        try {
            if (Files.notExists(path) || Files.size(path) == 0) {
                return new ArrayList<>();
            }

            try (var in = new ObjectInputStream(Files.newInputStream(path))) {
                return (List<CarBooking>) in.readObject();
            }
        } catch (IOException | ClassNotFoundException | ClassCastException e) {
            throw new BookingPersistenceException("Error occurred while loading the file" + e.getMessage());
        }
    }

    private void writeAll(List<CarBooking> bookings) {
        try (var out = new ObjectOutputStream(Files.newOutputStream(path))) {
            out.writeObject(bookings);
        } catch (IOException e) {
            throw new BookingPersistenceException("Error occurred while writing to file" + e.getMessage());
        }
    }
}
