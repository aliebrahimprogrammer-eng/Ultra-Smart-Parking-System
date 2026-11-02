package com.ga.reservation_service.controller;

import com.ga.reservation_service.model.Payment;
import com.ga.reservation_service.model.Reservation;
import com.ga.reservation_service.service.PaymentService;
import com.ga.reservation_service.service.ReservationService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/payments")
@CrossOrigin
public class PaymentController {

    private final PaymentService paymentService;
    private final ReservationService reservationService;

    public PaymentController(
            PaymentService paymentService,
            ReservationService reservationService) {

        this.paymentService = paymentService;
        this.reservationService = reservationService;
    }

    @PostMapping("/{paymentId}/confirm")
    public Reservation confirmPayment(@PathVariable String paymentId) {

        Payment payment = paymentService.confirmPayment(paymentId);

        return reservationService.confirmReservationPayment(
                payment.getReservationId()
        );
    }

    @GetMapping("/reservation/{reservationId}")
    public Payment getPaymentByReservation(
            @PathVariable String reservationId) {

        return paymentService.getPaymentByReservation(reservationId);
    }
}