package com.ga.reservation_service.controller;

import com.ga.reservation_service.dto.request.ReservationRequest;
import com.ga.reservation_service.model.Reservation;
import com.ga.reservation_service.service.ReservationService;
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

    @GetMapping
    public List<Reservation> getAllReservations() {
        return reservationService.getAllReservations();
    }

    @GetMapping("/user/{userId}")
    public List<Reservation> getUserReservations(
            @PathVariable String userId) {
        return reservationService.getUserReservations(userId);
    }

    @GetMapping("/{id}")
    public Reservation getReservationById(@PathVariable String id) {
        return reservationService.getReservationById(id);
    }

    @PutMapping("/{id}/cancel")
    public Reservation cancelReservation(@PathVariable String id) {
        return reservationService.cancelReservation(id);
    }
}