package com.stela.car;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CarServiceTest {

    @Mock
    private CarDao carDao;
    @InjectMocks
    private CarService carService;

    private final UUID carId = UUID.randomUUID();
    private final Car mockCar = new Car(carId, "mockCar", BigDecimal.ONE, Brand.AUDI, true);

    @Test
    void shouldReturnCarWhenFindById() {
        // given
        when(carDao.findCarById(carId)).thenReturn(Optional.of(mockCar));

        // when
        Optional<Car> actual = carService.getCarById(carId);

        // then
        assertThat(actual).contains(mockCar);
    }

    @Test
    void shouldReturnOptionEmptyWhenNoSuchCar() {
        // given
        when(carDao.findCarById(carId)).thenReturn(Optional.empty());

        // when
        Optional<Car> actual = carService.getCarById(carId);

        // then
        assertThat(actual).isEmpty();
    }

    @Test
    void shouldReturnAllCarsWhenGetAllCars() {
        // given
        when(carDao.getCars()).thenReturn(List.of(mockCar, mockCar, mockCar));

        // when
        List<Car> actual = carService.getCars();

        // then
        assertThat(actual).containsExactly(mockCar, mockCar, mockCar);
    }

    @Test
    void shouldReturnEmptyListWhenNoCars() {
        // given
        when(carDao.getCars()).thenReturn(List.of());

        // when
        List<Car> actual = carService.getCars();

        // then
        assertThat(actual).isEmpty();
    }
}