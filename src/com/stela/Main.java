package com.stela;

import com.stela.booking.BookingData;
import com.stela.booking.BookingNotFoundException;
import com.stela.booking.CarBookingService;
import com.stela.car.Car;
import com.stela.car.CarAlreadyBookedException;
import com.stela.car.CarNotFoundException;
import com.stela.car.CarService;
import com.stela.user.User;
import com.stela.user.UserNotFoundException;
import com.stela.user.UserService;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Scanner;

import static com.stela.util.ArraysUtil.printFormatedArrayOutput;
import static com.stela.util.DatesUtil.isStartDateAfterEndDate;
import static com.stela.util.MenuUtil.*;

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


    private static final UserService userService = new UserService();
    private static final CarService carService = new CarService();
    private static final CarBookingService carBookingService = new CarBookingService();

    public static void main(String[] args) {

        var scanner = new Scanner(System.in);
        System.out.println("=== Welcome to Car Bookings ===");

        var option = printMenuAndSelectOption(scanner);
        while (option != 8) {
            boolean validOption = true;
            switch (option) {
                case 1 -> bookACar(scanner);
                case 2 -> deleteBooking(scanner);
                case 3 -> printUserBookings(scanner);
                case 4 -> printAllBookings();
                case 5 -> printAvailableCars(scanner);
                case 6 -> printAvailableElectricCars(scanner);
                case 7 -> printAllUsers();  //done
                default -> {
                    System.out.println("Invalid option. Please try again:");
                    validOption = false;
                    option = selectOption(scanner);
                }
            }
            if (validOption) {
                goBack(scanner);
                option = printMenuAndSelectOption(scanner);
            }
        }
        System.out.println("Goodbye!");
    }

    private static void bookACar(Scanner scanner) {
        BookingData bookingData = readBookingDataInput(scanner);

        try {
            if (isStartDateAfterEndDate(bookingData.getStartDate(), bookingData.getEndDate())) {
                throw new IllegalArgumentException("End date must be after start date!");
            }

            User user = userService.findUserById(bookingData.getUserId());
            Car car = carService.getCarById(bookingData.getCarId());

            carBookingService.bookCar(user, car, bookingData.getStartDate(), bookingData.getEndDate());
        } catch (UserNotFoundException | CarNotFoundException | IllegalArgumentException
                 | CarAlreadyBookedException e) {
            System.out.println(e.getMessage());
            return;
        }
        System.out.println("Car Booking Added Successfully!");

    }

    private static void deleteBooking(Scanner scanner) {
        System.out.println("Enter Booking UUID or q to stop:");
        var bookingId = readUUIDOrQuit(scanner);
        if (bookingId != null) {
            try {
                carBookingService.deleteBooking(bookingId);
                System.out.println("Booking cancelled successfully!");
            } catch (BookingNotFoundException e) {
                System.out.println(e.getMessage());
            }
        }
    }

    private static void printUserBookings(Scanner scanner) {
        System.out.println("Enter User UUID or q to stop:");
        var userId = readUUIDOrQuit(scanner);
        if (userId != null) {
            try {
                User userById = userService.findUserById(userId);
                System.out.println("--- All cars booked by " + userById.getName() + " ---");

                var carsForSpecificUser = carBookingService.getCarsForSpecificUser(userId);
                printFormatedArrayOutput(Arrays.toString(carsForSpecificUser));
            } catch (UserNotFoundException | CarNotFoundException e) {
                System.out.println(e.getMessage());
            }
        }
    }

    private static void printAllBookings() {
        System.out.println("--- All current bookings ---");

        var allBookings = carBookingService.getBookings();
        if (allBookings.length > 0) {
            printFormatedArrayOutput(Arrays.toString(allBookings));
        } else {
            System.out.println("No bookings up to this moment.");
        }
    }

    private static void printAvailableCars(Scanner scanner) {
        System.out.println("--- Available Cars For Selected Period ---");

        System.out.println("Please select the start date (dd/mm/yyyy) for the period you are searching for:");
        LocalDate startDate = readLocalDate(scanner);

        System.out.println("Please select the end date (dd/mm/yyyy) for the period you are searching for:");
        LocalDate endDate = readLocalDate(scanner);

        printFormatedArrayOutput(Arrays.toString(carBookingService.getAvailableCars(startDate, endDate)));
    }


    private static void printAvailableElectricCars(Scanner scanner) {
        System.out.println("--- Available Electric Cars For Selected Period ---");

        System.out.println("Please select the start date (dd/mm/yyyy) for the period you are searching for:");
        LocalDate startDate = readLocalDate(scanner);

        System.out.println("Please select the end date (dd/mm/yyyy) for the period you are searching for:");
        LocalDate endDate = readLocalDate(scanner);

        printFormatedArrayOutput(Arrays.toString(carBookingService.getAvailableElectricCars(startDate, endDate)));
    }

    private static void printAllUsers() {
        System.out.println("--- Users in system ---");

        var usersInSystem = userService.getAllUsers();
        if (usersInSystem.length > 0) {
            printFormatedArrayOutput(Arrays.toString(usersInSystem));
        } else {
            System.out.println("No users present!");
        }
    }
}