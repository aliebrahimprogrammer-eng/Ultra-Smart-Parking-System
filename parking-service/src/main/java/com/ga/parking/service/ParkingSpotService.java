
package com.ga.parking.service;

import com.ga.parking.concurrency.SpotLockManager;
import com.ga.parking.model.ParkingSpot;
import com.ga.parking.model.enums.SpotStatus;
import com.ga.parking.repository.ParkingLotRepository;
import com.ga.parking.repository.ParkingSpotRepository;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.concurrent.locks.ReentrantLock;

@Service
public class ParkingSpotService {

    private final ParkingSpotRepository parkingSpotRepository;
    private final ParkingLotRepository parkingLotRepository;
    private final SpotLockManager spotLockManager;

    public ParkingSpotService(
            ParkingSpotRepository parkingSpotRepository,
            ParkingLotRepository parkingLotRepository,
            SpotLockManager spotLockManager) {

        this.parkingSpotRepository = parkingSpotRepository;
        this.parkingLotRepository = parkingLotRepository;
        this.spotLockManager = spotLockManager;
    }

    // Create a new parking spot
    public ParkingSpot createSpot(ParkingSpot parkingSpot) {

        if (!parkingLotRepository.existsById(parkingSpot.getLotId())) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "Parking lot not found"
            );
        }

        if (parkingSpotRepository.existsByLotIdAndSpotNumber(
                parkingSpot.getLotId(),
                parkingSpot.getSpotNumber())) {

            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "This spot number already exists in this parking lot"
            );
        }

        parkingSpot.setId(null);
        parkingSpot.setStatus(SpotStatus.AVAILABLE);

        return parkingSpotRepository.save(parkingSpot);
    }

    // Get all spots in a parking lot
    public List<ParkingSpot> getSpotsByLot(String lotId) {

        if (!parkingLotRepository.existsById(lotId)) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "Parking lot not found"
            );
        }

        return parkingSpotRepository.findByLotId(lotId);
    }

    // Get only available spots in a parking lot
    public List<ParkingSpot> getAvailableSpots(String lotId) {

        if (!parkingLotRepository.existsById(lotId)) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "Parking lot not found"
            );
        }

        return parkingSpotRepository.findByLotIdAndStatus(
                lotId,
                SpotStatus.AVAILABLE
        );
    }

    // Update a parking spot without changing its status
    public ParkingSpot updateSpot(String spotId, ParkingSpot updatedSpot) {

        ReentrantLock lock = spotLockManager.getLock(spotId);
        lock.lock();

        try {
            ParkingSpot existingSpot = parkingSpotRepository.findById(spotId)
                    .orElseThrow(() -> new ResponseStatusException(
                            HttpStatus.NOT_FOUND,
                            "Parking spot not found"
                    ));

            if (!parkingLotRepository.existsById(updatedSpot.getLotId())) {
                throw new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Parking lot not found"
                );
            }

            if (parkingSpotRepository.existsByLotIdAndSpotNumberAndIdNot(
                    updatedSpot.getLotId(),
                    updatedSpot.getSpotNumber(),
                    spotId)) {

                throw new ResponseStatusException(
                        HttpStatus.CONFLICT,
                        "This spot number already exists in this parking lot"
                );
            }

            // Update only administrative fields.
            // Preserve ID, status, and version.
            existingSpot.setLotId(updatedSpot.getLotId());
            existingSpot.setSpotNumber(updatedSpot.getSpotNumber());
            existingSpot.setType(updatedSpot.getType());

            return parkingSpotRepository.save(existingSpot);

        } finally {
            lock.unlock();
        }
    }

    // Delete a parking spot only when AVAILABLE
    public void deleteSpot(String spotId) {

        ReentrantLock lock = spotLockManager.getLock(spotId);
        lock.lock();

        try {
            long deletedCount = parkingSpotRepository.deleteByIdAndStatus(
                    spotId,
                    SpotStatus.AVAILABLE
            );

            if (deletedCount == 0) {

                if (!parkingSpotRepository.existsById(spotId)) {
                    throw new ResponseStatusException(
                            HttpStatus.NOT_FOUND,
                            "Parking spot not found"
                    );
                }

                throw new ResponseStatusException(
                        HttpStatus.CONFLICT,
                        "Cannot delete a parking spot that is RESERVED or OCCUPIED"
                );
            }

        } finally {
            lock.unlock();
        }
    }

    // Reserve a spot using Java locking and MongoDB atomic update
    public ParkingSpot reserveSpot(String spotId) {

        ReentrantLock lock = spotLockManager.getLock(spotId);
        lock.lock();

        try {
            ParkingSpot reservedSpot =
                    parkingSpotRepository.reserveAtomically(spotId);

            if (reservedSpot == null) {
                throw new ResponseStatusException(
                        HttpStatus.CONFLICT,
                        "Parking spot is unavailable or does not exist"
                );
            }

            return reservedSpot;

        } finally {
            lock.unlock();
        }
    }

    // Mark a reserved spot as occupied
    public ParkingSpot occupySpot(String spotId) {

        ReentrantLock lock = spotLockManager.getLock(spotId);
        lock.lock();

        try {
            ParkingSpot occupiedSpot =
                    parkingSpotRepository.occupyAtomically(spotId);

            if (occupiedSpot == null) {
                throw new ResponseStatusException(
                        HttpStatus.CONFLICT,
                        "Spot must be reserved before it can be occupied"
                );
            }

            return occupiedSpot;

        } finally {
            lock.unlock();
        }
    }

    // Release an occupied spot
    public ParkingSpot releaseSpot(String spotId) {

        ReentrantLock lock = spotLockManager.getLock(spotId);
        lock.lock();

        try {
            ParkingSpot releasedSpot =
                    parkingSpotRepository.releaseAtomically(spotId);

            if (releasedSpot == null) {
                throw new ResponseStatusException(
                        HttpStatus.CONFLICT,
                        "Spot must be occupied before it can be released"
                );
            }

            return releasedSpot;

        } finally {
            lock.unlock();
        }
    }

    // Cancel a reservation
    public ParkingSpot cancelReservation(String spotId) {

        ReentrantLock lock = spotLockManager.getLock(spotId);
        lock.lock();

        try {
            ParkingSpot spot =
                    parkingSpotRepository.cancelReservationAtomically(spotId);

            if (spot == null) {
                throw new ResponseStatusException(
                        HttpStatus.CONFLICT,
                        "Parking spot is not currently reserved"
                );
            }

            return spot;

        } finally {
            lock.unlock();
        }
    }
}
