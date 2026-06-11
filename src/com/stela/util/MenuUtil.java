package com.stela.util;

import com.stela.booking.CarBooking;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Scanner;
import java.util.UUID;

public class MenuUtil {
    private static final Scanner SCANNER = new Scanner(System.in);

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
                    id = UUID.fromString(input);
                }
            } catch (IllegalArgumentException e) {
                System.out.println("Invalid UUID. Please try again or b to go back:");
            }
        }
        return id;
    }

    /**
     * @return Booking Data object with the inputs from the user or null if user backs out
     */
    public static CarBooking readCarBookingRequestInput() {
        System.out.println("Enter User UUID or b to go back:");
        var userID = readUUIDOrGoBack();
        if (userID == null) {
            return null;
        }

        System.out.println("Enter Car UUID or b to go back:");
        var carSelectionId = readUUIDOrGoBack();
        if (carSelectionId == null) {
            return null;
        }

        System.out.println("Enter start date (dd/MM/yyyy) or b to go back:");
        var startDate = readLocalDate();
        if (startDate == null) {
            return null;
        }

        System.out.println("Enter end date (dd/MM/yyyy) or b to go back:");
        var endDate = readLocalDate();
        if (endDate == null) {
            return null;
        }
        return new CarBooking(userID, carSelectionId, startDate, endDate);
    }

    /**
     * @return a future-or-today date, or null if the user entered 'b' to go back
     */
    public static LocalDate readLocalDate() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        LocalDate localDate = null;
        while (localDate == null) {
            try {
                String line = SCANNER.nextLine().trim();
                if ("b".equalsIgnoreCase(line)) break;
                else {
                    localDate = LocalDate.parse(line, formatter);
                    if (localDate.isBefore(LocalDate.now())) {
                        System.out.println("Date must not be in the past. Please try again:");
                        continue;
                    }
                    return localDate;
                }
            } catch (DateTimeParseException e) {
                System.out.println("Invalid date. Please try again:");
            }
        }
        return localDate;
    }

}
