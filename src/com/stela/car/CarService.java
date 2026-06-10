package com.stela.car;

import java.util.UUID;

public class CarService {
    private final CarDao carDao = new CarDao();

    /**
     * @param id the car id
     * @return the car with selected id
     */
    public Car getCarById(UUID id) {
        Car car = carDao.findCarById(id);
        if (car == null) {
            throw new CarNotFoundException("Car with id %s not found", id);
        }
        return car;
    }

    /**
     * @return all cars
     */
    public Car[] getCars() {
        return carDao.getCars();
    }

    /**
     * @return an array of the available cars for that period
     */
    public Car[] getAvailableElectricCars() {
        Car[] cars = carDao.getCars();

        int electricCarsCount = getElectricCarsCount(cars);
        Car[] electricCars = new Car[electricCarsCount];

        int index = 0;
        for (Car car : cars) {
            if (car.isElectric()) {
                electricCars[index++] = car;
            }
        }
        return electricCars;
    }


    /**
     * used for array initialization
     *
     * @return - total available cars count
     */
    private int getElectricCarsCount(Car[] allCars) {
        int electricCarsCount = 0;

        for (Car car : allCars) {
            if (car.isElectric()) {
                electricCarsCount++;
            }
        }
        return electricCarsCount;
    }
}

