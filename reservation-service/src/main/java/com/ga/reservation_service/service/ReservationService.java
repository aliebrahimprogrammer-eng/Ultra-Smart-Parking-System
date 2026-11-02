package com.ga.reservation_service.service;

import com.ga.reservation_service.model.Payment;
import com.ga.reservation_service.model.Reservation;
import com.ga.reservation_service.repository.PaymentRepository;
import com.ga.reservation_service.repository.ReservationRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ReservationService {

    private final ReservationRepository reservationRepository;
    private final PaymentRepository paymentRepository;
    private final PaymentService paymentService;

    public ReservationService(
            ReservationRepository reservationRepository,
            PaymentRepository paymentRepository,
            PaymentService paymentService) {

        this.reservationRepository = reservationRepository;
        this.paymentRepository = paymentRepository;
        this.paymentService = paymentService;
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

        Reservation reservation = getReservationById(reservationId);

        if (reservation.getStatus() != Reservation.ReservationStatus.PENDING) {
            throw new RuntimeException("Reservation is not pending");
        }

        if (reservation.getExpiresAt().isBefore(LocalDateTime.now())) {
            reservation.setStatus(Reservation.ReservationStatus.EXPIRED);
            reservationRepository.save(reservation);

            throw new RuntimeException("Reservation has expired");
        }

        Payment payment = paymentService.getPaymentByReservation(reservationId);

        if (payment.getStatus() != Payment.PaymentStatus.SUCCESSFUL) {
            throw new RuntimeException("Payment has not been confirmed");
        }

        reservation.setStatus(Reservation.ReservationStatus.CONFIRMED);

        return reservationRepository.save(reservation);
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

        Reservation reservation = getReservationById(id);

        if (reservation.getStatus() == Reservation.ReservationStatus.ACTIVE
                || reservation.getStatus() == Reservation.ReservationStatus.COMPLETED) {
            throw new RuntimeException("This reservation cannot be cancelled");
        }

        reservation.setStatus(Reservation.ReservationStatus.CANCELLED);

        return reservationRepository.save(reservation);
    }

    @Scheduled(fixedRate = 60000)
    public void expirePendingReservations() {

        List<Reservation> pendingReservations =
                reservationRepository.findByStatus(
                        Reservation.ReservationStatus.PENDING
                );

        LocalDateTime now = LocalDateTime.now();

        for (Reservation reservation : pendingReservations) {

            if (reservation.getExpiresAt() != null
                    && reservation.getExpiresAt().isBefore(now)) {

                reservation.setStatus(Reservation.ReservationStatus.EXPIRED);
                reservationRepository.save(reservation);
            }
        }
    }
}