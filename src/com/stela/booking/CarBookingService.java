package com.stela.booking;

import com.stela.car.Car;
import com.stela.car.CarAlreadyBookedException;
import com.stela.car.CarNotFoundException;
import com.stela.user.User;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

import static com.stela.util.ArraysUtil.getUserCarsCount;
import static com.stela.util.DatesUtil.isCarAvailableForPeriod;


public class CarBookingService {
    private final CarBookingDao carBookingDao = new CarBookingDao();

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
        CarBooking booking = carBookingDao.findBookingById(bookingId);
        carBookingDao.deleteBooking(booking);
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
}





