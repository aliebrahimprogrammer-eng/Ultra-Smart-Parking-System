
package com.ga.parking.repository;

import com.ga.parking.model.ParkingSpot;

public interface ParkingSpotAtomicRepository {

    ParkingSpot reserveAtomically(String spotId);
}
