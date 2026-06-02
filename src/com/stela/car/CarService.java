package com.stela.car;

import java.util.UUID;

public class CarService {
    private final CarDao carDao = new CarDao();

    /**
     * @param id the car id
     * @return the car with selected id
     * @throws CarNotFoundException - if the ar is not found
     */
    public Car getCarById(UUID id) throws CarNotFoundException {
        return carDao.findCarById(id);
    }

    /**
     * @return all cars
     */
    public Car[] getCars() {
        return carDao.getCars();
    }
}
