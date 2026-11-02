
package com.ga.parking.controller;

import com.ga.parking.model.ParkingSpot;
import com.ga.parking.service.ParkingSpotService;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;

import java.util.List;

@RestController
@RequestMapping("/spots")
public class ParkingSpotController {

    private final ParkingSpotService parkingSpotService;

    public ParkingSpotController(ParkingSpotService parkingSpotService) {
        this.parkingSpotService = parkingSpotService;
    }

    // Create a parking spot
    @PostMapping
    public ResponseEntity<ParkingSpot> createSpot(
            @Valid @RequestBody ParkingSpot parkingSpot) {

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(parkingSpotService.createSpot(parkingSpot));
    }

    // Get all spots in a parking lot
    @GetMapping("/lot/{lotId}")
    public ResponseEntity<List<ParkingSpot>> getSpotsByLot(
            @PathVariable String lotId) {

        return ResponseEntity.ok(
                parkingSpotService.getSpotsByLot(lotId)
        );
    }

    // Get available spots in a parking lot
    @GetMapping("/lot/{lotId}/available")
    public ResponseEntity<List<ParkingSpot>> getAvailableSpots(
            @PathVariable String lotId) {

        return ResponseEntity.ok(
                parkingSpotService.getAvailableSpots(lotId)
        );
    }

    // Reserve a parking spot
    @PatchMapping("/{spotId}/reserve")
    public ResponseEntity<ParkingSpot> reserveSpot(
            @PathVariable String spotId) {

        return ResponseEntity.ok(
                parkingSpotService.reserveSpot(spotId)
        );
    }


    // Mark a reserved spot as occupied
    @PatchMapping("/{spotId}/occupy")
    public ResponseEntity<ParkingSpot> occupySpot(
            @PathVariable String spotId) {

        return ResponseEntity.ok(
                parkingSpotService.occupySpot(spotId)
        );
    }

    // Release an occupied spot
    @PatchMapping("/{spotId}/release")
    public ResponseEntity<ParkingSpot> releaseSpot(
            @PathVariable String spotId) {

        return ResponseEntity.ok(
                parkingSpotService.releaseSpot(spotId)
        );
    }

}
