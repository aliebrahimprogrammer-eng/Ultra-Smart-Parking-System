package com.ga.reservation_service.service;

import com.ga.reservation_service.model.Payment;
import com.ga.reservation_service.repository.PaymentRepository;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.FindAndModifyOptions;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;

@Service
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final MongoTemplate mongoTemplate;

    public PaymentService(
            PaymentRepository paymentRepository,
            MongoTemplate mongoTemplate) {

        this.paymentRepository = paymentRepository;
        this.mongoTemplate = mongoTemplate;
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

        Query query = new Query(
                Criteria.where("id").is(paymentId)
                        .and("status").is(Payment.PaymentStatus.PENDING)
        );

        Update update = new Update()
                .set("status", Payment.PaymentStatus.SUCCESSFUL)
                .set("paidAt", LocalDateTime.now());

        Payment confirmedPayment = mongoTemplate.findAndModify(
                query,
                update,
                FindAndModifyOptions.options().returnNew(true),
                Payment.class
        );

        if (confirmedPayment == null) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Payment cannot be confirmed. It may already have been processed."
            );
        }

        return confirmedPayment;
    }

    public Payment getPaymentById(String paymentId) {

        return paymentRepository.findById(paymentId)
                .orElseThrow(() -> new RuntimeException("Payment not found"));
    }

    public Payment getPaymentByReservation(String reservationId) {

        return paymentRepository.findByReservationId(reservationId)
                .orElseThrow(() -> new RuntimeException("Payment not found"));
    }
}