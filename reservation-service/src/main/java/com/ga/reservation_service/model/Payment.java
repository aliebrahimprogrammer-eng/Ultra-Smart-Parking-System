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
    public enum PaymentMethod {
        CREDIT_CARD,
        DEBIT_CARD,
        CASH,
        APPLE_PAY,
        GOOGLE_PAY
    }
    private PaymentMethod method;

    public enum PaymentStatus {
        PENDING,
        SUCCESSFUL,
        FAILED
    }

    private PaymentStatus status = PaymentStatus.PENDING;

    private LocalDateTime paidAt;
}