package com.stela.car;

import com.stela.booking.CarBookingService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/cars")
public class CarController {

    private final CarBookingService bookingService;

    public CarController(CarBookingService bookingService) {
        this.bookingService = bookingService;
    }

    @GetMapping("/available")
    public ResponseEntity<List<CarResponse>> getAvailableCars() {
        List<CarResponse> response = bookingService.getAllAvailableCars().stream()
                .map(car ->
                        new CarResponse(car.getId(),
                                car.getRegNumber(),
                                car.getRentalPricePerDay(),
                                car.getBrand(),
                                car.isElectric()))
                .toList();

        return ResponseEntity.ok(response);
    }

    @GetMapping("/available/electric")
    public ResponseEntity<List<CarResponse>> getAvailableElectricCars() {
        List<CarResponse> response = bookingService.getAvailableElectricCars().stream()
                .map(car ->
                        new CarResponse(car.getId(),
                                car.getRegNumber(),
                                car.getRentalPricePerDay(),
                                car.getBrand(),
                                car.isElectric()))
                .toList();

        return ResponseEntity.ok(response);
    }

}
