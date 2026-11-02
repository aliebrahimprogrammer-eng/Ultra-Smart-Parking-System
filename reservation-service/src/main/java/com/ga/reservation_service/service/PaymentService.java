package com.ga.reservation_service.service;

import com.ga.reservation_service.model.Payment;
import com.ga.reservation_service.repository.PaymentRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class PaymentService {

    private final PaymentRepository paymentRepository;

    public PaymentService(PaymentRepository paymentRepository) {
        this.paymentRepository = paymentRepository;
    }

    public Payment createPayment(
            String reservationId,
            String userId,
            double amount,
            Payment.PaymentMethod method) {

        Payment payment = new Payment();

        payment.setReservationId(reservationId);
        payment.setUserId(userId);
        payment.setAmount(amount);
        payment.setMethod(method);
        payment.setStatus(Payment.PaymentStatus.PENDING);

        return paymentRepository.save(payment);
    }

    public Payment confirmPayment(String paymentId) {

        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new RuntimeException("Payment not found"));

        if (payment.getStatus() != Payment.PaymentStatus.PENDING) {
            throw new RuntimeException("Payment is not pending");
        }

        payment.setStatus(Payment.PaymentStatus.SUCCESSFUL);
        payment.setPaidAt(LocalDateTime.now());

        return paymentRepository.save(payment);
    }

    public Payment getPaymentByReservation(String reservationId) {

        return paymentRepository.findByReservationId(reservationId)
                .orElseThrow(() -> new RuntimeException("Payment not found"));
    }
}