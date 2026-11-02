package com.ga.reservation_service.repository;

import com.ga.reservation_service.model.Reservation;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface ReservationRepository extends MongoRepository<Reservation, String> {

    List<Reservation> findByUserId(String userId);

    List<Reservation> findByVehicleId(String vehicleId);

    List<Reservation> findByStatus(Reservation.ReservationStatus status);

    List<Reservation> findBySpotIdAndStatus(
            String spotId,
            Reservation.ReservationStatus status
    );
}