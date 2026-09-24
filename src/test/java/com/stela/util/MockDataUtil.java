package com.stela.util;

import com.stela.booking.BookingStatus;
import com.stela.booking.CarBooking;
import com.stela.booking.CarBookingResponse;
import com.stela.car.Car;
import com.stela.car.CarResponse;
import com.stela.user.AppUser;
import com.stela.user.AppUserResponse;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.ObjectMapper;

import java.io.File;
import java.util.List;

public class MockDataUtil {
    static ObjectMapper objectMapper = new ObjectMapper();

    public static List<CarBooking> getCarBookings() {
        return objectMapper.readValue(new File("src/test/resources/data/booking.json"), new TypeReference<>() {
        });
    }

    public static List<CarBookingResponse> getCarBookingsResponse() {
        return objectMapper.readValue(new File("src/test/resources/data/bookingResponse.json"), new TypeReference<>() {
        });
    }

    public static List<Car> getCars() {
        return objectMapper.readValue(new File("src/test/resources/data/cars.json"), new TypeReference<>() {
        });
    }

    public static List<CarResponse> getCarsResponse() {
        return objectMapper.readValue(new File("src/test/resources/data/cars.json"), new TypeReference<>() {
        });
    }

    public static List<AppUser> getUsers() {
        return objectMapper.readValue(new File("src/test/resources/data/users.json"), new TypeReference<>() {
        });
    }
    public static List<AppUserResponse> getUsersResponse() {
        return objectMapper.readValue(new File("src/test/resources/data/users.json"), new TypeReference<>() {
        });
    }

    public static CarBooking getBookingForCarAndStatus(String carId, BookingStatus status) {
        return MockDataUtil.getCarBookings().stream()
                .filter(carBooking -> carBooking.getStatus().equals(status))
                .filter(carBooking -> carBooking.getCar().getId().toString().equals(carId))
                .findFirst()
                .orElse(null);
    }

    public static Car getCarForCarId(String carId) {
        return MockDataUtil.getCars().stream()
                .filter(car -> car.getId().toString().equals(carId))
                .findFirst()
                .orElse(null);
    }

    public static CarResponse getCarByID(String id) {
        return MockDataUtil.getCarsResponse().stream()
                .filter(car -> car.id().toString().equals(id))
                .findFirst()
                .orElse(null);
    }
}
