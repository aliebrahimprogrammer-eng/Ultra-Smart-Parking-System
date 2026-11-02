package com.ga.reservation_service.service;

import com.ga.reservation_service.model.Vehicle;
import com.ga.reservation_service.repository.VehicleRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class VehicleService {

    private final VehicleRepository vehicleRepository;

    public VehicleService(VehicleRepository vehicleRepository) {
        this.vehicleRepository = vehicleRepository;
    }

    public Vehicle addVehicle(Vehicle vehicle) {

        if (vehicleRepository.existsByPlateNumber(vehicle.getPlateNumber())) {
            throw new RuntimeException("Vehicle plate number already exists");
        }

        return vehicleRepository.save(vehicle);
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

        vehicle.setPlateNumber(updatedVehicle.getPlateNumber());
        vehicle.setModel(updatedVehicle.getModel());

        return vehicleRepository.save(vehicle);
    }

    public void deleteVehicle(String id) {

        Vehicle vehicle = getVehicleById(id);
        vehicleRepository.delete(vehicle);
    }
}