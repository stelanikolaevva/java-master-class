package com.stela.booking;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;
import java.util.function.Function;

@RestController
@RequestMapping("api/v1/bookings")
public class CarBookingController {
    private final CarBookingService carBookingService;

    public CarBookingController(CarBookingService carBookingService) {
        this.carBookingService = carBookingService;
    }

    @PostMapping
    public ResponseEntity<CarBookingResponse> bookACar(@RequestBody CarBookingRequest carBookingRequest) {
        CarBooking carBooking = carBookingService.bookCar(carBookingRequest);

        return ResponseEntity.ok(mapToBookingResponse().apply(carBooking));
    }
    @GetMapping
    public ResponseEntity<List<CarBookingResponse>> getCarBookings() {
        List<CarBookingResponse> response = carBookingService.getBookings().stream()
                .map(mapToBookingResponse())
                .toList();

        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{bookingId}")
    public ResponseEntity<String> deleteBooking(@PathVariable UUID bookingId) {
        carBookingService.deleteBooking(bookingId);

        return ResponseEntity.ok().build();
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<CarBookingResponse>> getCarBookingsByUserId(@PathVariable UUID userId) {
        List<CarBookingResponse> response = carBookingService.getBookingsForSpecificUser(userId).stream()
                .map(mapToBookingResponse())
                .toList();

        return ResponseEntity.ok(response);
    }

    private Function<CarBooking, CarBookingResponse> mapToBookingResponse() {
        return (b) -> new CarBookingResponse(
                b.getId(),
                b.getAppUser().getName(),
                b.getCar().getRegNumber(),
                b.getCar().getBrand(),
                b.getStartDate(),
                b.getStartDate(),
                b.getPrice(),
                b.getStatus(),
                b.getBookedAt());
    }
}
