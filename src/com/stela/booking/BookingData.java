package com.stela.booking;

import java.time.LocalDate;
import java.util.UUID;

public class BookingData {
    private UUID userId;
    private UUID carId;
    private LocalDate startDate;
    private LocalDate endDate;

    public BookingData(UUID userID, UUID carId, LocalDate startDate, LocalDate endDate) {
        this.userId = userID;
        this.carId = carId;
        this.startDate = startDate;
        this.endDate = endDate;
    }

    public UUID getUserId() {
        return userId;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public UUID getCarId() {
        return carId;
    }
}
