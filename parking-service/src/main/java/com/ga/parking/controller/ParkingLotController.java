
package com.ga.parking.controller;

import com.ga.parking.model.ParkingLot;
import com.ga.parking.service.ParkingLotService;

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
@RequestMapping("/lots")
@Tag(
        name = "Parking Lots",
        description = "Operations for creating, retrieving, and managing parking lots"
)
@SecurityRequirement(name = "bearerAuth")
public class ParkingLotController {

    private final ParkingLotService parkingLotService;

    public ParkingLotController(ParkingLotService parkingLotService) {
        this.parkingLotService = parkingLotService;
    }

    @PostMapping
    @Operation(
            summary = "Create a parking lot",
            description = "Creates a new parking lot with its name, location, hourly rate, and total number of parking spots."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Parking lot created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid parking lot data"),
            @ApiResponse(responseCode = "409", description = "A parking lot with this name already exists"),
            @ApiResponse(responseCode = "401", description = "Unauthorized - valid JWT token required")
    })
    public ResponseEntity<ParkingLot> createLot(
            @Valid @RequestBody ParkingLot parkingLot) {

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(parkingLotService.createLot(parkingLot));
    }

    @GetMapping
    @Operation(
            summary = "Get all parking lots",
            description = "Retrieves a list of all parking lots registered in the system."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Parking lots retrieved successfully"),
            @ApiResponse(responseCode = "401", description = "Unauthorized - valid JWT token required")
    })
    public ResponseEntity<List<ParkingLot>> getAllLots() {
        return ResponseEntity.ok(parkingLotService.getAllLots());
    }

    @GetMapping("/{lotId}")
    @Operation(
            summary = "Get parking lot by ID",
            description = "Retrieves the details of a specific parking lot using its unique ID."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Parking lot retrieved successfully"),
            @ApiResponse(responseCode = "404", description = "Parking lot not found"),
            @ApiResponse(responseCode = "401", description = "Unauthorized - valid JWT token required")
    })
    public ResponseEntity<ParkingLot> getLotById(
            @PathVariable String lotId) {

        return ResponseEntity.ok(
                parkingLotService.getLotById(lotId)
        );
    }
}
