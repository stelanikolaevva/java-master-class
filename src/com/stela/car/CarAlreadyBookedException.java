package com.stela.car;

/**
 * Custom checked exception thrown when the car is already book for the desired dates
 */
public class CarAlreadyBookedException extends RuntimeException {

    public CarAlreadyBookedException(String message, String regNumber) {
        super(String.format(message, regNumber));
    }
}
