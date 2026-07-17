package com.stela.booking;

import com.stela.car.Car;
import com.stela.car.CarNotFoundException;
import com.stela.car.CarService;
import com.stela.user.User;
import com.stela.user.UserNotFoundException;
import com.stela.user.UserService;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static com.stela.util.DatesUtil.isValidBookingPeriod;

public class CarBookingService {
    private final CarBookingDao carBookingDao;
    private final CarService carService;
    private final UserService userService;

    public CarBookingService(CarBookingDao carBookingDao, CarService carService, UserService userService) {
        this.carBookingDao = carBookingDao;
        this.carService = carService;
        this.userService = userService;
    }

    public List<CarBooking> getBookings() {
        return carBookingDao.getBookings();
    }

    public boolean deleteBooking(UUID bookingId) {
        return carBookingDao.deleteBooking(bookingId);
    }

    public CarBooking bookCar(CarBooking bookingRequest) {
        List<CarBooking> bookings = carBookingDao.getBookings();

        User user = userService.findUserById(bookingRequest.getUserId())
                .orElseThrow(() -> new UserNotFoundException("User with id %s not found", bookingRequest.getUserId()));

        Car car = carService.getCarById(bookingRequest.getCarId())
                .orElseThrow(() -> new CarNotFoundException("Car with id %s not found", bookingRequest.getCarId()));

        if (!isValidBookingPeriod(bookingRequest.getStartDate(), bookingRequest.getEndDate())) {
            throw new IllegalArgumentException("Invalid date range!");
        }

        if (!isCarAvailableForPeriod(bookings, car.getId(), bookingRequest.getStartDate(), bookingRequest.getEndDate())) {
            throw new CarAlreadyBookedException("Car with plates %s already booked.", car.getRegNumber());
        }

        //Calculate the price for the days the car will be rented.
        BigDecimal daysRented = BigDecimal.valueOf(ChronoUnit.DAYS.between(bookingRequest.getStartDate(), bookingRequest.getEndDate()) + 1);
        BigDecimal totalCostForCar = car.getRentalPricePerDay().multiply(daysRented);

        CarBooking carBooking = new CarBooking(user.getId(), car.getId(), bookingRequest.getStartDate(), bookingRequest.getEndDate(), totalCostForCar);
        carBookingDao.saveBooking(carBooking);

        return carBooking;
    }

    public List<Car> getCarsForSpecificUser(UUID userId) {
        List<CarBooking> bookings = carBookingDao.getBookings();
        List<Car> carsForUser = new ArrayList<>();

        for (CarBooking carBooking : bookings) {
            if (carBooking.getUserId().equals(userId)) {
                Optional<Car> carById = carService.getCarById(carBooking.getCarId());
                carById.ifPresent(carsForUser::add);
            }
        }
        return carsForUser;
    }

    public List<Car> getAllAvailableCars() {
        return getAvailableCars(false);
    }

    public List<Car> getAvailableElectricCars() {
        return getAvailableCars(true);
    }

    private boolean isCarAvailableForPeriod(List<CarBooking> allBookings, UUID carId, LocalDate startDate, LocalDate endDate) {
        for (CarBooking booking : allBookings) {
            if (booking.getCarId().equals(carId) && booking.getStatus().equals(BookingStatus.ACTIVE)) {

                //we already are checking if the end date is after start date and that start day is in the future or today
                boolean hasNoOverlap = endDate.isBefore(booking.getStartDate())
                        || startDate.isAfter(booking.getEndDate());

                if (!hasNoOverlap) {
                    return false;
                }
            }
        }
        return true; // if there are no booking then the car is free
    }

    private List<Car> getAvailableCars(boolean isElectricOnly) {
        List<Car> cars = carService.getCars();
        List<CarBooking> carBookings = carBookingDao.getBookings();

        List<Car> availableCars = new ArrayList<>();

        for (Car car : cars) {
            if (isElectricOnly && !car.isElectric()) continue;
            if (hasNoActiveBooking(carBookings, car.getId())) availableCars.add(car);
        }
        return availableCars;
    }

    private boolean hasNoActiveBooking(List<CarBooking>  bookings, UUID carId) {
        for (CarBooking b : bookings) {
            if (b.getCarId().equals(carId) && b.getStatus() == BookingStatus.ACTIVE) {
                return false;
            }
        }
        return true;
    }
}