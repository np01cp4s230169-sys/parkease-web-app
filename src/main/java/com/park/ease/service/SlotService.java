package com.park.ease.service;

import java.util.List;
import java.util.Map;

import com.park.ease.dao.SlotDAO;
import com.park.ease.dao.ZoneDAO;
import com.park.ease.daoimpl.SlotDAOImpl;
import com.park.ease.daoimpl.ZoneDAOImpl;
import com.park.ease.model.ParkingSlot;
import com.park.ease.model.Zone;

/**
 * SlotService handles all business logic related to parking slot management.
 * Enforces zone capacity rules when adding or moving slots between zones.
 * 
 * Coordinates between SlotDAO and ZoneDAO to validate zone capacity
 * before allowing slot creation or zone reassignment.
 */
public class SlotService {

    // DAO dependencies for slot and zone data operations
    private SlotDAO slotDAO = new SlotDAOImpl();
    private ZoneDAO zoneDAO = new ZoneDAOImpl();

    /**
     * Adds a new parking slot to a zone.
     * Validates that the zone exists and has not reached its capacity
     * before creating the new slot record.
     * 
     * @param slot the ParkingSlot object with zone, number, type, rate, and status
     * @return true if slot was added successfully, false if zone is full or invalid
     */
    public boolean addParkingSlot(ParkingSlot slot) {
        if (slot == null) {
            return false;
        }

        // Validate zone exists before adding slot
        Zone zone = zoneDAO.getZoneById(slot.getZoneId());
        if (zone == null) {
            return false;
        }

        // Check zone has not reached maximum capacity
        List<ParkingSlot> currentSlots = slotDAO.getSlotsByZone(slot.getZoneId());
        if (currentSlots.size() < zone.getCapacity()) {
            if (slot.getStatus() == null || slot.getStatus().isBlank()) {
                slot.setStatus("available");
            }
            return slotDAO.addSlot(slot);
        }

        return false;
    }

    /**
     * Retrieves all parking slots in the system.
     * 
     * @return list of all ParkingSlot objects
     */
    public List<ParkingSlot> getAllSlots() {
        return slotDAO.getAllSlots();
    }

    /**
     * Retrieves a specific parking slot by its ID.
     * 
     * @param slotId the ID of the slot to retrieve
     * @return ParkingSlot object or null if not found
     */
    public ParkingSlot getSlotById(int slotId) {
        return slotDAO.getSlotById(slotId);
    }

    /**
     * Updates an existing parking slot record.
     * Validates slot data, checks zone capacity when changing zones,
     * and preserves existing status if none is provided.
     * 
     * @param slot the updated ParkingSlot object
     * @return true if update was successful, false if validation fails
     */
    public boolean updateParkingSlot(ParkingSlot slot) {
        if (slot == null || slot.getSlotId() <= 0) {
            return false;
        }

        if (slot.getSlotNumber() == null || slot.getSlotNumber().isBlank()) {
            return false;
        }

        if (slot.getHourlyRate() < 0) {
            return false;
        }

        // Validate both zone and existing slot exist
        Zone zone = zoneDAO.getZoneById(slot.getZoneId());
        ParkingSlot existingSlot = slotDAO.getSlotById(slot.getSlotId());

        if (zone == null || existingSlot == null) {
            return false;
        }

        // Preserve existing status if none provided in update
        if (slot.getStatus() == null || slot.getStatus().isBlank()) {
            slot.setStatus(existingSlot.getStatus() != null ? existingSlot.getStatus() : "available");
        }

        // Check new zone capacity if slot is being moved to a different zone
        boolean zoneChanged = existingSlot.getZoneId() != slot.getZoneId();
        if (zoneChanged) {
            List<ParkingSlot> currentSlots = slotDAO.getSlotsByZone(slot.getZoneId());
            if (currentSlots.size() >= zone.getCapacity()) {
                return false;
            }
        }

        return slotDAO.updateSlot(slot);
    }

    /**
     * Deletes a parking slot by its ID.
     * 
     * @param slotId the ID of the slot to delete
     * @return true if deletion was successful, false otherwise
     */
    public boolean deleteParkingSlot(int slotId) {
        return slotDAO.deleteSlot(slotId);
    }

    /**
     * Retrieves all available parking slots in a specific zone.
     * Filters out occupied and maintenance slots for user booking.
     * 
     * @param zoneId the ID of the zone to filter by
     * @return list of available ParkingSlot objects in the zone
     */
    public List<ParkingSlot> getAvailableSlotsByZone(int zoneId) {
        return slotDAO.getSlotsByZone(zoneId).stream()
                .filter(s -> "available".equalsIgnoreCase(s.getStatus()))
                .toList();
    }

    /**
     * Searches for available slots using optional filter criteria.
     * Supports filtering by zone, vehicle type, and slot number.
     * 
     * @param zoneId      optional zone filter (null to search all zones)
     * @param vehicleType optional vehicle type filter
     * @param slotNumber  optional slot number filter
     * @return list of matching available ParkingSlot objects
     */
    public List<ParkingSlot> searchAvailableSlots(Integer zoneId, String vehicleType, String slotNumber) {
        return slotDAO.searchAvailableSlots(zoneId, vehicleType, slotNumber);
    }

    /**
     * Updates the status of a specific parking slot.
     * Used during booking and checkout to mark slots as occupied or available.
     * 
     * @param slotId the ID of the slot to update
     * @param status the new status: available, occupied, or maintenance
     * @return true if update was successful, false otherwise
     */
    public boolean updateStatus(int slotId, String status) {
        return slotDAO.updateSlotStatus(slotId, status);
    }

    /**
     * Retrieves dashboard statistics including total, available, and occupied slot counts.
     * Used by the admin dashboard to display system overview.
     * 
     * @return map of statistic labels to their integer values
     */
    public Map<String, Integer> getDashboardStats() {
        return slotDAO.getDashboardStats();
    }
}