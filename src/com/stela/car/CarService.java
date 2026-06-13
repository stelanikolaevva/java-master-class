package com.stela.car;

import java.util.Optional;
import java.util.UUID;

public class CarService {
    private final CarDao carDao = new CarDao();

    public Optional<Car> getCarById(UUID id) {
        return carDao.findCarById(id);
    }

    public Car[] getCars() {
        return carDao.getCars();
    }

}

