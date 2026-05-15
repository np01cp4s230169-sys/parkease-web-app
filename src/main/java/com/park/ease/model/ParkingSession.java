package com.park.ease.model;

import java.sql.Timestamp;

/**
 * Model class for ParkingSession following the JavaBean pattern.
 * Mapped to the parking_sessions table in the database.
 */
public class ParkingSession {
    private int sessionId;
    private int vehicleId;
    private int slotId;
    private Timestamp entryTime;
    private Timestamp exitTime;
    private double totalHours;
    private double totalCharges;
    private String paymentStatus; // e.g., PENDING, PAID
    private String status;        // e.g., ACTIVE, COMPLETED
    private Timestamp createdAt;

    // Default Constructor (Required for JavaBeans)
    public ParkingSession() {}

    // Parameterized Constructor (Useful for DAO implementation)
    public ParkingSession(int sessionId, int vehicleId, int slotId, Timestamp entryTime, 
                          Timestamp exitTime, double totalHours, double totalCharges, 
                          String paymentStatus, String status) {
        this.sessionId = sessionId;
        this.vehicleId = vehicleId;
        this.slotId = slotId;
        this.entryTime = entryTime;
        this.exitTime = exitTime;
        this.totalHours = totalHours;
        this.totalCharges = totalCharges;
        this.paymentStatus = paymentStatus;
        this.status = status;
    }

    // Getters and Setters
    public int getSessionId() { return sessionId; }
    public void setSessionId(int sessionId) { this.sessionId = sessionId; }

    public int getVehicleId() { return vehicleId; }
    public void setVehicleId(int vehicleId) { this.vehicleId = vehicleId; }

    public int getSlotId() { return slotId; }
    public void setSlotId(int slotId) { this.slotId = slotId; }

    public Timestamp getEntryTime() { return entryTime; }
    public void setEntryTime(Timestamp entryTime) { this.entryTime = entryTime; }

    public Timestamp getExitTime() { return exitTime; }
    public void setExitTime(Timestamp exitTime) { this.exitTime = exitTime; }

    public double getTotalHours() { return totalHours; }
    public void setTotalHours(double totalHours) { this.totalHours = totalHours; }

    public double getTotalCharges() { return totalCharges; }
    public void setTotalCharges(double totalCharges) { this.totalCharges = totalCharges; }

    public String getPaymentStatus() { return paymentStatus; }
    public void setPaymentStatus(String paymentStatus) { this.paymentStatus = paymentStatus; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public Timestamp getCreatedAt() { return createdAt; }
    public void setCreatedAt(Timestamp createdAt) { this.createdAt = createdAt; }

    // Optional: Overriding toString() for easier debugging in the Service Layer
    @Override
    public String toString() {
        return "ParkingSession [ID=" + sessionId + ", Vehicle=" + vehicleId + 
               ", Amount=$" + totalCharges + ", Status=" + status + "]";
    }
}