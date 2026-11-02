package com.ga.reservation_service.dto.request;

import com.ga.reservation_service.model.Payment;
import jakarta.validation.constraints.Positive;

public class ReservationRequest {

    private String vehicleId;
    private String spotId;
    private String lotId;

    @Positive(message = "Amount must be greater than zero")
    private double amount;

    private Payment.PaymentMethod method;

    public String getVehicleId() {
        return vehicleId;
    }

    public void setVehicleId(String vehicleId) {
        this.vehicleId = vehicleId;
    }

    public String getSpotId() {
        return spotId;
    }

    public void setSpotId(String spotId) {
        this.spotId = spotId;
    }

    public String getLotId() {
        return lotId;
    }

    public void setLotId(String lotId) {
        this.lotId = lotId;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public Payment.PaymentMethod getMethod() {
        return method;
    }

    public void setMethod(Payment.PaymentMethod method) {
        this.method = method;
    }
}