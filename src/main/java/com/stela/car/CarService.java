package com.stela.car;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class CarService {
    private final CarDao carDao;

    public CarService(CarDao carDao) {
        this.carDao = carDao;
    }

    public Optional<Car> getCarById(UUID id) {
        return carDao.findCarById(id);
    }

    public List<Car> getCars() {
        return carDao.getCars();
    }
}

