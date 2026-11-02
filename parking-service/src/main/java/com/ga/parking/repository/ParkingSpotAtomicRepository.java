
package com.ga.parking.repository;

import com.ga.parking.model.ParkingSpot;

public interface ParkingSpotAtomicRepository {

    ParkingSpot reserveAtomically(String spotId);

    ParkingSpot occupyAtomically(String spotId);

    ParkingSpot releaseAtomically(String spotId);

    ParkingSpot cancelReservationAtomically(String spotId);
}
