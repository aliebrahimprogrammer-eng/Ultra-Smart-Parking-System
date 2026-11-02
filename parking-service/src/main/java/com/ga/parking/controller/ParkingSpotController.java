
package com.ga.parking.controller;

import com.ga.parking.model.ParkingSpot;
import com.ga.parking.service.ParkingSpotService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/spots")
@Tag(
        name = "Parking Spots",
        description = "Operations for creating, retrieving, reserving, occupying, and releasing parking spots"
)
@SecurityRequirement(name = "bearerAuth")
public class ParkingSpotController {

    private final ParkingSpotService parkingSpotService;

    public ParkingSpotController(ParkingSpotService parkingSpotService) {
        this.parkingSpotService = parkingSpotService;
    }

    @PostMapping
    @Operation(
            summary = "Create a parking spot",
            description = "Creates a new parking spot inside an existing parking lot. The spot is initially assigned AVAILABLE status."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Parking spot created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid parking spot data"),
            @ApiResponse(responseCode = "404", description = "Parking lot not found"),
            @ApiResponse(responseCode = "409", description = "A spot with this number already exists in the parking lot"),
            @ApiResponse(responseCode = "401", description = "Unauthorized - valid JWT token required")
    })
    public ResponseEntity<ParkingSpot> createSpot(
            @Valid @RequestBody ParkingSpot parkingSpot) {

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(parkingSpotService.createSpot(parkingSpot));
    }

    @GetMapping("/lot/{lotId}")
    @Operation(
            summary = "Get all spots in a parking lot",
            description = "Retrieves every parking spot belonging to the specified parking lot, including its type and current status."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Parking spots retrieved successfully"),
            @ApiResponse(responseCode = "404", description = "Parking lot not found"),
            @ApiResponse(responseCode = "401", description = "Unauthorized - valid JWT token required")
    })
    public ResponseEntity<List<ParkingSpot>> getSpotsByLot(
            @PathVariable String lotId) {

        return ResponseEntity.ok(
                parkingSpotService.getSpotsByLot(lotId)
        );
    }

    @GetMapping("/lot/{lotId}/available")
    @Operation(
            summary = "Get available parking spots",
            description = "Retrieves only parking spots with AVAILABLE status in the specified parking lot."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Available parking spots retrieved successfully"),
            @ApiResponse(responseCode = "404", description = "Parking lot not found"),
            @ApiResponse(responseCode = "401", description = "Unauthorized - valid JWT token required")
    })
    public ResponseEntity<List<ParkingSpot>> getAvailableSpots(
            @PathVariable String lotId) {

        return ResponseEntity.ok(
                parkingSpotService.getAvailableSpots(lotId)
        );
    }

    @PatchMapping("/{spotId}/reserve")
    @Operation(
            summary = "Reserve a parking spot",
            description = "Atomically changes a parking spot from AVAILABLE to RESERVED. Prevents multiple requests from successfully reserving the same spot."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Parking spot reserved successfully"),
            @ApiResponse(responseCode = "409", description = "Parking spot is unavailable or does not exist"),
            @ApiResponse(responseCode = "401", description = "Unauthorized - valid JWT token required")
    })
    public ResponseEntity<ParkingSpot> reserveSpot(
            @PathVariable String spotId) {

        return ResponseEntity.ok(
                parkingSpotService.reserveSpot(spotId)
        );
    }

    @PatchMapping("/{spotId}/occupy")
    @Operation(
            summary = "Occupy a reserved parking spot",
            description = "Changes a parking spot from RESERVED to OCCUPIED when the vehicle arrives."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Parking spot marked as occupied successfully"),
            @ApiResponse(responseCode = "409", description = "Parking spot must be reserved before it can be occupied"),
            @ApiResponse(responseCode = "401", description = "Unauthorized - valid JWT token required")
    })
    public ResponseEntity<ParkingSpot> occupySpot(
            @PathVariable String spotId) {

        return ResponseEntity.ok(
                parkingSpotService.occupySpot(spotId)
        );
    }

    @PatchMapping("/{spotId}/release")
    @Operation(
            summary = "Release an occupied parking spot",
            description = "Changes a parking spot from OCCUPIED to AVAILABLE when the vehicle leaves."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Parking spot released successfully"),
            @ApiResponse(responseCode = "409", description = "Parking spot must be occupied before it can be released"),
            @ApiResponse(responseCode = "401", description = "Unauthorized - valid JWT token required")
    })
    public ResponseEntity<ParkingSpot> releaseSpot(
            @PathVariable String spotId) {

        return ResponseEntity.ok(
                parkingSpotService.releaseSpot(spotId)
        );
    }

    @PatchMapping("/{spotId}/cancel-reservation")
    @Operation(
            summary = "Cancel a parking spot reservation",
            description = "Changes a parking spot from RESERVED back to AVAILABLE when its reservation is cancelled."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Reservation cancelled successfully"),
            @ApiResponse(responseCode = "409", description = "Parking spot is not currently reserved or does not exist"),
            @ApiResponse(responseCode = "401", description = "Unauthorized - valid JWT token required")
    })
    public ResponseEntity<ParkingSpot> cancelReservation(
            @PathVariable String spotId) {

        return ResponseEntity.ok(
                parkingSpotService.cancelReservation(spotId)
        );
    }
}
