package com.stela.car;

import java.util.UUID;

/**
 * Custom checked exception thrown when no such car is found
 */
public class CarNotFoundException extends Exception {

    public CarNotFoundException(String message, UUID carId) {
        super(String.format(message, carId.toString()));
    }

    public CarNotFoundException(String message) {
        super(String.format(message));
    }
}
