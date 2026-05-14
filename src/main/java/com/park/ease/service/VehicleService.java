package com.park.ease.service;

import com.park.ease.dao.VehicleDAO;
import com.park.ease.daoimpl.VehicleDAOImpl;
import com.park.ease.model.Vehicle;
import java.util.List;

public class VehicleService {

    private VehicleDAO vehicleDAO = new VehicleDAOImpl();

    /**
     * Registers a new vehicle for a user.
     * Business Rule: Ensure registration number is provided.
     */
    public boolean addVehicle(Vehicle vehicle) {
        if (vehicle.getRegistrationNumber() == null || vehicle.getRegistrationNumber().trim().isEmpty()) {
            return false;
        }
        // Normalize reg number to uppercase
        vehicle.setRegistrationNumber(vehicle.getRegistrationNumber().toUpperCase());
        return vehicleDAO.addVehicle(vehicle);
    }

    /**
     * Retrieves all vehicles belonging to a specific user.
     */
    public List<Vehicle> getUserVehicles(int userId) {
        return vehicleDAO.getVehiclesByUserId(userId);
    }

    /**
     * Deletes a vehicle.
     */
    public boolean removeVehicle(int vehicleId) {
        return vehicleDAO.deleteVehicle(vehicleId);
    }
}
