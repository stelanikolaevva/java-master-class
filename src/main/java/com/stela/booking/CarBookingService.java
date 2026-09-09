package com.stela.booking;


import com.stela.car.Car;
import com.stela.car.CarNotFoundException;
import com.stela.car.CarService;
import com.stela.user.AppUser;
import com.stela.user.AppUserNotFoundException;
import com.stela.user.AppUserService;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import static com.stela.util.DatesUtil.isValidBookingPeriod;

@Service
public class CarBookingService {
    private final CarBookingRepository carBookingRepository;
    private final CarService carService;
    private final AppUserService appUserService;

    public CarBookingService(CarBookingRepository carBookingRepository, CarService carService, AppUserService appUserService) {
        this.carBookingRepository = carBookingRepository;
        this.carService = carService;
        this.appUserService = appUserService;
    }

    public List<CarBooking> getBookings() {
        return carBookingRepository.findAll();
    }

    public void deleteBooking(UUID bookingId) {
        carBookingRepository.deleteById(bookingId);
    }

    public CarBooking bookCar(CarBookingRequest bookingRequest) {
        List<CarBooking> bookings = carBookingRepository.findAll();

        AppUser appUser = appUserService.findUserById(bookingRequest.userId())
                .orElseThrow(() -> new AppUserNotFoundException("User with id %s not found", bookingRequest.userId()));

        Car car = carService.getCarById(bookingRequest.carId())
                .orElseThrow(() -> new CarNotFoundException("Car with id %s not found", bookingRequest.carId()));

        if (!isValidBookingPeriod(bookingRequest.startDate(), bookingRequest.endDate())) {
            throw new IllegalArgumentException("Invalid date range!");
        }

        if (!isCarAvailableForPeriod(bookings, car.getId(), bookingRequest.startDate(), bookingRequest.endDate())) {
            throw new CarAlreadyBookedException("Car with plates %s already booked.", car.getRegNumber());
        }

        //Calculate the price for the days the car will be rented.
        BigDecimal daysRented = BigDecimal.valueOf(ChronoUnit.DAYS.between(bookingRequest.startDate(), bookingRequest.endDate()) + 1);
        BigDecimal totalCostForCar = car.getRentalPricePerDay().multiply(daysRented);

        CarBooking carBooking = new CarBooking(appUser, car, bookingRequest.startDate(), bookingRequest.endDate(), totalCostForCar);
        carBookingRepository.save(carBooking);

        return carBooking;
    }

    public List<CarBooking> getBookingsForSpecificUser(UUID userId) {
        return carBookingRepository.findByAppUserId(userId);
    }

    public List<Car> getAllAvailableCars() {
        return getAvailableCars(false);
    }

    public List<Car> getAvailableElectricCars() {
        return getAvailableCars(true);
    }

    private boolean isCarAvailableForPeriod(List<CarBooking> allBookings, UUID carId, LocalDate startDate, LocalDate endDate) {
        return allBookings.stream()
                .filter(booking -> booking.getCar().getId().equals(carId))
                .filter(booking -> booking.getStatus().equals(BookingStatus.ACTIVE))
                .allMatch(b -> endDate.isBefore(b.getStartDate()) || startDate.isAfter(b.getEndDate()));
    }

    private List<Car> getAvailableCars(boolean isElectricOnly) {
        List<CarBooking> carBookings = carBookingRepository.findAll();

        return carService.getCars().stream()
                .filter(car -> !isElectricOnly || car.isElectric())
                .filter(car -> hasNoActiveBooking(carBookings, car.getId()))
                .collect(Collectors.toList());
    }

    private boolean hasNoActiveBooking(List<CarBooking>  bookings, UUID carId) {
        return bookings.stream()
                .noneMatch(b -> b.getCar().getId().equals(carId) && b.getStatus() == BookingStatus.ACTIVE);
    }
}