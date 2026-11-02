package com.ga.reservation_service.controller;

import com.ga.reservation_service.dto.request.ReservationRequest;
import com.ga.reservation_service.model.Reservation;
import com.ga.reservation_service.security.CurrentUser;
import com.ga.reservation_service.service.ReservationService;
import com.ga.reservation_service.service.VehicleService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/reservations")
@CrossOrigin
public class ReservationController {

    private final ReservationService reservationService;
    private final VehicleService vehicleService;

    public ReservationController(
            ReservationService reservationService,
            VehicleService vehicleService) {

        this.reservationService = reservationService;
        this.vehicleService = vehicleService;
    }

    @PostMapping
    public Reservation createReservation(
            @Valid @RequestBody ReservationRequest request) {

        CurrentUser.checkOwner(
                vehicleService.getVehicleById(request.getVehicleId()).getUserId()
        );

        return reservationService.createReservation(
                CurrentUser.getEmail(),
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
        CurrentUser.checkOwner(userId);
        return reservationService.getUserReservations(userId);
    }

    @GetMapping("/{id}")
    public Reservation getReservationById(@PathVariable String id) {
        Reservation reservation = reservationService.getReservationById(id);
        CurrentUser.checkOwner(reservation.getUserId());
        return reservation;
    }

    @PutMapping("/{id}/cancel")
    public Reservation cancelReservation(@PathVariable String id) {
        CurrentUser.checkOwner(
                reservationService.getReservationById(id).getUserId()
        );
        return reservationService.cancelReservation(id);
    }
}
