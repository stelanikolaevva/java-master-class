package com.stela.car;

public enum Brand {
    TESLA("Tesla"), AUDI("Audi"), MERCEDES("Mercedes"), TOYOTA("Toyota");

    private final String brand;

    Brand(String brand) {
        this.brand = brand;
    }

    public String getBrand() {
        return this.brand;
    }
}
