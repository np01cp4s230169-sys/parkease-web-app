package com.park.ease.daoimpl;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import com.park.ease.dao.VehicleDAO;
import com.park.ease.model.Vehicle;
import com.park.ease.util.DBConnectionUtil;

/**
 * VehicleDAOImpl provides JDBC-based implementation of the VehicleDAO interface.
 * Handles all database operations for vehicle records in the vehicles table.
 */
public class VehicleDAOImpl implements VehicleDAO {

    /**
     * Inserts a new vehicle record into the vehicles table.
     */
    @Override
    public boolean addVehicle(Vehicle vehicle) {
        String sql = "INSERT INTO vehicles (user_id, registration_number, vehicle_type, make, model, color) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection con = DBConnectionUtil.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, vehicle.getUserId());
            ps.setString(2, vehicle.getRegistrationNumber());
            ps.setString(3, vehicle.getVehicleType());
            ps.setString(4, vehicle.getMake());
            ps.setString(5, vehicle.getModel());
            ps.setString(6, vehicle.getColor());
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Retrieves all vehicles registered by a specific user.
     * Uses try-with-resources to ensure ResultSet is properly closed.
     */
    @Override
    public List<Vehicle> getVehiclesByUserId(int userId) {
        List<Vehicle> vehicles = new ArrayList<>();
        String sql = "SELECT * FROM vehicles WHERE user_id = ?";
        try (Connection con = DBConnectionUtil.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, userId);
            // Use try-with-resources for ResultSet to prevent resource leak
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    vehicles.add(mapResultSetToVehicle(rs));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return vehicles;
    }

    /**
     * Retrieves a specific vehicle by its unique ID.
     * Uses try-with-resources to ensure ResultSet is properly closed.
     */
    @Override
    public Vehicle getVehicleById(int vehicleId) {
        String sql = "SELECT * FROM vehicles WHERE vehicle_id = ?";
        try (Connection con = DBConnectionUtil.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, vehicleId);
            // Use try-with-resources for ResultSet to prevent resource leak
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToVehicle(rs);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Deletes a vehicle record by its ID.
     */
    @Override
    public boolean deleteVehicle(int vehicleId) {
        String sql = "DELETE FROM vehicles WHERE vehicle_id = ?";
        try (Connection con = DBConnectionUtil.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, vehicleId);
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Maps a database ResultSet row to a Vehicle model object.
     * Reused across all methods that retrieve vehicle records.
     */
    private Vehicle mapResultSetToVehicle(ResultSet rs) throws SQLException {
        Vehicle v = new Vehicle();
        v.setVehicleId(rs.getInt("vehicle_id"));
        v.setUserId(rs.getInt("user_id"));
        v.setRegistrationNumber(rs.getString("registration_number"));
        v.setVehicleType(rs.getString("vehicle_type"));
        v.setMake(rs.getString("make"));
        v.setModel(rs.getString("model"));
        v.setColor(rs.getString("color"));
        v.setCreatedAt(rs.getTimestamp("created_at"));
        return v;
    }
}