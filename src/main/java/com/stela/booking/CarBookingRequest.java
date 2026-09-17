package com.stela.booking;

import java.time.LocalDate;
import java.util.UUID;

public record CarBookingRequest(UUID userId, UUID carId, LocalDate startDate, LocalDate endDate) {
}
