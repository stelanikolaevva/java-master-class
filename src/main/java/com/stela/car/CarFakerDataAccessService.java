package com.stela.car;

import com.github.javafaker.Faker;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.UUID;
import java.util.stream.IntStream;

public class CarFakerDataAccessService implements CarDao {
    private static final int CAR_COUNT = 20;

    private final List<Car> cars;

    public CarFakerDataAccessService() {
        Faker faker = new Faker();
        Random random = new Random();
        Brand[] brands = Brand.values();

        this.cars = IntStream.range(0, CAR_COUNT)
                .mapToObj(i -> new Car(UUID.randomUUID(),
                        faker.lorem().word().toUpperCase(),
                        BigDecimal.valueOf(faker.number().randomDouble(2, 20, 300)),
                        brands[random.nextInt(brands.length)],
                        random.nextBoolean()))
                .toList();
    }

    @Override
    public List<Car> getCars() {
       return cars;
    }

    @Override
    public Optional<Car> findCarById(UUID carId) {
        return cars.stream()
                .filter(car -> car.getId().equals(carId))
                .findFirst();
    }
}
