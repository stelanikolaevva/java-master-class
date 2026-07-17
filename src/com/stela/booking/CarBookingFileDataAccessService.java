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
        return findAll();
    }

    @Override
    public Optional<CarBooking> findBookingById(UUID id) {
        List<CarBooking> carBookings = findAll();

        for (CarBooking booking : carBookings) {
            if (booking != null && booking.getId().equals(id)) {
                return Optional.of(booking);
            }
        }
        return Optional.empty();
    }

    @Override
    public void saveBooking(CarBooking carBooking) {
        List<CarBooking> allBookings = findAll();
        allBookings.add(carBooking);

        writeAll(allBookings);
    }

    @Override
    public boolean deleteBooking(UUID bookingId) {
        List<CarBooking> allBookings = findAll();

        for (CarBooking booking : allBookings) {
            if (booking.getId().equals(bookingId) && booking.getStatus() == BookingStatus.ACTIVE) {
                booking.setStatus(BookingStatus.CANCELLED);
                writeAll(allBookings);
                return true;
            }
        }
        return false;
    }

    private List<CarBooking> findAll() {
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
