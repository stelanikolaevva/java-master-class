package com.stela.booking;

import com.stela.car.Car;
import com.stela.user.User;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;
import java.util.UUID;

public class CarBooking {
    private UUID id;
    private User user;
    private Car car;
    private LocalDate startDate;
    private LocalDate endDate;
    private BigDecimal price;
    private BookingStatus status;
    private LocalDateTime bookedAt;

    public CarBooking(User user,
                      Car car,
                      LocalDate startDate,
                      LocalDate endDate,
                      BigDecimal price) {
        this.id = UUID.randomUUID();
        this.user = user;
        this.car = car;
        this.startDate = startDate;
        this.endDate = endDate;
        this.price = price;
        this.status = BookingStatus.ACTIVE;
        this.bookedAt = LocalDateTime.now();
    }

    public UUID getId() {
        return id;
    }

    public User getUser() {
        return user;
    }

    public Car getCar() {
        return car;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public BookingStatus getStatus() {
        return status;
    }

    public LocalDateTime getBookedAt() {
        return bookedAt;
    }

    public void setStatus(BookingStatus status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "id: " + id + "\n" +
                "Booked by: " + user.getName() + "\n" +
                "Car: " + car.getRegNumber() + "\n" +
                "From: " + startDate + ", to: " + endDate + "\n" +
                "Total cost: " + price + "$\n" +
                "Status: " + status + "\n" +
                "Booked at: " + bookedAt.format(DateTimeFormatter.ofPattern("HH:mm dd-MM-yyyy")) + "\n\n";
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        CarBooking booking = (CarBooking) o;
        return Objects.equals(id, booking.id) && Objects.equals(user, booking.user) && Objects.equals(car, booking.car) && Objects.equals(startDate, booking.startDate) && Objects.equals(endDate, booking.endDate) && Objects.equals(price, booking.price) && status == booking.status && Objects.equals(bookedAt, booking.bookedAt);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, user, car, startDate, endDate, price, status, bookedAt);
    }
}
