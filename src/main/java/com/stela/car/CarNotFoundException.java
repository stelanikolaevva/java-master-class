package com.stela.car;

import java.util.UUID;

public class CarNotFoundException extends RuntimeException {

    public CarNotFoundException(String message, UUID carId) {
        super(String.format(message, carId));
    }
}
