package com.ga.reservation_service.repository;

import com.ga.reservation_service.model.Payment;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

public interface PaymentRepository extends MongoRepository<Payment, String> {

    Optional<Payment> findByReservationId(String reservationId);

    List<Payment> findByUserId(String userId);
}