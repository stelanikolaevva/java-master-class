package com.stela.car;

import java.util.UUID;

/**
 * Custom unchecked exception thrown when no such car is found
 */
public class CarNotFoundException extends RuntimeException {

    public CarNotFoundException(String message, UUID carId) {
        super(String.format(message, carId));
    }
}
