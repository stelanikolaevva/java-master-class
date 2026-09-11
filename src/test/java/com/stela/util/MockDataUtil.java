package com.stela.util;

import com.stela.booking.BookingStatus;
import com.stela.booking.CarBooking;
import com.stela.car.Car;
import com.stela.user.AppUser;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.ObjectMapper;

import java.io.File;
import java.util.List;
import java.util.UUID;

public class MockDataUtil {
    static ObjectMapper objectMapper = new ObjectMapper();

    public static List<CarBooking> getCarBookings() {
        return objectMapper.readValue(new File("src/test/resources/data/booking.json"), new TypeReference<>() {
        });
    }

    public static List<Car> getCars() {
        return objectMapper.readValue(new File("src/test/resources/data/cars.json"), new TypeReference<>() {
        });
    }

    public static List<AppUser> getUsers() {
        return objectMapper.readValue(new File("src/test/resources/data/users.json"), new TypeReference<>() {
        });
    }

    public static CarBooking getCancelledBookingForUser(UUID appUserId) {
        return MockDataUtil.getCarBookings().stream()
                .filter(carBooking -> carBooking.getStatus().equals(BookingStatus.CANCELLED))
                .filter(carBooking -> carBooking.getAppUser().getId().equals(appUserId))
                .findFirst()
                .orElse(null);
    }

    public static CarBooking getCompletedBookingForUser(UUID appUserId) {
        return MockDataUtil.getCarBookings().stream()
                .filter(carBooking -> carBooking.getStatus().equals(BookingStatus.COMPLETED))
                .filter(carBooking -> carBooking.getAppUser().getId().equals(appUserId))
                .findFirst()
                .orElse(null);
    }

    public static Car getCarByID(String id) {
        return MockDataUtil.getCars().stream()
                .filter(car -> car.getId().toString().equals(id))
                .findFirst()
                .orElse(null);
    }
}
