package com.ga.parking.config;

import com.ga.parking.model.ParkingLot;
import com.ga.parking.model.ParkingSpot;
import com.ga.parking.model.enums.SpotStatus;
import com.ga.parking.model.enums.SpotType;
import com.ga.parking.repository.ParkingLotRepository;
import com.ga.parking.repository.ParkingSpotRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class DataSeeder implements CommandLineRunner {

    private final ParkingLotRepository lotRepository;
    private final ParkingSpotRepository spotRepository;

    public DataSeeder(
            ParkingLotRepository lotRepository,
            ParkingSpotRepository spotRepository) {
        this.lotRepository = lotRepository;
        this.spotRepository = spotRepository;
    }

    @Override
    public void run(String... args) {

        seedParkingLot("Bahrain Mall Parking", "Sanabis", 0.500, 10);
        seedParkingLot("City Centre Parking", "Seef", 0.750, 10);
    }

    private void seedParkingLot(
            String name,
            String location,
            double hourlyRate,
            int totalSpots) {

        ParkingLot lot = lotRepository.findByName(name)
                .orElseGet(() -> lotRepository.save(
                        ParkingLot.builder()
                                .name(name)
                                .location(location)
                                .hourlyRate(hourlyRate)
                                .totalSpots(totalSpots)
                                .build()
                ));

        for (int i = 1; i <= totalSpots; i++) {

            String spotNumber = "P" + i;

            if (spotRepository.existsByLotIdAndSpotNumber(
                    lot.getId(), spotNumber)) {
                continue;
            }

            SpotType type;

            if (i == 1) {
                type = SpotType.DISABLED;
            } else if (i == 2) {
                type = SpotType.ELECTRIC;
            } else {
                type = SpotType.REGULAR;
            }

            ParkingSpot spot = ParkingSpot.builder()
                    .lotId(lot.getId())
                    .spotNumber(spotNumber)
                    .type(type)
                    .status(SpotStatus.AVAILABLE)
                    .build();

            spotRepository.save(spot);
        }
    }
}