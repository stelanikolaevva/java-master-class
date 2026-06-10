package com.stela.booking;

import com.stela.car.Car;
import com.stela.car.CarAlreadyBookedException;
import com.stela.car.CarService;
import com.stela.user.User;
import com.stela.user.UserService;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

import static com.stela.util.DatesUtil.isStartDateAfterEndDate;

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
     */
    public boolean deleteBooking(UUID bookingId) {
        CarBooking bookingById = carBookingDao.findBookingById(bookingId);

        if (bookingById == null) {
            throw new BookingNotFoundException("Booking with id %s not found", bookingId);
        }

        carBookingDao.deleteBooking(bookingById);
        return true;
    }


    public UUID bookCar(CarBooking bookingRequest) {
        CarBooking[] bookings = carBookingDao.getBookings();

        if (isStartDateAfterEndDate(bookingRequest.getStartDate(), bookingRequest.getEndDate())) {
            throw new IllegalArgumentException("End date must be after start date!");
        }

        User user = userService.findUserById(bookingRequest.getUserId());
        Car car = carService.getCarById(bookingRequest.getCarId());

        if (!isCarAvailableForPeriod(bookings, car, bookingRequest.getStartDate(), bookingRequest.getEndDate())) {
            throw new CarAlreadyBookedException("Car with plates %s already booked.", car.getRegNumber());
        }

        //Calculate the price for the days the car will be rented.
        BigDecimal daysRented = BigDecimal.valueOf(ChronoUnit.DAYS.between(bookingRequest.getStartDate(), bookingRequest.getEndDate()) + 1);
        BigDecimal totalCostForCar = car.getRentalPricePerDay().multiply(daysRented);

        CarBooking carBooking = new CarBooking(user.getId(), car.getId(), bookingRequest.getStartDate(), bookingRequest.getEndDate(), totalCostForCar);
        carBookingDao.saveBooking(carBooking);

        return carBooking.getId();
    }


    /**
     * @param user the selected user
     * @return all his booked cars
     */
    public Car[] getCarsForSpecificUser(User user) {
        int userCarsCount = getUserCarsCount(carBookingDao.getBookings(), user.getId());
        Car[] carsForUser = new Car[userCarsCount];

        int index = 0;
        for (CarBooking carBooking : carBookingDao.getBookings()) {
            if (carBooking.getUserId().equals(user.getId())) {
                carsForUser[index++] = carService.getCarById(carBooking.getCarId());
            }
        }
        return carsForUser;
    }


    /**
     * @param allBookings all present car bookings
     * @param car         - the car we search for
     * @param startDate   - the start of the period for which we should look for availability
     * @param endDate     - the end of the period
     * @return if the car is not already booked for this time
     */
    private boolean isCarAvailableForPeriod(CarBooking[] allBookings, Car car, LocalDate startDate, LocalDate endDate) {
        for (CarBooking booking : allBookings) {
            if (booking.getCarId().equals(car.getId()) && booking.getStatus().equals(BookingStatus.ACTIVE)) {

                //we already are checking if the end date is after start date and that start day is after today
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
}