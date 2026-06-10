package com.stela.booking;

import java.util.UUID;

/**
 * Custom unchecked exception when the booking is not found
 */
public class BookingNotFoundException extends RuntimeException {

    public BookingNotFoundException(String message, UUID id) {
        super(String.format(message, id));
    }
}
