package com.park.ease.model;

import java.sql.Timestamp;

/**
 * Zone model class representing a designated parking area in the ParkEase system.
 * Maps to the 'zones' table in the database.
 * 
 * A Zone is a named parking area (e.g., Zone A, Zone B) with a defined
 * capacity. Each Zone contains multiple ParkingSlots managed by the admin.
 */
public class Zone {

    private int zoneId;          // Primary key - unique zone identifier
    private String zoneName;     // Name of the zone (e.g., Zone A, North Wing)
    private int capacity;        // Maximum number of parking slots in this zone
    private String description;  // Optional description of the zone location or rules
    private Timestamp createdAt; // Timestamp when the zone was created

    /** Default constructor required for object instantiation. */
    public Zone() {}

    public int getZoneId() { return zoneId; }
    public void setZoneId(int zoneId) { this.zoneId = zoneId; }

    public String getZoneName() { return zoneName; }
    public void setZoneName(String zoneName) { this.zoneName = zoneName; }

    public int getCapacity() { return capacity; }
    public void setCapacity(int capacity) { this.capacity = capacity; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public Timestamp getCreatedAt() { return createdAt; }
    public void setCreatedAt(Timestamp createdAt) { this.createdAt = createdAt; }
}