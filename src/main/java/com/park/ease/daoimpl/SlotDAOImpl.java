package com.park.ease.daoimpl;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.park.ease.dao.SlotDAO;
import com.park.ease.model.ParkingSlot;
import com.park.ease.util.DBConnectionUtil;

/**
 * SlotDAOImpl provides JDBC-based implementation of the SlotDAO interface.
 * Handles all database operations for parking slot records in the parking_slots table.
 * 
 * Uses PreparedStatements with dynamic query building for flexible slot searching.
 */
public class SlotDAOImpl implements SlotDAO {

    /**
     * Inserts a new parking slot record into the parking_slots table.
     * Checks for duplicate slot number within the same zone before inserting.
     */
    @Override
    public boolean addSlot(ParkingSlot slot) {
        // Check if slot number already exists in the same zone
        String checkSql = "SELECT COUNT(*) FROM parking_slots WHERE zone_id = ? AND LOWER(slot_number) = LOWER(?)";
        try (Connection con = DBConnectionUtil.getConnection();
             PreparedStatement checkPs = con.prepareStatement(checkSql)) {
            checkPs.setInt(1, slot.getZoneId());
            checkPs.setString(2, slot.getSlotNumber());
            try (ResultSet rs = checkPs.executeQuery()) {
                if (rs.next() && rs.getInt(1) > 0) {
                    return false;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }

        String sql = "INSERT INTO parking_slots (zone_id, slot_number, vehicle_type, hourly_rate, status) VALUES (?, ?, ?, ?, ?)";
        try (Connection con = DBConnectionUtil.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, slot.getZoneId());
            ps.setString(2, slot.getSlotNumber());
            ps.setString(3, slot.getVehicleType());
            ps.setDouble(4, slot.getHourlyRate());
            ps.setString(5, slot.getStatus());
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Retrieves all parking slots ordered by zone and slot number.
     */
    @Override
    public List<ParkingSlot> getAllSlots() {
        List<ParkingSlot> slots = new ArrayList<>();
        String sql = "SELECT * FROM parking_slots ORDER BY zone_id, slot_number";
        try (Connection con = DBConnectionUtil.getConnection();
             Statement st = con.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                slots.add(mapResultSetToSlot(rs));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return slots;
    }

    /**
     * Retrieves all parking slots belonging to a specific zone.
     */
    @Override
    public List<ParkingSlot> getSlotsByZone(int zoneId) {
        List<ParkingSlot> slots = new ArrayList<>();
        String sql = "SELECT * FROM parking_slots WHERE zone_id = ?";
        try (Connection con = DBConnectionUtil.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, zoneId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    slots.add(mapResultSetToSlot(rs));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return slots;
    }

    /**
     * Retrieves a specific parking slot by its unique ID.
     */
    @Override
    public ParkingSlot getSlotById(int slotId) {
        String sql = "SELECT * FROM parking_slots WHERE slot_id = ?";
        try (Connection con = DBConnectionUtil.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, slotId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToSlot(rs);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Updates only the status field of a parking slot and records the update timestamp.
     */
    @Override
    public boolean updateSlotStatus(int slotId, String status) {
        String sql = "UPDATE parking_slots SET status = ?, updated_at = CURRENT_TIMESTAMP WHERE slot_id = ?";
        try (Connection con = DBConnectionUtil.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, status);
            ps.setInt(2, slotId);
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Updates all fields of an existing parking slot record.
     */
    @Override
    public boolean updateSlot(ParkingSlot slot) {
        String sql = "UPDATE parking_slots SET zone_id = ?, slot_number = ?, vehicle_type = ?, hourly_rate = ?, status = ?, updated_at = CURRENT_TIMESTAMP WHERE slot_id = ?";
        try (Connection con = DBConnectionUtil.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, slot.getZoneId());
            ps.setString(2, slot.getSlotNumber());
            ps.setString(3, slot.getVehicleType());
            ps.setDouble(4, slot.getHourlyRate());
            ps.setString(5, slot.getStatus());
            ps.setInt(6, slot.getSlotId());
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Deletes a parking slot record by its ID.
     */
    @Override
    public boolean deleteSlot(int slotId) {
        String sql = "DELETE FROM parking_slots WHERE slot_id = ?";
        try (Connection con = DBConnectionUtil.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, slotId);
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Searches for available slots using optional filter criteria.
     * Dynamically builds SQL query based on provided filters.
     * Supports filtering by zone ID, vehicle type, and slot number (partial match).
     */
    @Override
    public List<ParkingSlot> searchAvailableSlots(Integer zoneId, String vehicleType, String slotNumber) {
        List<ParkingSlot> slots = new ArrayList<>();
        StringBuilder sql = new StringBuilder("SELECT * FROM parking_slots WHERE status = 'available'");
        List<Object> params = new ArrayList<>();

        // Append optional filter conditions dynamically
        if (zoneId != null && zoneId > 0) {
            sql.append(" AND zone_id = ?");
            params.add(zoneId);
        }
        if (vehicleType != null && !vehicleType.trim().isEmpty()) {
            sql.append(" AND vehicle_type = ?");
            params.add(vehicleType.trim().toLowerCase());
        }
        if (slotNumber != null && !slotNumber.trim().isEmpty()) {
            sql.append(" AND LOWER(slot_number) LIKE ?");
            params.add("%" + slotNumber.trim().toLowerCase() + "%");
        }
        sql.append(" ORDER BY zone_id, slot_number");

        try (Connection con = DBConnectionUtil.getConnection();
             PreparedStatement ps = con.prepareStatement(sql.toString())) {
            for (int i = 0; i < params.size(); i++) {
                ps.setObject(i + 1, params.get(i));
            }
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    slots.add(mapResultSetToSlot(rs));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return slots;
    }

    /**
     * Retrieves dashboard statistics using a single SQL query.
     * Returns counts of available slots, occupied slots, and pending user approvals.
     */
    @Override
    public Map<String, Integer> getDashboardStats() {
        Map<String, Integer> stats = new HashMap<>();
        String sql = "SELECT " +
                     "(SELECT COUNT(*) FROM parking_slots WHERE status = 'available') as available, " +
                     "(SELECT COUNT(*) FROM parking_slots WHERE status = 'occupied') as occupied, " +
                     "(SELECT COUNT(*) FROM users WHERE status = 'pending') as pending_users";

        try (Connection con = DBConnectionUtil.getConnection();
             Statement st = con.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            if (rs.next()) {
                stats.put("availableSlots", rs.getInt("available"));
                stats.put("occupiedSlots", rs.getInt("occupied"));
                stats.put("pendingApprovals", rs.getInt("pending_users"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return stats;
    }

    /**
     * Maps a database ResultSet row to a ParkingSlot model object.
     * Reused across all methods that retrieve slot records.
     */
    private ParkingSlot mapResultSetToSlot(ResultSet rs) throws SQLException {
        ParkingSlot slot = new ParkingSlot();
        slot.setSlotId(rs.getInt("slot_id"));
        slot.setZoneId(rs.getInt("zone_id"));
        slot.setSlotNumber(rs.getString("slot_number"));
        slot.setVehicleType(rs.getString("vehicle_type"));
        slot.setHourlyRate(rs.getDouble("hourly_rate"));
        slot.setStatus(rs.getString("status"));
        slot.setCreatedAt(rs.getTimestamp("created_at"));
        slot.setUpdatedAt(rs.getTimestamp("updated_at"));
        return slot;
    }
}
