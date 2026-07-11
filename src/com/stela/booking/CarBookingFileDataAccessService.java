package com.stela.booking;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Optional;
import java.util.UUID;

public class CarBookingFileDataAccessService implements CarBookingDao {
    private final Path path;

    public CarBookingFileDataAccessService(Path path) {
        this.path = path;
    }

    @Override
    public CarBooking[] getBookings() {
        return findAll();
    }

    @Override
    public Optional<CarBooking> findBookingById(UUID id) {
        CarBooking[] carBookings = findAll();

        for (CarBooking booking : carBookings) {
            if (booking != null && booking.getId().equals(id)) {
                return Optional.of(booking);
            }
        }
        return Optional.empty();
    }

    @Override
    public void saveBooking(CarBooking carBooking) {
        CarBooking[] current = findAll();
        CarBooking[] updated = Arrays.copyOf(current, current.length + 1);
        updated[current.length] = carBooking;
        writeAll(updated);
    }

    @Override
    public boolean deleteBooking(UUID bookingId) {
        CarBooking[] allBookings = findAll();

        for (CarBooking booking : allBookings) {
            if (booking.getId().equals(bookingId) && booking.getStatus() == BookingStatus.ACTIVE) {
                booking.setStatus(BookingStatus.CANCELLED);
                writeAll(allBookings);
                return true;
            }
        }
        return false;
    }

    private CarBooking[] findAll() {
        try {
            if (Files.notExists(path) || Files.size(path) == 0) {
                return new CarBooking[0];
            }

            try (var in = new ObjectInputStream(Files.newInputStream(path))) {
                return (CarBooking[]) in.readObject();
            }
        } catch (IOException | ClassNotFoundException e) {
            throw new BookingPersistenceException("Error occurred while loading the file" + e.getMessage());
        }
    }

    private void writeAll(CarBooking[] bookings) {
        try (var out = new ObjectOutputStream(Files.newOutputStream(path))) {
            out.writeObject(bookings);
        } catch (IOException e) {
            throw new BookingPersistenceException("Error occurred while writing to file" + e.getMessage());
        }
    }
}
