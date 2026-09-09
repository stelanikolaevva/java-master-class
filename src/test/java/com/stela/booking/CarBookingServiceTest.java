package com.stela.booking;

import com.stela.car.Car;
import com.stela.car.CarNotFoundException;
import com.stela.car.CarService;
import com.stela.user.AppUser;
import com.stela.user.AppUserNotFoundException;
import com.stela.user.AppUserService;
import com.stela.util.MockDataUtil;
import org.junit.jupiter.api.BeforeEach;
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

    public static final String AVAILABLE_PETROL_CAR = "d40d949f-c13c-43e6-af6d-d6d235833d9f";
    public static final String AVAILABLE_ELECTRIC_CAR = "c7664df0-6fa6-4b38-8185-cb14fe061556";
    public static final String CAR_FROM_COMPLETED_BOOKING = "d40d949f-c13c-43e6-af6d-d6d235833d9f";
    @Mock
    private CarBookingRepository carBookingDao;
    @Mock
    private CarService carService;
    @Mock
    private AppUserService appUserService;

    @InjectMocks
    private CarBookingService carBookingService;

    private AppUser appUser;
    private Car car;
    private CarBookingRequest mockRequest;

    @BeforeEach
    void setUp() {
        appUser = MockDataUtil.getUsers().getFirst();
        car = MockDataUtil.getCars().getFirst();

        mockRequest = new CarBookingRequest(appUser.getId(), car.getId(),
                LocalDate.of(2026, 12, 12),
                LocalDate.of(2026, 12, 13));
    }


    @Test
    void shouldReturnAllBookings() {
        //given
        List<CarBooking> carBookings = MockDataUtil.getCarBookings();

        when(carBookingDao.findAll()).thenReturn(carBookings);

        //when
        List<CarBooking> actual = carBookingService.getBookings();

        //then
        assertThat(actual).containsExactlyElementsOf(carBookings);
    }

    @Test
    void shouldReturnEmptyListWhenNoBookings() {
        //given
        when(carBookingDao.findAll()).thenReturn(new ArrayList<>());
        //when
        List<CarBooking> actual = carBookingService.getBookings();
        //then
        assertThat(actual).isEmpty();
    }

    @Test
    void shouldCallDeleteWhenDeletingBooking() {
        UUID bookingId = UUID.randomUUID();
        //when
        carBookingService.deleteBooking(bookingId);

        //then
        verify(carBookingDao, times(1)).deleteById(bookingId);
    }

    @Test
    void shouldReturnActiveBookingWhenCarAndUserAreValidForBooking() {
        // given
        BigDecimal pricePerDay = BigDecimal.valueOf(23.99);

        when(carBookingDao.findAll()).thenReturn(List.of());
        when(appUserService.findUserById(appUser.getId())).thenReturn(Optional.of(appUser));
        when(carService.getCarById(car.getId())).thenReturn(Optional.of(car));

        // when
        CarBooking actual = carBookingService.bookCar(mockRequest);

        // then
        BigDecimal expectedPrice = pricePerDay.multiply(BigDecimal.valueOf(2));

        assertThat(actual.getStartDate()).isEqualTo(LocalDate.of(2026, 12, 12));
        assertThat(actual.getEndDate()).isEqualTo(LocalDate.of(2026, 12, 13));
        assertThat(actual.getStatus()).isEqualTo(BookingStatus.ACTIVE);
        assertThat(actual.getPrice()).isEqualByComparingTo(expectedPrice);

        ArgumentCaptor<CarBooking> captor = ArgumentCaptor.forClass(CarBooking.class);
        verify(carBookingDao, times(1)).save(captor.capture());

        CarBooking saved = captor.getValue();
        assertThat(saved.getStatus()).isEqualTo(BookingStatus.ACTIVE);
    }

    @Test
    void shouldAllowBookingWhenExistingBookingForSameCarIsCancelled() {
        // given
        CarBooking cancelledBooking = MockDataUtil.getCancelledBookingForUser(appUser.getId());

        when(carBookingDao.findAll()).thenReturn(List.of(cancelledBooking));
        when(appUserService.findUserById(appUser.getId())).thenReturn(Optional.of(appUser));
        when(carService.getCarById(car.getId())).thenReturn(Optional.of(car));

        // when
        CarBooking actual = carBookingService.bookCar(mockRequest);

        // then
        assertThat(actual.getCar().getId()).isEqualTo(car.getId());
        verify(carBookingDao, times(1)).save(any());
    }

    @Test
    void shouldAllowBookingWhenExistingBookingForSameCarIsCompleted() {
        // given
        CarBooking cancelledBooking = MockDataUtil.getCompletedBookingForUser(appUser.getId());

        when(carBookingDao.findAll()).thenReturn(List.of(cancelledBooking));
        when(appUserService.findUserById(appUser.getId())).thenReturn(Optional.of(appUser));
        when(carService.getCarById(car.getId())).thenReturn(Optional.of(car));

        // when
        CarBooking actual = carBookingService.bookCar(mockRequest);

        // then
        assertThat(actual.getCar().getId()).isEqualTo(car.getId());
        verify(carBookingDao, times(1)).save(any());
    }

    @Test
    void shouldThrowWhenNoExistingUserWhenBooking() {
        //given
        when(carBookingDao.findAll()).thenReturn(new ArrayList<>());
        when(appUserService.findUserById(appUser.getId())).thenReturn(Optional.empty());

        //when + then
        assertThatThrownBy(() -> carBookingService.bookCar(mockRequest))
                .isInstanceOf(AppUserNotFoundException.class)
                .hasMessage("User with id " + appUser.getId() + " not found");

        verify(carService, never()).getCarById(any());
        verify(carBookingDao, never()).save(any());
    }

    @Test
    void shouldThrowWhenNoExistingCarWhenBooking() {
        //given
        when(carBookingDao.findAll()).thenReturn(new ArrayList<>());
        when(appUserService.findUserById(appUser.getId())).thenReturn(Optional.of(appUser));
        when(carService.getCarById(car.getId())).thenReturn(Optional.empty());

        //when + then
        assertThatThrownBy(() -> carBookingService.bookCar(mockRequest))
                .isInstanceOf(CarNotFoundException.class)
                .hasMessage("Car with id " + car.getId() + " not found");

        verify(carBookingDao, never()).save(any());
    }

    @Test
    void shouldThrowWhenNoValidDatesWhenBooking() {
        // given
        CarBookingRequest invalidRequest = new CarBookingRequest(appUser.getId(), car.getId(), LocalDate.now(), LocalDate.now().minusDays(10));

        when(carBookingDao.findAll()).thenReturn(List.of());
        when(appUserService.findUserById(appUser.getId())).thenReturn(Optional.of(appUser));
        when(carService.getCarById(car.getId())).thenReturn(Optional.of(car));

        // when + then
        assertThatThrownBy(() -> carBookingService.bookCar(invalidRequest))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Invalid date range!");
        verify(carBookingDao, never()).save(any());
    }

    @Test
    void shouldThrowWhenNoAvailableCarWhenBooking() {
        // given
        when(carBookingDao.findAll()).thenReturn(List.of(MockDataUtil.getCarBookings().getFirst()));
        when(appUserService.findUserById(appUser.getId())).thenReturn(Optional.of(appUser));
        when(carService.getCarById(car.getId())).thenReturn(Optional.of(car));

        // when + then
        assertThatThrownBy(() -> carBookingService.bookCar(mockRequest))
                .isInstanceOf(CarAlreadyBookedException.class)
                .hasMessage("Car with plates B 1310 CH already booked.");

        verify(carBookingDao, never()).save(any());
    }

    @Test
    void shouldReturnBookedCarsForSpecificUser() {
        //given
        List<CarBooking> carBookings = MockDataUtil.getCarBookings();

        when(carBookingDao.findByAppUserId(appUser.getId())).thenReturn(carBookings);

        //when
        List<CarBooking> carsForSpecificUser = carBookingService.getBookingsForSpecificUser(appUser.getId());

        //then
        assertThat(carsForSpecificUser.getFirst().getCar()).isEqualTo(car);
    }

    @Test
    void shouldReturnEmptyListWhenThereAreNoBookings() {
        //when
        List<CarBooking> carsForSpecificUser = carBookingService.getBookingsForSpecificUser(appUser.getId());

        //then
        assertThat(carsForSpecificUser).isEmpty();
        verify(carService, never()).getCarById(car.getId());
    }

    @Test
    void shouldReturnEmptyListWhenUserHasNoBookings() {
        //given
        when(carBookingDao.findByAppUserId(any())).thenReturn(new ArrayList<>());
        //when
        List<CarBooking> carsForSpecificUser = carBookingService.getBookingsForSpecificUser(UUID.randomUUID());
        //then
        assertThat(carsForSpecificUser).isEmpty();
        verify(carService, never()).getCarById(car.getId());
    }

    @Test
    void shouldReturnAllAvailableCars() {
        //given
        Car availablePetrolCar = MockDataUtil.getCarByID(AVAILABLE_PETROL_CAR);
        Car availableElectricCar = MockDataUtil.getCarByID(AVAILABLE_ELECTRIC_CAR);

        when(carService.getCars()).thenReturn(MockDataUtil.getCars());
        when(carBookingDao.findAll()).thenReturn(MockDataUtil.getCarBookings());

        //when
        List<Car> actual = carBookingService.getAllAvailableCars();

        //then
        assertThat(actual).containsExactly(availablePetrolCar, availableElectricCar);
    }

    @Test
    void shouldTreatCarAsAvailableWhenOnlyBookingIsCompleted() {
        // given
        Car car = MockDataUtil.getCarByID(CAR_FROM_COMPLETED_BOOKING);
        CarBooking completedBooking = MockDataUtil.getCompletedBookingForUser(appUser.getId());

        when(carBookingDao.findAll()).thenReturn(List.of(completedBooking));
        when(carService.getCars()).thenReturn(List.of(car));

        // when
        List<Car> actual = carBookingService.getAllAvailableCars();

        // then
        assertThat(actual).containsExactly(car);
    }

    @Test
    void shouldReturnAllElectricCars() {
        //given
        Car availableElectricCar = MockDataUtil.getCarByID(AVAILABLE_ELECTRIC_CAR);
        when(carBookingDao.findAll()).thenReturn(MockDataUtil.getCarBookings());
        when(carService.getCars()).thenReturn(MockDataUtil.getCars());

        //when
        List<Car> actual = carBookingService.getAvailableElectricCars();

        //then
        assertThat(actual).containsExactly(availableElectricCar);
    }

    @Test
    void shouldAllowBookingWhenOverlappingBookingBelongsToDifferentCar() {
        // given
        Car car = MockDataUtil.getCarByID(AVAILABLE_ELECTRIC_CAR);
        mockRequest = new CarBookingRequest(appUser.getId(), car.getId(),
                LocalDate.of(2026, 12, 12),
                LocalDate.of(2026, 12, 13));
        when(carBookingDao.findAll()).thenReturn(MockDataUtil.getCarBookings());
        when(appUserService.findUserById(appUser.getId())).thenReturn(Optional.of(appUser));
        when(carService.getCarById(car.getId())).thenReturn(Optional.of(car));

        // when
        CarBooking actual = carBookingService.bookCar(mockRequest);

        // then
        assertThat(actual.getCar().getId()).isEqualTo(car.getId());
        verify(carBookingDao, times(1)).save(any());
    }
}
