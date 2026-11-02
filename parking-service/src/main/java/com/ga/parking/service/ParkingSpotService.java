
package com.ga.parking.service;

import com.ga.parking.concurrency.SpotLockManager;
import com.ga.parking.model.ParkingSpot;
import com.ga.parking.repository.ParkingSpotRepository;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.concurrent.locks.ReentrantLock;

@Service
public class ParkingSpotService {

    private final ParkingSpotRepository parkingSpotRepository;
    private final SpotLockManager spotLockManager;

    public ParkingSpotService(
            ParkingSpotRepository parkingSpotRepository,
            SpotLockManager spotLockManager) {

        this.parkingSpotRepository = parkingSpotRepository;
        this.spotLockManager = spotLockManager;
    }

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
}
