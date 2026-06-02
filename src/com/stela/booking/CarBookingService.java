package com.stela.booking;

import com.stela.car.Car;
import com.stela.car.CarAlreadyBookedException;
import com.stela.car.CarNotFoundException;
import com.stela.car.CarService;
import com.stela.user.User;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

import static com.stela.util.ArraysUtil.getUserCarsCount;
import static com.stela.util.ArraysUtil.getElectricCarsCount;
import static com.stela.util.ArraysUtil.getAvailableCarsCount;
import static com.stela.util.DatesUtil.isCarAvailableForPeriod;


public class CarBookingService {
    private final CarBookingDao carBookingDao = new CarBookingDao();
    private final CarService carService = new CarService();

    /**
     * @return all bookings
     */
    public CarBooking[] getBookings() {
        return carBookingDao.getBookings();
    }

    /**
     * @param bookingId of the booking that is getting canceled
     * @throws BookingNotFoundException - if no such booking exists
     */
    public void deleteBooking(UUID bookingId) throws BookingNotFoundException {
        carBookingDao.deleteBooking(bookingId);
    }

    /**
     * @param user      who is booking the car
     * @param car       which car he is booking
     * @param startDate - start of the booking period
     * @param endDate   - end of the booking period
     * @throws CarAlreadyBookedException - if the car is already booked
     */
    public void bookCar(User user, Car car, LocalDate startDate, LocalDate endDate) throws CarAlreadyBookedException {
        CarBooking[] bookings = carBookingDao.getBookings();

        if (!isCarAvailableForPeriod(bookings, car, startDate, endDate)) {
            throw new CarAlreadyBookedException("Car with plates %s already booked.", car.getRegNumber());
        }

        //Calculate the price for the days the car will be rented.
        BigDecimal daysRented = BigDecimal.valueOf(ChronoUnit.DAYS.between(startDate, endDate) + 1);
        BigDecimal totalCostForCar = car.getRentalPricePerDay().multiply(daysRented);

        CarBooking carBooking = new CarBooking(user, car, startDate, endDate, totalCostForCar);
        carBookingDao.saveBooking(carBooking);
    }


    /**
     * @param userId of the selected user
     * @return all his booked cars
     * @throws CarNotFoundException - if he has not booked anything yet
     */
    public Car[] getCarsForSpecificUser(UUID userId) throws CarNotFoundException {
        int userCarsCount = getUserCarsCount(carBookingDao.getBookings(), userId);
        Car[] carsForUser = new Car[userCarsCount];

        int index = 0;
        for (CarBooking carBooking : carBookingDao.getBookings()) {
            if (carBooking.getUser().getId().equals(userId)) {
                carsForUser[index++] = carBooking.getCar();
            }
        }
        if (index == 0) {
            throw new CarNotFoundException("No cars booked!");
        }
        return carsForUser;
    }

    /**
     * @param startDate start of period
     * @param endDate   end of period
     * @return an array of the available cars for that period
     */
    public Car[] getAvailableCars(LocalDate startDate, LocalDate endDate) {
        CarBooking[] bookings = getBookings();
        Car[] cars = carService.getCars();

        int availableCarsCount = getAvailableCarsCount(bookings, cars, startDate, endDate);
        Car[] availableCars = new Car[availableCarsCount];

        int index = 0;
        for (Car car : cars) {
            if (isCarAvailableForPeriod(bookings, car, startDate, endDate)) {
                availableCars[index++] = car;
            }
        }
        return availableCars;
    }

    /**
     * @param startDate start of period
     * @param endDate   end of period
     * @return an array of the available electric cars for that period
     */
    public Car[] getAvailableElectricCars(LocalDate startDate, LocalDate endDate) {
        Car[] availableCars = getAvailableCars(startDate, endDate);

        int availableElectricCarCount = getElectricCarsCount(availableCars);
        Car[] availableElectricCars = new Car[availableElectricCarCount];

        int index = 0;
        for (Car car : availableCars) {
            if (car.isElectric()) {
                availableElectricCars[index++] = car;
            }
        }
        return availableElectricCars;
    }
}