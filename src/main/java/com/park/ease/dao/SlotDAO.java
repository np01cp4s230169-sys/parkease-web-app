package com.park.ease.dao;

import com.park.ease.model.ParkingSlot;
import java.util.List;
import java.util.Map;

public interface SlotDAO {
    boolean addSlot(ParkingSlot slot);
    List<ParkingSlot> getAllSlots();
    ParkingSlot getSlotById(int slotId);
    List<ParkingSlot> getSlotsByZone(int zoneId);
    boolean updateSlot(ParkingSlot slot);
    boolean updateSlotStatus(int slotId, String status);
    boolean deleteSlot(int slotId);
    Map<String, Integer> getDashboardStats();

    List<ParkingSlot> searchAvailableSlots(Integer zoneId, String vehicleType, String slotNumber);
}