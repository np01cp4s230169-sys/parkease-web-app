package com.park.ease.dao;

import java.util.List;

import com.park.ease.model.Vehicle;

/**
 * VehicleDAO defines the data access contract for vehicle database operations.
 * Implemented by VehicleDAOImpl using JDBC and MySQL.
 */
public interface VehicleDAO {

    /** Adds a new vehicle record associated with a user. */
    boolean addVehicle(Vehicle vehicle);

    /** Retrieves all vehicles registered by a specific user. */
    List<Vehicle> getVehiclesByUserId(int userId);

    /** Retrieves a specific vehicle by its unique ID. */
    Vehicle getVehicleById(int vehicleId);

    /** Deletes a vehicle record by its ID. */
    boolean deleteVehicle(int vehicleId);
}