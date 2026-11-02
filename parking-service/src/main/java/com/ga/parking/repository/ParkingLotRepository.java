
package com.ga.parking.repository;

import com.ga.parking.model.ParkingLot;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface ParkingLotRepository
        extends MongoRepository<ParkingLot, String> {

    boolean existsByName(String name);

    Optional<ParkingLot> findByName(String name);
}
