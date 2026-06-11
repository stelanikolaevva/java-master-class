package com.stela.booking;

/**
 * Custom unchecked exception thrown when the car is already booked
 */
public class CarAlreadyBookedException extends RuntimeException {

    public CarAlreadyBookedException(String message, String regNumber) {
        super(String.format(message, regNumber));
    }
}
