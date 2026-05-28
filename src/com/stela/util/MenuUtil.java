package com.stela.util;

import com.stela.booking.BookingData;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Scanner;
import java.util.UUID;

public class MenuUtil {
    public final static int MAX_INDEX = 16;

    private final static String MENU = """
            1 - Book Car
            2 - Delete Booking
            3 - View All User Booked Cars
            4 - View All Bookings
            5 - View Available Cars
            6 - View Available Electric Cars
            7 - View All Users
            8 - Exit
            Please select an option to continue:
            """;

    /**
     * Print the menu and returns the option
     * @param scanner the scanner instance used to capture user input
     * @return selected number option from the user
     */
    public static int printMenuAndSelectOption(Scanner scanner) {
        System.out.println(MENU);
        return selectOption(scanner);
    }

    /**
     * @param scanner the scanner instance used to capture user input
     * @return selected number option from the user
     */
    public static int selectOption(Scanner scanner) {
        while (true) {
            String input = scanner.nextLine().trim();
            try {
                return Integer.parseInt(input);
            } catch (NumberFormatException e) {
                System.out.println("Invalid option. Please try again:");
            }
        }
    }

    /**
     * Waits for the user to select B or b to go back to main menu
     * @param scanner the scanner instance used to capture user input
     */
    public static void goBack(Scanner scanner) {
        //Option to go back to main menu
        System.out.println("Press b to go back to main menu:");
        while (true) {
            var input = scanner.nextLine().trim();
            if (input.equalsIgnoreCase("b")) {
                break;
            }
        }
    }

    /**
     * @param scanner the scanner instance used to capture user input
     * @return a valid UUID inputted from the user or null if the user quit
     */
    public static UUID readUUIDOrQuit(Scanner scanner) {
        UUID id = null;
        while (id == null) {
            try {
                var input = scanner.nextLine().trim();
                if ("q".equalsIgnoreCase(input)) break;
                else {
                    id = UUID.fromString(input.trim());
                }
            } catch (IllegalArgumentException e) {
                System.out.println("Invalid UUID. Please try again or q to quit:");
            }
        }
        return id;
    }

    /**
     * @param scanner the scanner instance used to capture user input
     * @return Booking Data object with the inputs from the user
     */
    public static BookingData readBookingDataInput(Scanner scanner) {
        System.out.println("Enter User UUID or q to quit:");
        var userID = readUUIDOrQuit(scanner);

        System.out.println("Enter Car UUID or q to quit:");
        var carSelection = readUUIDOrQuit(scanner);

        System.out.println("Enter start date (dd/MM/yyyy):");
        var startDate = readLocalDate(scanner);

        System.out.println("Enter end date (dd/MM/yyyy):");
        var endDate = readLocalDate(scanner);
        return new BookingData(userID, carSelection, startDate, endDate);
    }

    /**
     * @param scanner the scanner instance used to capture user input
     * @return a valid LocalDate inputted from the user
     */
    public static LocalDate readLocalDate(Scanner scanner) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

        while (true) {
            try {
                String line = scanner.nextLine().trim();
                LocalDate date = LocalDate.parse(line, formatter);
                if (date.isBefore(LocalDate.now())) {
                    System.out.println("Date must be in the future. Please try again:");
                    continue;
                }
                return date;
            } catch (DateTimeParseException e) {
                System.out.println("Invalid date. Please try again:");
            }
        }
    }

}
