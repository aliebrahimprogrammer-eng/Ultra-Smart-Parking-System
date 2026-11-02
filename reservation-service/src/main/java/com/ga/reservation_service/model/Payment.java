package com.ga.reservation_service.model;


import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Data
@Document(collection = "payments")
public class Payment {

    @Id
    private String id;

    private String reservationId;
    private String userId;
    private double amount;
    public enum PaymentStatus {
        PENDING,
        PAID,
        FAILED,
        REFUNDED
    }
    private PaymentStatus status;
    private LocalDateTime paymentDate;
}