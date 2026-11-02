
package com.ga.parking.repository;

import com.ga.parking.model.ParkingSpot;
import com.ga.parking.model.enums.SpotStatus;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface ParkingSpotRepository
        extends MongoRepository<ParkingSpot, String>,
        ParkingSpotAtomicRepository {

    List<ParkingSpot> findByLotId(String lotId);

    List<ParkingSpot> findByLotIdAndStatus(
            String lotId,
            SpotStatus status
    );

    boolean existsByLotIdAndSpotNumber(
            String lotId,
            String spotNumber
    );
}
