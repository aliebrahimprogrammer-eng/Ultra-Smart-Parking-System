
package com.ga.parking.repository;

import com.ga.parking.model.ParkingLot;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface ParkingLotRepository
        extends MongoRepository<ParkingLot, String> {

    boolean existsByName(String name);
}
