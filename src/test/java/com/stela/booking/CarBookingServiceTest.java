package com.stela.booking;

import com.stela.car.Car;
import com.stela.car.CarNotFoundException;
import com.stela.car.CarResponse;
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

    public static final String AVAILABLE_ELECTRIC_CAR = "c7664df0-6fa6-4b38-8185-cb14fe061556";
    public static final String CAR_FROM_COMPLETED_BOOKING = "d40d949f-c13c-43e6-af6d-d6d235833d9f";
    public static final String CAR_FROM_CANCELED_BOOKING = "92f9e8e7-4126-4a55-90c6-d4977c4d90b3";
    public static final String CAR_ID = "00ba7493-1e63-4dea-b7be-34060e73047f";

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
        car = MockDataUtil.getCarForCarId(CAR_ID);
        mockRequest = new CarBookingRequest(appUser.getId(), car.getId(),
                LocalDate.of(2026, 12, 12),
                LocalDate.of(2026, 12, 13));
    }


    @Test
    void shouldReturnAllBookings() {
        //given
        when(carBookingDao.findAll()).thenReturn(MockDataUtil.getCarBookings());

        //when
        List<CarBookingResponse> actual = carBookingService.getBookings();

        //then
        List<CarBookingResponse> responses = MockDataUtil.getCarBookingsResponse();
        assertThat(actual).containsExactlyElementsOf(responses);
    }

    @Test
    void shouldReturnEmptyListWhenNoBookings() {
        //given
        when(carBookingDao.findAll()).thenReturn(new ArrayList<>());
        //when
        List<CarBookingResponse> actual = carBookingService.getBookings();
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
    void shouldReturnActiveBookingResponseWhenCarAndUserAreValidForBooking() {
        // given
        when(carBookingDao.findAll()).thenReturn(new ArrayList<>());
        when(appUserService.findUserById(appUser.getId())).thenReturn(Optional.of(appUser));
        when(carService.getCarById(car.getId())).thenReturn(Optional.of(car));

        // when
        CarBookingResponse actual = carBookingService.bookCar(mockRequest);

        // then
        BigDecimal expectedPrice = car.getRentalPricePerDay().multiply(BigDecimal.valueOf(2));

        assertThat(actual.startDate()).isEqualTo(LocalDate.of(2026, 12, 12));
        assertThat(actual.endDate()).isEqualTo(LocalDate.of(2026, 12, 13));
        assertThat(actual.status()).isEqualTo(BookingStatus.ACTIVE);
        assertThat(actual.price()).isEqualByComparingTo(expectedPrice);

        ArgumentCaptor<CarBooking> captor = ArgumentCaptor.forClass(CarBooking.class);
        verify(carBookingDao, times(1)).save(captor.capture());

        CarBooking saved = captor.getValue();
        assertThat(saved.getStatus()).isEqualTo(BookingStatus.ACTIVE);
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
    void shouldThrowWhenEndDateIsBeforeStartDate() {
        // given
        CarBookingRequest invalidRequest = new CarBookingRequest(appUser.getId(), car.getId(), LocalDate.now(), LocalDate.now().minusDays(10));

        when(carBookingDao.findAll()).thenReturn(List.of());
        when(appUserService.findUserById(appUser.getId())).thenReturn(Optional.of(appUser));
        when(carService.getCarById(car.getId())).thenReturn(Optional.of(car));

        // when + then
        assertThatThrownBy(() -> carBookingService.bookCar(invalidRequest)).isInstanceOf(IllegalArgumentException.class).hasMessage("Invalid date range!");
        verify(carBookingDao, never()).save(any());
    }

    @Test
    void shouldThrowWhenStartDateIsInThePast() {
        // given
        CarBookingRequest invalidRequest = new CarBookingRequest(appUser.getId(), car.getId(), LocalDate.now().minusDays(10), LocalDate.now());

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
    void shouldAllowBookingStartingDayAfterExistingActiveBookingEnds() {
        // given
        CarBookingRequest nextDayRequest = new CarBookingRequest(appUser.getId(), car.getId(), LocalDate.of(2026, 12, 14), LocalDate.of(2026, 12, 15));

        when(carBookingDao.findAll()).thenReturn(MockDataUtil.getCarBookings());
        when(appUserService.findUserById(appUser.getId())).thenReturn(Optional.of(appUser));
        when(carService.getCarById(car.getId())).thenReturn(Optional.of(car));

        // when
        CarBookingResponse actual = carBookingService.bookCar(nextDayRequest);

        // then
        assertThat(actual.carRegNumber()).isEqualTo(car.getRegNumber());
        verify(carBookingDao, times(1)).save(any());
    }

    @Test
    void shouldThrowWhenNewBookingStartsOnDayExistingActiveBookingEnds() {
        // given
        CarBookingRequest backToBackRequest = new CarBookingRequest(appUser.getId(), car.getId(), LocalDate.of(2026, 12, 13), LocalDate.of(2026, 12, 14));

        when(carBookingDao.findAll()).thenReturn(MockDataUtil.getCarBookings());
        when(appUserService.findUserById(appUser.getId())).thenReturn(Optional.of(appUser));
        when(carService.getCarById(car.getId())).thenReturn(Optional.of(car));

        // when + then
        assertThatThrownBy(() -> carBookingService.bookCar(backToBackRequest)).isInstanceOf(CarAlreadyBookedException.class).hasMessage("Car with plates CB 2320 CH already booked.");

        verify(carBookingDao, never()).save(any());
    }
    @Test
    void shouldThrowWhenNoAvailableCarWhenBooking() {
        // given
        when(carBookingDao.findAll()).thenReturn(MockDataUtil.getCarBookings());
        when(appUserService.findUserById(appUser.getId())).thenReturn(Optional.of(appUser));
        when(carService.getCarById(car.getId())).thenReturn(Optional.of(car));

        // when + then
        assertThatThrownBy(() -> carBookingService.bookCar(mockRequest))
                .isInstanceOf(CarAlreadyBookedException.class).hasMessage("Car with plates CB 2320 CH already booked.");

        verify(carBookingDao, never()).save(any());
    }

    @Test
    void shouldAllowBookingWhenExistingBookingForSameCarIsCancelled() {
        // given
        CarBooking cancelledBooking = MockDataUtil.getBookingForCarAndStatus(CAR_FROM_CANCELED_BOOKING, BookingStatus.CANCELLED);
        Car carFromCancelled = MockDataUtil.getCarForCarId(CAR_FROM_CANCELED_BOOKING);

        mockRequest = new CarBookingRequest(appUser.getId(), carFromCancelled.getId(), LocalDate.of(2026, 12, 12), LocalDate.of(2026, 12, 13));

        when(carBookingDao.findAll()).thenReturn(List.of(cancelledBooking));
        when(appUserService.findUserById(appUser.getId())).thenReturn(Optional.of(appUser));
        when(carService.getCarById(carFromCancelled.getId())).thenReturn(Optional.of(carFromCancelled));

        // when
        CarBookingResponse actual = carBookingService.bookCar(mockRequest);

        // then
        assertThat(actual.carRegNumber()).isEqualTo(carFromCancelled.getRegNumber());
        verify(carBookingDao, times(1)).save(any());
    }

    @Test
    void shouldAllowBookingWhenExistingBookingForSameCarIsCompleted() {
        // given
        CarBooking completedBooking = MockDataUtil.getBookingForCarAndStatus(CAR_FROM_COMPLETED_BOOKING, BookingStatus.COMPLETED);
        Car completedCar = MockDataUtil.getCarForCarId(CAR_FROM_COMPLETED_BOOKING);

        mockRequest = new CarBookingRequest(appUser.getId(), completedCar.getId(), LocalDate.of(2026, 12, 12), LocalDate.of(2026, 12, 13));

        when(carBookingDao.findAll()).thenReturn(List.of(completedBooking));
        when(appUserService.findUserById(appUser.getId())).thenReturn(Optional.of(appUser));
        when(carService.getCarById(completedCar.getId())).thenReturn(Optional.of(completedCar));

        // when
        CarBookingResponse actual = carBookingService.bookCar(mockRequest);

        // then
        assertThat(actual.carRegNumber()).isEqualTo(completedCar.getRegNumber());
        verify(carBookingDao, times(1)).save(any());
    }

    @Test
    void shouldReturnBookedCarsForSpecificUser() {
        //given
        List<CarBooking> carBookings = MockDataUtil.getCarBookings();

        when(carBookingDao.findByAppUserId(appUser.getId())).thenReturn(carBookings);

        //when
        List<CarBookingResponse> carsForSpecificUser = carBookingService.getBookingsForSpecificUser(appUser.getId());

        //then
        assertThat(carsForSpecificUser.size()).isEqualTo(4);
        assertThat(carsForSpecificUser).allMatch(carBooking -> carBooking.userName().equals(appUser.getName()));
        verify(carBookingDao, times(1)).findByAppUserId(appUser.getId());
    }

    @Test
    void shouldReturnEmptyListWhenUserHasNoBookings() {
        //given
        when(carBookingDao.findByAppUserId(any())).thenReturn(new ArrayList<>());
        //when
        List<CarBookingResponse> carsForSpecificUser = carBookingService.getBookingsForSpecificUser(UUID.randomUUID());
        //then
        assertThat(carsForSpecificUser).isEmpty();
        verify(carBookingDao, times(1)).findByAppUserId(any());
    }

    @Test
    void shouldReturnAllAvailableCars() {
        //given
        CarResponse availablePetrolCar = MockDataUtil.getCarByID(CAR_FROM_COMPLETED_BOOKING);
        CarResponse availableElectricCar = MockDataUtil.getCarByID(AVAILABLE_ELECTRIC_CAR);

        when(carBookingDao.findAll()).thenReturn(MockDataUtil.getCarBookings());
        when(carService.getCars()).thenReturn(MockDataUtil.getCars());

        //when
        List<CarResponse> actual = carBookingService.getAllAvailableCars();

        //then
        assertThat(actual).containsExactlyInAnyOrder(availablePetrolCar, availableElectricCar);

        verify(carBookingDao, times(1)).findAll();
        verify(carService, times(1)).getCars();
    }

    @Test
    void shouldReturnAllElectricCars() {
        //given
        CarResponse availableElectricCar = MockDataUtil.getCarByID(AVAILABLE_ELECTRIC_CAR);
        when(carBookingDao.findAll()).thenReturn(MockDataUtil.getCarBookings());
        when(carService.getCars()).thenReturn(MockDataUtil.getCars());

        //when
        List<CarResponse> actual = carBookingService.getAvailableElectricCars();

        //then
        assertThat(actual).containsExactlyInAnyOrder(availableElectricCar);
        verify(carBookingDao, times(1)).findAll();
        verify(carService, times(1)).getCars();
    }

    @Test
    void shouldIncludeCarWithActiveBookingWhenRequestedPeriodDoesNotOverlapIt() {
        // given
        LocalDate futureStart = LocalDate.of(2027, 1, 1);
        LocalDate futureEnd = LocalDate.of(2027, 1, 2);

        when(carBookingDao.findAll()).thenReturn(MockDataUtil.getCarBookings());
        when(carService.getCars()).thenReturn(MockDataUtil.getCars());

        // when
        List<CarResponse> actual = carBookingService.getAvailableCarsForSpecificPeriod(false, futureStart, futureEnd);

        // then
        assertThat(actual).extracting(CarResponse::regNumber).contains(car.getRegNumber());
        assertThat(actual).hasSize(MockDataUtil.getCars().size());
    }

    @Test
    void shouldExcludeCarWithActiveBookingWhenRequestedPeriodOverlapsIt() {
        // given
        when(carBookingDao.findAll()).thenReturn(MockDataUtil.getCarBookings());
        when(carService.getCars()).thenReturn(MockDataUtil.getCars());

        // when
        List<CarResponse> actual = carBookingService.getAvailableCarsForSpecificPeriod(false, LocalDate.of(2026, 12, 12), LocalDate.of(2026, 12, 13));

        // then
        assertThat(actual).extracting(CarResponse::regNumber).doesNotContain(car.getRegNumber());
    }

    @Test
    void shouldReturnOnlyElectricCarsWhenIsElectricOnlyIsTrue() {
        // given
        CarResponse availableElectricCar = MockDataUtil.getCarByID(AVAILABLE_ELECTRIC_CAR);

        when(carBookingDao.findAll()).thenReturn(MockDataUtil.getCarBookings());
        when(carService.getCars()).thenReturn(MockDataUtil.getCars());

        // when
        List<CarResponse> actual = carBookingService.getAvailableCarsForSpecificPeriod(true, LocalDate.of(2026, 12, 12), LocalDate.of(2026, 12, 13));

        // then
        assertThat(actual).containsExactlyInAnyOrder(availableElectricCar);
    }
    @Test
    void shouldAllowBookingWhenOverlappingBookingBelongsToDifferentCar() {
        // given
        Car unbookedCar = MockDataUtil.getCarForCarId(AVAILABLE_ELECTRIC_CAR);

        mockRequest = new CarBookingRequest(appUser.getId(), unbookedCar.getId(),
                LocalDate.of(2026, 12, 12),
                LocalDate.of(2026, 12, 13));

        when(carBookingDao.findAll()).thenReturn(MockDataUtil.getCarBookings());
        when(appUserService.findUserById(appUser.getId())).thenReturn(Optional.of(appUser));
        when(carService.getCarById(unbookedCar.getId())).thenReturn(Optional.of(unbookedCar));

        // when
        CarBookingResponse actual = carBookingService.bookCar(mockRequest);

        // then
        assertThat(actual.carRegNumber()).isEqualTo(unbookedCar.getRegNumber());
        verify(carBookingDao, times(1)).save(any());
    }

}
