package com.park.ease.dao;

import com.park.ease.model.Vehicle;
import java.util.List;

public interface VehicleDAO {
    boolean addVehicle(Vehicle vehicle);
    List<Vehicle> getVehiclesByUserId(int userId);
    Vehicle getVehicleById(int vehicleId);
    boolean deleteVehicle(int vehicleId);
}
