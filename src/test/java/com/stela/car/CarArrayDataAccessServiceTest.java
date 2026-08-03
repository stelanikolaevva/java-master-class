package com.stela.car;

import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class CarArrayDataAccessServiceTest {

    private final CarArrayDataAccessService carDao = new CarArrayDataAccessService();

    @Test
    void shouldReturnAllPreSeededCars() {
        // when
        List<Car> actual = carDao.getCars();

        // then
        assertThat(actual)
                .hasSize(4)
                .extracting(Car::getRegNumber)
                .containsExactlyInAnyOrder("B 1310 CH", "CB 2231 TH", "CB 3399 KH", "B 1122 OK");
    }

    @Test
    void shouldFindCarByIdWhenCarExists() {
        // given
        UUID existingCarId = UUID.fromString("d6992eaa-8724-4abe-a9fa-b12d4b04edcb");

        // when
        Optional<Car> actual = carDao.findCarById(existingCarId);

        // then
        assertThat(actual)
                .isPresent()
                .get()
                .extracting(Car::getRegNumber)
                .isEqualTo("B 1310 CH");
    }

    @Test
    void shouldReturnEmptyOptionalWhenCarIdIsUnknown() {
        // when
        Optional<Car> actual = carDao.findCarById(UUID.randomUUID());

        // then
        assertThat(actual).isEmpty();
    }
}