package com.stela.car;

import java.util.Optional;
import java.util.UUID;

public class CarService {
    private final CarDao carDao = new CarDao();

    /**
     * @param id the car id
     * @return the car with selected id
     */
    public Car getCarById(UUID id) {
        Optional<Car> car = carDao.findCarById(id);
        if (car.isEmpty()) {
            throw new CarNotFoundException("Car with id %s not found", id);
        }
        return car.get();
    }

    /**
     * @return all cars
     */
    public Car[] getCars() {
        return carDao.getCars();
    }

}

