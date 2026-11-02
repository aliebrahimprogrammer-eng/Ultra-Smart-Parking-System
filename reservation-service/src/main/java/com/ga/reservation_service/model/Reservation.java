package com.ga.reservation_service.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Data
@Document(collection = "reservations")
public class Reservation {

    @Id
    private String id;

    private String userId;
    private String vehicleId;
    private String spotId;
    private String lotId;

    private LocalDateTime reservedAt;
    private LocalDateTime expiresAt;

    public enum ReservationStatus {
        PENDING,
        CONFIRMED,
        EXPIRED,
        CANCELLED,
        ACTIVE,
        COMPLETED
    }
    private ReservationStatus status;

    private LocalDateTime checkInTime;
    private LocalDateTime checkOutTime;

    private double totalFee;
}
