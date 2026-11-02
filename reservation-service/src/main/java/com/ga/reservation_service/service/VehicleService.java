package com.ga.reservation_service.service;

import com.ga.reservation_service.model.Vehicle;
import com.ga.reservation_service.repository.VehicleRepository;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class VehicleService {

    private final VehicleRepository vehicleRepository;

    public VehicleService(VehicleRepository vehicleRepository) {
        this.vehicleRepository = vehicleRepository;
    }

    public Vehicle addVehicle(Vehicle vehicle) {

        vehicle.setPlateNumber(
                vehicle.getPlateNumber().trim().toUpperCase()
        );

        try {
            return vehicleRepository.save(vehicle);
        } catch (DuplicateKeyException e) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Vehicle plate number already exists"
            );
        }
    }

    public List<Vehicle> getAllVehicles() {
        return vehicleRepository.findAll();
    }

    public List<Vehicle> getUserVehicles(String userId) {
        return vehicleRepository.findByUserId(userId);
    }

    public Vehicle getVehicleById(String id) {
        return vehicleRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Vehicle not found"));
    }

    public Vehicle updateVehicle(String id, Vehicle updatedVehicle) {

        Vehicle vehicle = getVehicleById(id);

        vehicle.setPlateNumber(
                updatedVehicle.getPlateNumber().trim().toUpperCase()
        );

        vehicle.setModel(updatedVehicle.getModel());

        try {
            return vehicleRepository.save(vehicle);
        } catch (DuplicateKeyException e) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Vehicle plate number already exists"
            );
        }
    }

    public void deleteVehicle(String id) {

        Vehicle vehicle = getVehicleById(id);
        vehicleRepository.delete(vehicle);
    }
}