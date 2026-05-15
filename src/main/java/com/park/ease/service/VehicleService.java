package com.park.ease.service;

import java.util.List;

import com.park.ease.dao.VehicleDAO;
import com.park.ease.daoimpl.VehicleDAOImpl;
import com.park.ease.model.Vehicle;

/**
 * VehicleService handles all business logic for vehicle management
 * in the ParkEase system.
 * 
 * Validates vehicle data and normalizes registration numbers
 * to uppercase before passing to the DAO layer.
 */
public class VehicleService {

    // DAO dependency for vehicle data operations
    private VehicleDAO vehicleDAO = new VehicleDAOImpl();

    /**
     * Registers a new vehicle for a user.
     * Validates registration number is provided and normalizes
     * it to uppercase before saving.
     * 
     * @param vehicle the Vehicle object with userId, regNumber, type, make, model, color
     * @return true if vehicle was registered successfully, false otherwise
     */
    public boolean addVehicle(Vehicle vehicle) {
        if (vehicle == null || vehicle.getRegistrationNumber() == null
                || vehicle.getRegistrationNumber().trim().isEmpty()) {
            return false;
        }

        // Normalize registration number to uppercase for consistency
        vehicle.setRegistrationNumber(vehicle.getRegistrationNumber().trim().toUpperCase());
        return vehicleDAO.addVehicle(vehicle);
    }

    /**
     * Retrieves all vehicles registered by a specific user.
     * 
     * @param userId the ID of the user whose vehicles to retrieve
     * @return list of Vehicle objects belonging to the user
     */
    public List<Vehicle> getUserVehicles(int userId) {
        return vehicleDAO.getVehiclesByUserId(userId);
    }

    /**
     * Removes a vehicle record from the system.
     * 
     * @param vehicleId the ID of the vehicle to remove
     * @return true if deletion was successful, false otherwise
     */
    public boolean removeVehicle(int vehicleId) {
        return vehicleDAO.deleteVehicle(vehicleId);
    }
}