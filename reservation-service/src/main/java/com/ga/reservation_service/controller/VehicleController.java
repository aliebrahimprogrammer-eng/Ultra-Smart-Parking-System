package com.ga.reservation_service.controller;

import com.ga.reservation_service.model.Vehicle;
import com.ga.reservation_service.security.CurrentUser;
import com.ga.reservation_service.service.VehicleService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/vehicles")
@CrossOrigin
public class VehicleController {

    private final VehicleService vehicleService;

    public VehicleController(VehicleService vehicleService) {
        this.vehicleService = vehicleService;
    }

    @PostMapping
    public Vehicle addVehicle(@RequestBody Vehicle vehicle) {
        // A client-sent id would overwrite someone else's vehicle on save
        vehicle.setId(null);
        vehicle.setUserId(CurrentUser.getEmail());
        return vehicleService.addVehicle(vehicle);
    }

    @GetMapping
    public List<Vehicle> getAllVehicles() {
        return vehicleService.getAllVehicles();
    }

    @GetMapping("/user/{userId}")
    public List<Vehicle> getUserVehicles(@PathVariable String userId) {
        CurrentUser.checkOwner(userId);
        return vehicleService.getUserVehicles(userId);
    }

    @GetMapping("/{id}")
    public Vehicle getVehicleById(@PathVariable String id) {
        Vehicle vehicle = vehicleService.getVehicleById(id);
        CurrentUser.checkOwner(vehicle.getUserId());
        return vehicle;
    }

    @PutMapping("/{id}")
    public Vehicle updateVehicle(
            @PathVariable String id,
            @RequestBody Vehicle vehicle) {
        CurrentUser.checkOwner(vehicleService.getVehicleById(id).getUserId());
        return vehicleService.updateVehicle(id, vehicle);
    }

    @DeleteMapping("/{id}")
    public String deleteVehicle(@PathVariable String id) {
        CurrentUser.checkOwner(vehicleService.getVehicleById(id).getUserId());
        vehicleService.deleteVehicle(id);
        return "Vehicle deleted successfully";
    }
}
