package com.stela.util;

import com.stela.booking.CarBooking;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Scanner;
import java.util.UUID;

public class MenuUtil {
    private static final Scanner SCANNER = new Scanner (System.in);

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
     *
     * @return selected number option from the user
     */
    public static int printMenuAndSelectOption() {
        System.out.println(MENU);
        return selectOption();
    }

    /**
     * @return selected number option from the user
     */
    public static int selectOption() {
        while (true) {
            String input = SCANNER.nextLine().trim();
            try {
                return Integer.parseInt(input);
            } catch (NumberFormatException e) {
                System.out.println("Invalid option. Please try again:");
            }
        }
    }

    /**
     * Waits for the user to select B or b to go back to main menu
     */
    public static void goBack() {
        //Option to go back to main menu
        System.out.println("Press b to go back to main menu:");
        while (true) {
            var input = SCANNER.nextLine().trim();
            if (input.equalsIgnoreCase("b")) {
                break;
            }
        }
    }

    /**
     * @return a valid UUID inputted from the user or null if the user quit
     */
    public static UUID readUUIDOrGoBack() {
        UUID id = null;
        while (id == null) {
            try {
                var input = SCANNER.nextLine().trim();
                if ("b".equalsIgnoreCase(input)) break;
                else {
                    id = UUID.fromString(input.trim());
                }
            } catch (IllegalArgumentException e) {
                System.out.println("Invalid UUID. Please try again or b to go back:");
            }
        }
        return id;
    }

    /**
     * @return Booking Data object with the inputs from the user
     */
    public static CarBooking readCarBookingRequestInput() {
        System.out.println("Enter User UUID or q to quit:");
        var userID = readUUIDOrGoBack();

        System.out.println("Enter Car UUID or q to quit:");
        var carSelection = readUUIDOrGoBack();

        System.out.println("Enter start date (dd/MM/yyyy):");
        var startDate = readLocalDate();

        System.out.println("Enter end date (dd/MM/yyyy):");
        var endDate = readLocalDate();
        return new CarBooking(userID, carSelection, startDate, endDate);
    }

    /**
     * @return a valid LocalDate inputted from the user
     */
    public static LocalDate readLocalDate() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

        while (true) {
            try {
                String line = SCANNER.nextLine().trim();
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
