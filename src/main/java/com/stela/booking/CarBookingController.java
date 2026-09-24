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

@RestController
@RequestMapping("api/v1/bookings")
public class CarBookingController {
    private final CarBookingService carBookingService;

    public CarBookingController(CarBookingService carBookingService) {
        this.carBookingService = carBookingService;
    }

    @PostMapping
    public ResponseEntity<CarBookingResponse> bookACar(@RequestBody CarBookingRequest carBookingRequest) {
        CarBookingResponse response = carBookingService.bookCar(carBookingRequest);

        return ResponseEntity.ok(response);
    }
    @GetMapping
    public ResponseEntity<List<CarBookingResponse>> getCarBookings() {
        List<CarBookingResponse> response = carBookingService.getBookings();

        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{bookingId}")
    public ResponseEntity<String> deleteBooking(@PathVariable UUID bookingId) {
        carBookingService.deleteBooking(bookingId);

        return ResponseEntity.ok().build();
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<CarBookingResponse>> getCarBookingsByUserId(@PathVariable UUID userId) {
        List<CarBookingResponse> response = carBookingService.getBookingsForSpecificUser(userId);
        return ResponseEntity.ok(response);
    }
}
