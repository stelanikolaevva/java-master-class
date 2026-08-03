package com.stela.booking;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class CarBookingFileDataAccessServiceTest {

    @TempDir
    Path tempDir;

    private Path bookingsFile;

    private CarBookingFileDataAccessService carBookingDao;
    private final CarBooking mockBooking = new CarBooking(
            UUID.randomUUID(),
            UUID.randomUUID(),
            LocalDate.now(),
            LocalDate.now().plusDays(1),
            BigDecimal.ONE);


    @BeforeEach
    void setUp() {
        bookingsFile = tempDir.resolve("bookings.dat");
        carBookingDao = new CarBookingFileDataAccessService(bookingsFile);
    }

    @Test
    void shouldReturnEmptyListWhenNoBookingsArePresent() {
        assertThat(carBookingDao.getBookings()).isEmpty();
    }

    @Test
    void shouldSaveBooking() {
        //given
        carBookingDao.saveBooking(mockBooking);

        //when
        List<CarBooking> bookings = carBookingDao.getBookings();

        //then
        assertThat(bookings).hasSize(1);
        assertThat(bookings).containsExactly(mockBooking);
    }

    @Test
    void shouldPersistMultipleBookingsAcrossSaves() {
        // given
        CarBooking second = new CarBooking(UUID.randomUUID(), UUID.randomUUID(),
                LocalDate.now(), LocalDate.now().plusDays(2), BigDecimal.TEN);

        // when
        carBookingDao.saveBooking(mockBooking);
        carBookingDao.saveBooking(second);

        // then
        assertThat(carBookingDao.getBookings()).containsExactlyInAnyOrder(mockBooking, second);
    }

    @Test
    void shouldReturnTrueAndCancelBooking() {
        //given
        carBookingDao.saveBooking(mockBooking);

        //when
        boolean actual = carBookingDao.deleteBooking(mockBooking.getId());

        //then
        assertThat(actual).isTrue();

        List<CarBooking> cancelledBooking = carBookingDao.getBookings();
        assertThat(cancelledBooking.getFirst().getStatus())
                .isEqualTo(BookingStatus.CANCELLED);
    }

    @Test
    void shouldReturnFalseCancellingUnknownBookingId() {
        assertThat(carBookingDao.deleteBooking(UUID.randomUUID())).isFalse();
    }

    @Test
    void shouldReturnBookingByIdWhenItExists() {
        //given
        carBookingDao.saveBooking(mockBooking);

        //when
        Optional<CarBooking> actual = carBookingDao.findBookingById(mockBooking.getId());

        //then
        assertThat(actual).contains(mockBooking);
    }

    @Test
    void shouldThrowBookingPersistenceExceptionWhenFileContentIsCorrupted() throws IOException {
        // given
        Files.writeString(bookingsFile, "not a serialized object");

        // when + then
        assertThatThrownBy(() -> carBookingDao.getBookings())
                .isInstanceOf(BookingPersistenceException.class);
    }
}