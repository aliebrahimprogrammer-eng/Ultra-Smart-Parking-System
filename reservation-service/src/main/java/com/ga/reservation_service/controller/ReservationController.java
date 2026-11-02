package com.ga.reservation_service.controller;

import com.ga.reservation_service.dto.request.ReservationRequest;
import com.ga.reservation_service.model.Reservation;
import com.ga.reservation_service.service.ReservationService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/reservations")
@CrossOrigin
public class ReservationController {

    private final ReservationService reservationService;

    public ReservationController(ReservationService reservationService) {
        this.reservationService = reservationService;
    }

    @PreAuthorize("hasAnyRole('CUSTOMER', 'ADMIN')")
    @PostMapping
    public Reservation createReservation(
            @RequestBody ReservationRequest request) {

        return reservationService.createReservation(
                request.getUserId(),
                request.getVehicleId(),
                request.getSpotId(),
                request.getLotId(),
                request.getAmount(),
                request.getMethod()
        );
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping
    public List<Reservation> getAllReservations() {
        return reservationService.getAllReservations();
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/user/{userId}")
    public List<Reservation> getUserReservations(
            @PathVariable String userId) {
        return reservationService.getUserReservations(userId);
    }

    @PreAuthorize("hasAnyRole('CUSTOMER', 'ADMIN')")
    @GetMapping("/{id}")
    public Reservation getReservationById(@PathVariable String id) {
        return reservationService.getReservationById(id);
    }

    @PreAuthorize("hasAnyRole('CUSTOMER', 'ADMIN')")
    @PutMapping("/{id}/cancel")
    public Reservation cancelReservation(@PathVariable String id) {
        return reservationService.cancelReservation(id);
    }
}