package com.stela.booking;

import com.stela.car.Brand;
import com.stela.car.Car;
import com.stela.car.CarNotFoundException;
import com.stela.car.CarService;
import com.stela.user.User;
import com.stela.user.UserNotFoundException;
import com.stela.user.UserService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CarBookingServiceTest {

    @Mock
    private CarBookingDao carBookingDao;
    @Mock
    private CarService carService;
    @Mock
    private UserService userService;

    @InjectMocks
    private CarBookingService carBookingService;

    private final UUID carId = UUID.randomUUID();
    private final UUID userId = UUID.randomUUID();
    private final LocalDate startDate = LocalDate.now();
    private final LocalDate endDate = LocalDate.now().plusDays(1);
    private final CarBooking bookingRequest = new CarBooking(userId, carId, startDate, endDate);
    private final CarBooking existingBooking = new CarBooking(userId, carId, startDate, endDate, BigDecimal.ONE);


    @Test
    void shouldReturnAllBookings() {
        //given
        List<CarBooking> carBookings = new ArrayList<>();
        carBookings.add(existingBooking);

        when(carBookingDao.getBookings()).thenReturn(carBookings);

        //when
        List<CarBooking> actual = carBookingService.getBookings();

        //then
        assertThat(actual).containsExactlyElementsOf(carBookings);
    }

    @Test
    void shouldReturnEmptyListWhenNoBookings() {
        //given
        when(carBookingDao.getBookings()).thenReturn(new ArrayList<>());
        //when
        List<CarBooking> actual = carBookingService.getBookings();
        //then
        assertThat(actual).isEmpty();
    }

    @Test
    void shouldReturnTrueWhenDeletingBooking() {
        //given
        when(carBookingDao.deleteBooking(existingBooking.getId()))
                .thenReturn(true);

        //when
        boolean actual = carBookingService.deleteBooking(existingBooking.getId());

        //then
        assertThat(actual).isTrue();
    }

    @Test
    void shouldReturnFalseWhenDeletingUnknownBooking() {
        //given
        when(carBookingDao.deleteBooking(any())).thenReturn(false);

        //when
        boolean actual = carBookingService.deleteBooking(UUID.randomUUID());

        //then
        assertThat(actual).isFalse();
    }

    @Test
    void shouldReturnActiveBookingWhenCarAndUserAreValidForBooking() {
        // given
        BigDecimal pricePerDay = BigDecimal.valueOf(123.31);

        when(carBookingDao.getBookings()).thenReturn(List.of());
        when(userService.findUserById(userId)).thenReturn(Optional.of(
                new User(userId, "UserName")));
        when(carService.getCarById(carId)).thenReturn(Optional.of(
                new Car(carId, "RegNumber", pricePerDay, Brand.AUDI, true)));

        // when
        CarBooking actual = carBookingService.bookCar(bookingRequest);

        // then
        BigDecimal expectedPrice = pricePerDay.multiply(BigDecimal.valueOf(2));

        assertThat(actual.getUserId()).isEqualTo(userId);
        assertThat(actual.getCarId()).isEqualTo(carId);
        assertThat(actual.getStartDate()).isEqualTo(startDate);
        assertThat(actual.getEndDate()).isEqualTo(endDate);
        assertThat(actual.getStatus()).isEqualTo(BookingStatus.ACTIVE);
        assertThat(actual.getPrice()).isEqualByComparingTo(expectedPrice);

        ArgumentCaptor<CarBooking> captor = ArgumentCaptor.forClass(CarBooking.class);
        verify(carBookingDao, times(1)).saveBooking(captor.capture());

        CarBooking saved = captor.getValue();
        assertThat(saved.getStatus()).isEqualTo(BookingStatus.ACTIVE);
        assertThat(saved).isEqualTo(actual);
    }

    @Test
    void shouldAllowBookingWhenOverlappingBookingBelongsToDifferentCar() {
        // given
        UUID otherCarId = UUID.randomUUID();
        CarBooking otherCarBooking = new CarBooking(userId, otherCarId, startDate, endDate, BigDecimal.ONE);

        when(carBookingDao.getBookings()).thenReturn(List.of(otherCarBooking));
        when(userService.findUserById(userId)).thenReturn(Optional.of(new User(userId, "UserName")));
        when(carService.getCarById(carId)).thenReturn(Optional.of(
                new Car(carId, "RegNumber", BigDecimal.ONE, Brand.AUDI, true)));

        // when
        CarBooking actual = carBookingService.bookCar(bookingRequest);

        // then
        assertThat(actual.getCarId()).isEqualTo(carId);
        verify(carBookingDao, times(1)).saveBooking(any());
    }

    @Test
    void shouldAllowBookingWhenExistingBookingForSameCarIsCancelled() {
        // given
        CarBooking cancelledBooking = new CarBooking(userId, carId, startDate, endDate, BigDecimal.ONE);
        cancelledBooking.setStatus(BookingStatus.CANCELLED);

        when(carBookingDao.getBookings()).thenReturn(List.of(cancelledBooking));
        when(userService.findUserById(userId)).thenReturn(Optional.of(new User(userId, "UserName")));
        when(carService.getCarById(carId)).thenReturn(Optional.of(
                new Car(carId, "RegNumber", BigDecimal.ONE, Brand.AUDI, true)));

        // when
        CarBooking actual = carBookingService.bookCar(bookingRequest);

        // then
        assertThat(actual.getCarId()).isEqualTo(carId);
        verify(carBookingDao, times(1)).saveBooking(any());
    }

    @Test
    void shouldAllowBookingWhenExistingBookingForSameCarIsCompleted() {
        // given
        CarBooking cancelledBooking = new CarBooking(userId, carId, startDate, endDate, BigDecimal.ONE);
        cancelledBooking.setStatus(BookingStatus.COMPLETED);

        when(carBookingDao.getBookings()).thenReturn(List.of(cancelledBooking));
        when(userService.findUserById(userId)).thenReturn(Optional.of(new User(userId, "UserName")));
        when(carService.getCarById(carId)).thenReturn(Optional.of(
                new Car(carId, "RegNumber", BigDecimal.ONE, Brand.AUDI, true)));

        // when
        CarBooking actual = carBookingService.bookCar(bookingRequest);

        // then
        assertThat(actual.getCarId()).isEqualTo(carId);
        verify(carBookingDao, times(1)).saveBooking(any());
    }

    @Test
    void shouldTrowWhenNoExistingUserWhenBooking() {
        //given
        when(carBookingDao.getBookings()).thenReturn(new ArrayList<>());
        when(userService.findUserById(userId)).thenReturn(Optional.empty());

        //when + then
        assertThatThrownBy(() -> carBookingService.bookCar(bookingRequest))
                .isInstanceOf(UserNotFoundException.class)
                .hasMessage("User with id " + userId + " not found");

        verify(carService, never()).getCarById(any());
        verify(carBookingDao, never()).saveBooking(any());
    }

    @Test
    void shouldTrowWhenNoExistingCarWhenBooking() {
        //given
        when(carBookingDao.getBookings()).thenReturn(new ArrayList<>());
        when(userService.findUserById(userId)).thenReturn(Optional.of(
                new User(userId, "UserName")));
        when(carService.getCarById(carId)).thenReturn(Optional.empty());

        //when + then
        assertThatThrownBy(() -> carBookingService.bookCar(bookingRequest))
                .isInstanceOf(CarNotFoundException.class)
                .hasMessage("Car with id " + carId + " not found");

        verify(carBookingDao, never()).saveBooking(any());
    }

    @Test
    void shouldTrowWhenNoValidDatesWhenBooking() {
        // given
        CarBooking invalidBookingRequest = new CarBooking(userId, carId, startDate, endDate.minusDays(10));

        when(carBookingDao.getBookings()).thenReturn(List.of());
        when(userService.findUserById(userId)).thenReturn(Optional.of(
                new User(userId, "UserName")));
        when(carService.getCarById(carId)).thenReturn(Optional.of(
                new Car(carId, "RegNumber", BigDecimal.ONE, Brand.AUDI, true)));

        // when + then
        assertThatThrownBy(() -> carBookingService.bookCar(invalidBookingRequest))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Invalid date range!");
        verify(carBookingDao, never()).saveBooking(any());
    }

    @Test
    void shouldTrowWhenNoAvailableCarWhenBooking() {
        // given
        when(carBookingDao.getBookings()).thenReturn(List.of(existingBooking));
        when(userService.findUserById(userId)).thenReturn(Optional.of(new User(userId, "UserName")));
        when(carService.getCarById(carId)).thenReturn(Optional.of(
                new Car(carId, "RegNumber", BigDecimal.ONE, Brand.AUDI, true)));

        // when + then
        assertThatThrownBy(() -> carBookingService.bookCar(bookingRequest))
                .isInstanceOf(CarAlreadyBookedException.class)
                .hasMessage("Car with plates RegNumber already booked.");

        verify(carBookingDao, never()).saveBooking(any());
    }

    @Test
    void shouldReturnBookedCarsForSpecificUser() {
        // given
        Car mockCar = new Car(carId, "RegNumber", BigDecimal.ONE, Brand.AUDI, true);
        when(carBookingDao.getBookings()).thenReturn(List.of(existingBooking));
        when(carService.getCarById(carId)).thenReturn(Optional.of(mockCar));

        //when
        List<Car> carsForSpecificUser = carBookingService.getCarsForSpecificUser(userId);
        //then
        assertThat(carsForSpecificUser).containsExactly(mockCar);
    }

    @Test
    void shouldReturnEmptyListWhenThereAreNoBookings() {
        // given
        when(carBookingDao.getBookings()).thenReturn(new ArrayList<>());
        //when
        List<Car> carsForSpecificUser = carBookingService.getCarsForSpecificUser(userId);
        //then
        assertThat(carsForSpecificUser).isEmpty();
        verify(carService, never()).getCarById(carId);
    }

    @Test
    void shouldReturnEmptyListWhenUserHasNoBookings() {
        // given
        List<CarBooking> existingBookings = List.of(new CarBooking(UUID.randomUUID(),
                carId, startDate, endDate, BigDecimal.ONE));

        when(carBookingDao.getBookings()).thenReturn(existingBookings);
        //when
        List<Car> carsForSpecificUser = carBookingService.getCarsForSpecificUser(userId);
        //then
        assertThat(carsForSpecificUser).isEmpty();
        verify(carService, never()).getCarById(carId);
    }

    @Test
    void shouldSkipCarsThatNoLongerExistForSpecificUser() {
        // given
        when(carBookingDao.getBookings()).thenReturn(List.of(existingBooking));
        when(carService.getCarById(any())).thenReturn(Optional.empty());

        // when
        List<Car> actual = carBookingService.getCarsForSpecificUser(userId);

        // then
        assertThat(actual).isEmpty();
    }

    @Test
    void shouldReturnAllAvailableCars() {
        //given
        Car availablePetrolCar = new Car(UUID.randomUUID(), "REG1", BigDecimal.valueOf(50), Brand.TOYOTA, false);
        Car availableElectricCar = new Car(UUID.randomUUID(), "REG2", BigDecimal.valueOf(80), Brand.TESLA, true);
        Car bookedPetrolCar = new Car(UUID.randomUUID(), "REG3", BigDecimal.valueOf(60), Brand.AUDI, false);
        Car bookedElectricCar = new Car(UUID.randomUUID(), "REG4", BigDecimal.valueOf(60), Brand.AUDI, true);

        CarBooking activeBooking = new CarBooking(UUID.randomUUID(), bookedPetrolCar.getId(), LocalDate.now(), LocalDate.now().plusDays(1), BigDecimal.ONE);
        CarBooking activeBooking2 = new CarBooking(UUID.randomUUID(), bookedElectricCar.getId(), LocalDate.now(), LocalDate.now().plusDays(1), BigDecimal.ONE);

        when(carService.getCars()).thenReturn(List.of(availablePetrolCar, availableElectricCar, bookedElectricCar, bookedPetrolCar));
        when(carBookingDao.getBookings()).thenReturn(List.of(activeBooking, activeBooking2));

        //when
        List<Car> actual = carBookingService.getAllAvailableCars();

        //then
        assertThat(actual).containsExactly(availablePetrolCar, availableElectricCar);
    }

    @Test
    void shouldTreatCarAsAvailableWhenOnlyBookingIsCompleted() {
        // given
        Car car = new Car(UUID.randomUUID(), "REG1", BigDecimal.valueOf(50), Brand.TOYOTA, false);
        CarBooking completedBooking = new CarBooking(UUID.randomUUID(), car.getId(),
                LocalDate.now().minusDays(5), LocalDate.now().minusDays(1), BigDecimal.ONE);
        completedBooking.setStatus(BookingStatus.COMPLETED);

        when(carBookingDao.getBookings()).thenReturn(List.of(completedBooking));
        when(carService.getCars()).thenReturn(List.of(car));

        // when
        List<Car> actual = carBookingService.getAllAvailableCars();

        // then
        assertThat(actual).containsExactly(car);
    }

    @Test
    void shouldReturnAllElectricCars() {
        //given
        Car availablePetrolCar = new Car(UUID.randomUUID(), "REG1", BigDecimal.valueOf(50), Brand.TOYOTA, false);
        Car availableElectricCar = new Car(UUID.randomUUID(), "REG2", BigDecimal.valueOf(80), Brand.TESLA, true);
        Car bookedPetrolCar = new Car(UUID.randomUUID(), "REG3", BigDecimal.valueOf(60), Brand.AUDI, false);
        Car bookedElectricCar = new Car(UUID.randomUUID(), "REG4", BigDecimal.valueOf(60), Brand.AUDI, true);

        CarBooking activeBooking = new CarBooking(UUID.randomUUID(), bookedPetrolCar.getId(), LocalDate.now(), LocalDate.now().plusDays(1), BigDecimal.ONE);
        CarBooking activeBooking2 = new CarBooking(UUID.randomUUID(), bookedElectricCar.getId(), LocalDate.now(), LocalDate.now().plusDays(1), BigDecimal.ONE);

        when(carBookingDao.getBookings()).thenReturn(List.of(activeBooking, activeBooking2));
        when(carService.getCars()).thenReturn(List.of(availablePetrolCar, availableElectricCar, bookedElectricCar, bookedPetrolCar));

        //when
        List<Car> actual = carBookingService.getAvailableElectricCars();

        //then
        assertThat(actual).containsExactly(availableElectricCar);
    }
}
