
package com.ga.parking.service;

import com.ga.parking.model.ParkingLot;
import com.ga.parking.repository.ParkingLotRepository;
import com.ga.parking.repository.ParkingSpotRepository;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class ParkingLotService {

    private final ParkingLotRepository parkingLotRepository;
    private final ParkingSpotRepository parkingSpotRepository;

    public ParkingLotService(
            ParkingLotRepository parkingLotRepository,
            ParkingSpotRepository parkingSpotRepository) {

        this.parkingLotRepository = parkingLotRepository;
        this.parkingSpotRepository = parkingSpotRepository;
    }

    // Create a parking lot
    public ParkingLot createLot(ParkingLot parkingLot) {

        if (parkingLotRepository.existsByName(parkingLot.getName())) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "A parking lot with this name already exists"
            );
        }

        return parkingLotRepository.save(parkingLot);
    }

    // Get all parking lots
    public List<ParkingLot> getAllLots() {
        return parkingLotRepository.findAll();
    }

    // Get a parking lot by ID
    public ParkingLot getLotById(String lotId) {

        return parkingLotRepository.findById(lotId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Parking lot not found"
                ));
    }

    // Update a parking lot
    public ParkingLot updateLot(String lotId, ParkingLot updatedLot) {

        ParkingLot existingLot = getLotById(lotId);

        if (parkingLotRepository.existsByNameAndIdNot(
                updatedLot.getName(), lotId)) {

            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "A parking lot with this name already exists"
            );
        }

        existingLot.setName(updatedLot.getName());
        existingLot.setLocation(updatedLot.getLocation());
        existingLot.setHourlyRate(updatedLot.getHourlyRate());
        existingLot.setTotalSpots(updatedLot.getTotalSpots());

        return parkingLotRepository.save(existingLot);
    }

    // Delete a parking lot only if it contains no spots
    public void deleteLot(String lotId) {

        ParkingLot existingLot = getLotById(lotId);

        if (parkingSpotRepository.existsByLotId(lotId)) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Cannot delete parking lot because it still contains parking spots"
            );
        }

        parkingLotRepository.delete(existingLot);
    }
}
