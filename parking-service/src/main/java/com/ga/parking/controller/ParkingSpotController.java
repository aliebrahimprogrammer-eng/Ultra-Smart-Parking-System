
package com.ga.parking.controller;

import com.ga.parking.model.ParkingSpot;
import com.ga.parking.service.ParkingSpotService;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/spots")
public class ParkingSpotController {

    private final ParkingSpotService parkingSpotService;

    public ParkingSpotController(ParkingSpotService parkingSpotService) {
        this.parkingSpotService = parkingSpotService;
    }

    @PatchMapping("/{spotId}/reserve")
    public ResponseEntity<ParkingSpot> reserveSpot(
            @PathVariable String spotId) {

        ParkingSpot reservedSpot =
                parkingSpotService.reserveSpot(spotId);

        return ResponseEntity.ok(reservedSpot);
    }
}
