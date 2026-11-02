
package com.ga.parking.controller;

import com.ga.parking.model.ParkingLot;
import com.ga.parking.service.ParkingLotService;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/lots")
public class ParkingLotController {

    private final ParkingLotService parkingLotService;

    public ParkingLotController(ParkingLotService parkingLotService) {
        this.parkingLotService = parkingLotService;
    }

    @PostMapping
    public ResponseEntity<ParkingLot> createLot(
            @RequestBody ParkingLot parkingLot) {

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(parkingLotService.createLot(parkingLot));
    }

    @GetMapping
    public ResponseEntity<List<ParkingLot>> getAllLots() {
        return ResponseEntity.ok(parkingLotService.getAllLots());
    }

    @GetMapping("/{lotId}")
    public ResponseEntity<ParkingLot> getLotById(
            @PathVariable String lotId) {

        return ResponseEntity.ok(
                parkingLotService.getLotById(lotId)
        );
    }
}
