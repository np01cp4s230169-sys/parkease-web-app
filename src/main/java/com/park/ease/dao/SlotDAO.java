package com.park.ease.dao;

import java.util.List;
import java.util.Map;

import com.park.ease.model.ParkingSlot;

/**
 * SlotDAO defines the data access contract for parking slot database operations.
 * Implemented by SlotDAOImpl using JDBC and MySQL.
 */
public interface SlotDAO {

    /** Adds a new parking slot record to the database. */
    boolean addSlot(ParkingSlot slot);

    /** Retrieves all parking slots across all zones. */
    List<ParkingSlot> getAllSlots();

    /** Retrieves a specific parking slot by its unique ID. */
    ParkingSlot getSlotById(int slotId);

    /** Retrieves all parking slots belonging to a specific zone. */
    List<ParkingSlot> getSlotsByZone(int zoneId);

    /** Updates an existing parking slot record. */
    boolean updateSlot(ParkingSlot slot);

    /** Updates only the status field of a parking slot. */
    boolean updateSlotStatus(int slotId, String status);

    /** Deletes a parking slot record by its ID. */
    boolean deleteSlot(int slotId);

    /** Retrieves dashboard statistics: total, available, and occupied slot counts. */
    Map<String, Integer> getDashboardStats();

    /** Searches for available slots filtered by zone, vehicle type, and slot number. */
    List<ParkingSlot> searchAvailableSlots(Integer zoneId, String vehicleType, String slotNumber);
}