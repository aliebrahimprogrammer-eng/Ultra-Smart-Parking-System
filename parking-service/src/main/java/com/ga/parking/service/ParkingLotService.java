
package com.ga.parking.service;

import com.ga.parking.model.ParkingLot;
import com.ga.parking.repository.ParkingLotRepository;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class ParkingLotService {

    private final ParkingLotRepository parkingLotRepository;

    public ParkingLotService(ParkingLotRepository parkingLotRepository) {
        this.parkingLotRepository = parkingLotRepository;
    }

    public ParkingLot createLot(ParkingLot parkingLot) {

        if (parkingLotRepository.existsByName(parkingLot.getName())) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "A parking lot with this name already exists"
            );
        }

        return parkingLotRepository.save(parkingLot);
    }

    public List<ParkingLot> getAllLots() {
        return parkingLotRepository.findAll();
    }

    public ParkingLot getLotById(String lotId) {

        return parkingLotRepository.findById(lotId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Parking lot not found"
                ));
    }
}
