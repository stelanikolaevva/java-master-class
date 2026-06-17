package com.stela;

import com.stela.booking.CarBooking;
import com.stela.booking.CarBookingDao;
import com.stela.booking.CarBookingFileDataAccessService;
import com.stela.booking.CarBookingService;
import com.stela.car.CarArrayDataAccessService;
import com.stela.car.CarDao;
import com.stela.car.CarService;
import com.stela.user.UserArrayDataAccessService;
import com.stela.user.UserDao;
import com.stela.user.UserNotFoundException;
import com.stela.user.UserService;

import java.nio.file.Path;
import java.util.Arrays;

import static com.stela.util.MenuUtil.goBack;
import static com.stela.util.MenuUtil.printMenuAndSelectOption;
import static com.stela.util.MenuUtil.readCarBookingRequestInput;
import static com.stela.util.MenuUtil.readUUIDOrGoBack;
import static com.stela.util.MenuUtil.selectOption;

//import com.stela.booking.CarBookingFileDataAccessService;

public class Main {
    //Users
    // 28d59322-2f01-4794-9f07-fae11a1a7a03 - Leo
    // a10309f4-186b-4b2c-abb6-93f9bee4dba1 - Michael
    // b0bd60d0-619a-4c40-9a1a-861ab568a6a6 - Amaya
    // 37f04d98-9c09-495b-870f-52b322b676dc - Boyan

    //Cars
    // d6992eaa-8724-4abe-a9fa-b12d4b04edcb - B 1310 CH
    // 0d80fa15-c780-49b0-bfc4-bf99212321f0 - CB 2231 TH
    // a63496dd-1ae6-46fd-bb05-072b3cd64fe5 - CB 3399 KH
    // 46e4d6b4-9f1f-4995-9aa2-621179eec17f - B 1122 OK


    private static final UserDao userDao = new UserArrayDataAccessService();
    private static final UserService userService = new UserService(userDao);

    private static final CarDao carDao = new CarArrayDataAccessService();
    private static final CarService carService = new CarService(carDao);

    private static final CarBookingDao carBookingDao = new CarBookingFileDataAccessService(Path.of("booking.dat"));
    // private static final CarBookingDao carBookingDao = new CarBookingArrayDataAccessService();
    private static final CarBookingService carBookingService = new CarBookingService(carBookingDao, carService, userService);


    public static void main(String[] args) {

        System.out.println("=== Welcome to Car Bookings ===");

        var option = printMenuAndSelectOption();
        while (option != 8) {
            boolean validOption = true;
            switch (option) {
                case 1 -> bookACar();
                case 2 -> deleteBooking();
                case 3 -> printUserBookings();
                case 4 -> printAllBookings();
                case 5 -> printAvailableCars();
                case 6 -> printAvailableElectricCars();
                case 7 -> printAllUsers();
                default -> {
                    System.out.println("Invalid option. Please try again:");
                    validOption = false;
                    option = selectOption();
                }
            }
            if (validOption) {
                goBack();
                option = printMenuAndSelectOption();
            }
        }
        System.out.println("Goodbye!");
    }

    private static void bookACar() {
        CarBooking bookingRequest = readCarBookingRequestInput();
        if (bookingRequest == null) {
            return;
        }

        var newBooking = carBookingService.bookCar(bookingRequest);
        System.out.println("Car Booking Added Successfully With Id: " + newBooking.getId());
    }

    private static void deleteBooking() {
        System.out.println("Enter Booking UUID or b to go back:");

        var bookingId = readUUIDOrGoBack();
        if (bookingId == null) {
            return;
        }

        var isDeleted = carBookingService.deleteBooking(bookingId);
        if (isDeleted) {
            System.out.println("Booking cancelled successfully!");
        } else {
            System.out.println("Booking cancellation failed!");
        }

    }


    private static void printUserBookings() {
        System.out.println("Enter User UUID or b to go back:");

        var userId = readUUIDOrGoBack();
        if (userId == null) {
            return;
        }

        var userById = userService.findUserById(userId).orElseThrow(() -> new UserNotFoundException("User not found!", userId));
        System.out.println("--- All cars booked by " + userById.getName() + " ---");

        var carsForSpecificUser = carBookingService.getCarsForSpecificUser(userId);
        if (carsForSpecificUser.length > 0) {
            System.out.println(Arrays.toString(carsForSpecificUser));
        } else {
            System.out.println("No cars present!");
        }

    }

    private static void printAllBookings() {
        System.out.println("--- All current bookings ---");

        var allBookings = carBookingService.getBookings();
        if (allBookings.length > 0) {
            System.out.println(Arrays.toString(allBookings));
        } else {
            System.out.println("No bookings up to this moment.");
        }
    }

    private static void printAvailableCars() {
        System.out.println("--- Available Cars ---");

        var cars = carBookingService.getAllAvailableCars();
        if (cars.length > 0) {
            System.out.println(Arrays.toString(cars));
        } else {
            System.out.println("No cars present!");
        }
    }

    private static void printAvailableElectricCars() {
        System.out.println("--- Available Electric Cars---");

        var availableElectricCars = carBookingService.getAvailableElectricCars();
        if (availableElectricCars.length > 0) {
            System.out.println(Arrays.toString(availableElectricCars));
        } else {
            System.out.println("No cars present!");
        }
    }

    private static void printAllUsers() {
        System.out.println("--- Users in system ---");

        var usersInSystem = userService.getAllUsers();
        if (usersInSystem.length > 0) {
            System.out.println(Arrays.toString(usersInSystem));
        } else {
            System.out.println("No users present!");
        }
    }
}