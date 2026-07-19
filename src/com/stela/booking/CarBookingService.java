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
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

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
        return carBookingDao.getBookings().stream()
                .filter(booking -> booking.getUserId().equals(userId))
                .map(booking -> carService.getCarById(booking.getCarId()))
                .flatMap(Optional::stream)
                .collect(Collectors.toList());
    }

    public List<Car> getAllAvailableCars() {
        return getAvailableCars(false);
    }

    public List<Car> getAvailableElectricCars() {
        return getAvailableCars(true);
    }

    private boolean isCarAvailableForPeriod(List<CarBooking> allBookings, UUID carId, LocalDate startDate, LocalDate endDate) {
        return allBookings.stream()
                .filter(booking -> booking.getCarId().equals(carId))
                .filter(booking -> booking.getStatus().equals(BookingStatus.ACTIVE))
                .allMatch(b -> endDate.isBefore(b.getStartDate()) || startDate.isAfter(b.getEndDate()));
    }

    private List<Car> getAvailableCars(boolean isElectricOnly) {
        List<CarBooking> carBookings = carBookingDao.getBookings();

        return carService.getCars().stream()
                .filter(car -> !isElectricOnly || car.isElectric())
                .filter(car -> hasNoActiveBooking(carBookings, car.getId()))
                .collect(Collectors.toList());
    }

    private boolean hasNoActiveBooking(List<CarBooking>  bookings, UUID carId) {
        return bookings.stream()
                .noneMatch(b -> b.getCarId().equals(carId) && b.getStatus() == BookingStatus.ACTIVE);
    }
}