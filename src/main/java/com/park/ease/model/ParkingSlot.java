package com.park.ease.model;

import java.sql.Timestamp;

/**
 * ParkingSlot model class representing an individual parking slot in the ParkEase system.
 * Maps to the 'parking_slots' table in the database.
 * 
 * Each slot belongs to a Zone and has a unique slot number within that zone.
 * Slots are assigned a vehicle type and hourly rate by the admin.
 * 
 * Status values: available, occupied, maintenance
 * Vehicle types: CAR, BIKE, TRUCK
 */
public class ParkingSlot {

    private int slotId;          // Primary key - unique slot identifier
    private int zoneId;          // Foreign key referencing the zones table
    private String slotNumber;   // Unique slot identifier within a zone (e.g., A1, B3)
    private String vehicleType;  // Type of vehicle this slot accommodates: CAR, BIKE, TRUCK
    private double hourlyRate;   // Charge per hour for using this slot
    private String status;       // Current slot status: available, occupied, or maintenance
    private Timestamp createdAt; // Timestamp when slot was created
    private Timestamp updatedAt; // Timestamp when slot was last updated

    /** Default constructor required for object instantiation. */
    public ParkingSlot() {}

    public int getSlotId() { return slotId; }
    public void setSlotId(int slotId) { this.slotId = slotId; }

    public int getZoneId() { return zoneId; }
    public void setZoneId(int zoneId) { this.zoneId = zoneId; }

    public String getSlotNumber() { return slotNumber; }
    public void setSlotNumber(String slotNumber) { this.slotNumber = slotNumber; }

    public String getVehicleType() { return vehicleType; }
    public void setVehicleType(String vehicleType) { this.vehicleType = vehicleType; }

    public double getHourlyRate() { return hourlyRate; }
    public void setHourlyRate(double hourlyRate) { this.hourlyRate = hourlyRate; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public Timestamp getCreatedAt() { return createdAt; }
    public void setCreatedAt(Timestamp createdAt) { this.createdAt = createdAt; }

    public Timestamp getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(Timestamp updatedAt) { this.updatedAt = updatedAt; }
}