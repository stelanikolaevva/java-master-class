package com.stela.car;

import java.util.Optional;
import java.util.UUID;

public interface CarDao {

    Car[] getCars();

    Optional<Car> findCarById(UUID carId);
}
