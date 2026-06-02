package com.stela.car;

import java.math.BigDecimal;
import java.util.UUID;

public class CarDao {
    private static final Car[] cars;

    //Pre-Seed data
    static {
        cars = new Car[]{
                new Car(UUID.fromString("d6992eaa-8724-4abe-a9fa-b12d4b04edcb"),
                        "B 1310 CH",
                        new BigDecimal("23.99"),
                        Brand.MERCEDES,
                        false),
                new Car(UUID.fromString("0d80fa15-c780-49b0-bfc4-bf99212321f0"),
                        "CB 2231 TH",
                        new BigDecimal("33.99"),
                        Brand.MERCEDES,
                        true),
                new Car(UUID.fromString("a63496dd-1ae6-46fd-bb05-072b3cd64fe5"),
                        "CB 3399 KH",
                        new BigDecimal("25.99"),
                        Brand.TOYOTA,
                        true),
                new Car(UUID.fromString("46e4d6b4-9f1f-4995-9aa2-621179eec17f"),
                        "B 1122 OK",
                        new BigDecimal("27.99"),
                        Brand.AUDI,
                        false)
        };
    }

    /**
     * @return all cars in the system
     */
    public Car[] getCars() {
        return cars;
    }

    /**
     * @param id - the id of the car
     * @return the car for the selected id
     * @throws CarNotFoundException - if the car is not found
     */
    public Car findCarById(UUID id) throws CarNotFoundException {
        for (Car car : cars) {
            if (car.getId().equals(id)) {
                return car;
            }
        }
        throw new CarNotFoundException("Car with id %s not found", id);

    }


}
