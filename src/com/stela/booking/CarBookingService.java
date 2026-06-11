package com.stela.booking;

import com.stela.car.Car;
import com.stela.car.CarService;
import com.stela.user.User;
import com.stela.user.UserService;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

import static com.stela.util.DatesUtil.areDatesValid;

public class CarBookingService {
    private final CarBookingDao carBookingDao = new CarBookingDao();
    private final CarService carService = new CarService();
    private final UserService userService = new UserService();

    /**
     * @return all bookings
     */
    public CarBooking[] getBookings() {
        return carBookingDao.getBookings();
    }

    /**
     * @param bookingId of the booking that is getting canceled
     * @return true if the booking is successfully canceled
     */
    public boolean deleteBooking(UUID bookingId) {
        return carBookingDao.deleteBooking(bookingId);
    }


    /**
     * @param bookingRequest the requested user, car and date range
     * @return the saved booking
     */
    public CarBooking bookCar(CarBooking bookingRequest) {
        CarBooking[] bookings = carBookingDao.getBookings();

        User user = userService.findUserById(bookingRequest.getUserId());
        Car car = carService.getCarById(bookingRequest.getCarId());

        if (!areDatesValid(bookingRequest.getStartDate(), bookingRequest.getEndDate())) {
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


    /**
     * @param userId - the selected userId
     * @return all his booked cars
     */
    public Car[] getCarsForSpecificUser(UUID userId) {
        CarBooking[] bookings = carBookingDao.getBookings();
        int userCarsCount = getUserCarsCount(bookings, userId);
        Car[] carsForUser = new Car[userCarsCount];

        int index = 0;
        for (CarBooking carBooking : bookings) {
            if (carBooking.getUserId().equals(userId)) {
                carsForUser[index++] = carService.getCarById(carBooking.getCarId());
            }
        }
        return carsForUser;
    }

    /**
     * @return all cars that are not booked
     */
    public Car[] getAllAvailableCars() {
        return getAvailableCars(false);
    }

    /**
     * @return all electric cars that are not booked
     */
    public Car[] getAvailableElectricCars() {
        return getAvailableCars(true);
    }

    /**
     * @param allBookings all present car bookings
     * @param carId         - the carId we search for
     * @param startDate   - the start of the period for which we should look for availability
     * @param endDate     - the end of the period
     * @return if the car is not already booked for this time
     */
    private boolean isCarAvailableForPeriod(CarBooking[] allBookings, UUID carId, LocalDate startDate, LocalDate endDate) {
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


    /**
     * used for array initialization
     *
     * @param bookings - all car bookings
     * @param userId   - user which car we should search for
     * @return - the number of cars he had booked
     */
    private int getUserCarsCount(CarBooking[] bookings, UUID userId) {
        int count = 0;
        for (CarBooking b : bookings) {
            if (b.getUserId().equals(userId)) {
                count++;
            }
        }
        return count;
    }

    /**
     * @param isElectricOnly if true, only electric cars are considered
     * @return cars that have no active booking
     */
    private Car[] getAvailableCars(boolean isElectricOnly) {
        Car[] cars = carService.getCars();
        CarBooking[] carBookings = carBookingDao.getBookings();

        int carsCount = getCarsCount(carBookings, cars, isElectricOnly);
        Car[] availableCars = new Car[carsCount];

        int i = 0;
        for (Car car : cars) {
            if (isElectricOnly && !car.isElectric()) continue;
            if (hasNoActiveBooking(carBookings, car.getId())) availableCars[i++] = car;
        }
        return availableCars;
    }


    /**
     * used for array initialization
     *
     * @return - total available cars count
     */
    private int getCarsCount(CarBooking[] bookings, Car[] cars, boolean isElectricOnly) {
        int count = 0;
        for (Car car : cars) {
            if (isElectricOnly && !car.isElectric()) continue;
            if (hasNoActiveBooking(bookings, car.getId())) count++;
        }
        return count;
    }

    /**
     * @param bookings - all booking
     * @param carId      - the car to check
     * @return - if the car has no active bookings
     */
    private boolean hasNoActiveBooking(CarBooking[] bookings, UUID carId) {
        for (CarBooking b : bookings) {
            if (b.getCarId().equals(carId) && b.getStatus() == BookingStatus.ACTIVE) {
                return false;
            }
        }
        return true;
    }
}