package com.stela.car;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CarDao {

    List<Car> getCars();

    Optional<Car> findCarById(UUID carId);
}
