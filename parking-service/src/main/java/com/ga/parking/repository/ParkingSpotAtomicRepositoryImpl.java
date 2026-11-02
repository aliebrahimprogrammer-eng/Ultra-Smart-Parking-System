
package com.ga.parking.repository;

import com.ga.parking.model.ParkingSpot;
import com.ga.parking.model.enums.SpotStatus;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.FindAndModifyOptions;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Repository;

@Repository
public class ParkingSpotAtomicRepositoryImpl
        implements ParkingSpotAtomicRepository {

    private final MongoTemplate mongoTemplate;

    public ParkingSpotAtomicRepositoryImpl(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    @Override
    public ParkingSpot reserveAtomically(String spotId) {
        return updateStatus(
                spotId,
                SpotStatus.AVAILABLE,
                SpotStatus.RESERVED
        );
    }

    @Override
    public ParkingSpot occupyAtomically(String spotId) {
        return updateStatus(
                spotId,
                SpotStatus.RESERVED,
                SpotStatus.OCCUPIED
        );
    }

    @Override
    public ParkingSpot releaseAtomically(String spotId) {
        return updateStatus(
                spotId,
                SpotStatus.OCCUPIED,
                SpotStatus.AVAILABLE
        );
    }

    private ParkingSpot updateStatus(
            String spotId,
            SpotStatus currentStatus,
            SpotStatus newStatus) {

        Query query = new Query(
                Criteria.where("_id").is(spotId)
                        .and("status").is(currentStatus)
        );

        Update update = new Update()
                .set("status", newStatus)
                .inc("version", 1);

        FindAndModifyOptions options = FindAndModifyOptions.options()
                .returnNew(true);

        return mongoTemplate.findAndModify(
                query,
                update,
                options,
                ParkingSpot.class
        );
    }
}
