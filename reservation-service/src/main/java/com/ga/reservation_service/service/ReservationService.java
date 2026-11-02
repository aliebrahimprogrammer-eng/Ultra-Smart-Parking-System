package com.ga.reservation_service.service;

import com.ga.reservation_service.model.Payment;
import com.ga.reservation_service.model.Reservation;
import com.ga.reservation_service.repository.PaymentRepository;
import com.ga.reservation_service.repository.ReservationRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.FindAndModifyOptions;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;

@Service
public class ReservationService {

    private final ReservationRepository reservationRepository;
    private final PaymentRepository paymentRepository;
    private final PaymentService paymentService;
    private final MongoTemplate mongoTemplate;

    public ReservationService(
            ReservationRepository reservationRepository,
            PaymentRepository paymentRepository,
            PaymentService paymentService,
            MongoTemplate mongoTemplate) {

        this.reservationRepository = reservationRepository;
        this.paymentRepository = paymentRepository;
        this.paymentService = paymentService;
        this.mongoTemplate = mongoTemplate;
    }

    public Reservation createReservation(
            String userId,
            String vehicleId,
            String spotId,
            String lotId,
            double amount,
            Payment.PaymentMethod method) {

        Reservation reservation = new Reservation();

        reservation.setUserId(userId);
        reservation.setVehicleId(vehicleId);
        reservation.setSpotId(spotId);
        reservation.setLotId(lotId);

        reservation.setReservedAt(LocalDateTime.now());
        reservation.setExpiresAt(LocalDateTime.now().plusMinutes(15));

        reservation.setStatus(Reservation.ReservationStatus.PENDING);
        reservation.setTotalFee(amount);

        Reservation savedReservation = reservationRepository.save(reservation);

        paymentService.createPayment(
                savedReservation.getId(),
                userId,
                amount,
                method
        );

        return savedReservation;
    }

    public Reservation confirmReservationPayment(String reservationId) {

        Payment payment = paymentService.getPaymentByReservation(reservationId);

        if (payment.getStatus() != Payment.PaymentStatus.SUCCESSFUL) {
            throw new RuntimeException("Payment has not been confirmed");
        }

        LocalDateTime now = LocalDateTime.now();

        Query query = new Query(
                Criteria.where("id").is(reservationId)
                        .and("status").is(Reservation.ReservationStatus.PENDING)
                        .and("expiresAt").gt(now)
        );

        Update update = new Update()
                .set("status", Reservation.ReservationStatus.CONFIRMED);

        Reservation confirmedReservation = mongoTemplate.findAndModify(
                query,
                update,
                FindAndModifyOptions.options().returnNew(true),
                Reservation.class
        );

        if (confirmedReservation == null) {
            throw new RuntimeException(
                    "Reservation cannot be confirmed. It may have expired or already been processed."
            );
        }

        return confirmedReservation;
    }

    public Reservation getReservationById(String id) {

        return reservationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Reservation not found"));
    }

    public List<Reservation> getUserReservations(String userId) {
        return reservationRepository.findByUserId(userId);
    }

    public List<Reservation> getAllReservations() {
        return reservationRepository.findAll();
    }

    public Reservation cancelReservation(String id) {

        Query query = new Query(
                Criteria.where("id").is(id)
                        .and("status").in(
                                Reservation.ReservationStatus.PENDING,
                                Reservation.ReservationStatus.CONFIRMED
                        )
        );

        Update update = new Update()
                .set("status", Reservation.ReservationStatus.CANCELLED);

        Reservation cancelledReservation = mongoTemplate.findAndModify(
                query,
                update,
                FindAndModifyOptions.options().returnNew(true),
                Reservation.class
        );

        if (cancelledReservation == null) {
            throw new RuntimeException(
                    "Reservation cannot be cancelled. It may already be active, completed, expired or cancelled."
            );
        }

        return cancelledReservation;
    }

    @Scheduled(fixedRate = 30000)
    public void expirePendingReservations() {

        LocalDateTime now = LocalDateTime.now();

        Query query = new Query(
                Criteria.where("status").is(Reservation.ReservationStatus.PENDING)
                        .and("expiresAt").lte(now)
        );

        Update update = new Update()
                .set("status", Reservation.ReservationStatus.EXPIRED);

        mongoTemplate.updateMulti(query, update, Reservation.class);
    }
}