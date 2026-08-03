package com.stela.booking;

public class CarAlreadyBookedException extends RuntimeException {

    public CarAlreadyBookedException(String message, String regNumber) {
        super(String.format(message, regNumber));
    }
}
