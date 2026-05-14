package com.park.ease.service;

import com.park.ease.dao.SlotDAO;
import com.park.ease.dao.ZoneDAO;
import com.park.ease.daoimpl.SlotDAOImpl;
import com.park.ease.daoimpl.ZoneDAOImpl;
import com.park.ease.model.ParkingSlot;
import com.park.ease.model.Zone;

import java.util.List;
import java.util.Map;

public class SlotService {

    private SlotDAO slotDAO = new SlotDAOImpl();
    private ZoneDAO zoneDAO = new ZoneDAOImpl();

    public boolean addParkingSlot(ParkingSlot slot) {
        if (slot == null) {
            return false;
        }

        Zone zone = zoneDAO.getZoneById(slot.getZoneId());

        if (zone == null) {
            return false;
        }

        List<ParkingSlot> currentSlots = slotDAO.getSlotsByZone(slot.getZoneId());

        if (currentSlots.size() < zone.getCapacity()) {
            if (slot.getStatus() == null || slot.getStatus().isBlank()) {
                slot.setStatus("available");
            }
            return slotDAO.addSlot(slot);
        }

        return false;
    }

    public List<ParkingSlot> getAllSlots() {
        return slotDAO.getAllSlots();
    }

    public ParkingSlot getSlotById(int slotId) {
        return slotDAO.getSlotById(slotId);
    }

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

        Zone zone = zoneDAO.getZoneById(slot.getZoneId());
        ParkingSlot existingSlot = slotDAO.getSlotById(slot.getSlotId());

        if (zone == null || existingSlot == null) {
            return false;
        }

        if (slot.getStatus() == null || slot.getStatus().isBlank()) {
            slot.setStatus(existingSlot.getStatus() != null ? existingSlot.getStatus() : "available");
        }

        boolean zoneChanged = existingSlot.getZoneId() != slot.getZoneId();

        if (zoneChanged) {
            List<ParkingSlot> currentSlots = slotDAO.getSlotsByZone(slot.getZoneId());
            if (currentSlots.size() >= zone.getCapacity()) {
                return false;
            }
        }

        return slotDAO.updateSlot(slot);
    }

    public boolean deleteParkingSlot(int slotId) {
        return slotDAO.deleteSlot(slotId);
    }

    public List<ParkingSlot> getAvailableSlotsByZone(int zoneId) {
        return slotDAO.getSlotsByZone(zoneId).stream()
                .filter(s -> "AVAILABLE".equalsIgnoreCase(s.getStatus()) || "available".equalsIgnoreCase(s.getStatus()))
                .toList();
    }

    public List<ParkingSlot> searchAvailableSlots(Integer zoneId, String vehicleType, String slotNumber) {
        return slotDAO.searchAvailableSlots(zoneId, vehicleType, slotNumber);
    }

    public boolean updateStatus(int slotId, String status) {
        return slotDAO.updateSlotStatus(slotId, status);
    }

    public Map<String, Integer> getDashboardStats() {
        return slotDAO.getDashboardStats();
    }
}