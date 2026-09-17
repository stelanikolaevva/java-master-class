package com.stela.car;

import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class CarService {
    private final CarRepository carRepository;

    public CarService(CarRepository carRepository) {
        this.carRepository = carRepository;
    }

    public Optional<Car> getCarById(UUID id) {
        return carRepository.findById(id);
    }

    public List<Car> getCars() {
        return carRepository.findAll();
    }
}

