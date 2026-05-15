package com.park.ease.model;

import java.sql.Timestamp;

/**
 * ParkingSession model class representing an active or completed parking session
 * in the ParkEase system. Maps to the 'parking_sessions' table in the database.
 * 
 * A session is created when a user books a slot and is updated when they check out.
 * Total hours and charges are calculated using DateTimeUtil and the slot's hourly rate.
 * 
 * Status values: active (currently parked), completed (checked out)
 * Payment status values: pending, paid
 */
public class ParkingSession {

    private int sessionId;          // Primary key - unique session identifier
    private int vehicleId;          // Foreign key referencing the vehicles table
    private int slotId;             // Foreign key referencing the parking_slots table
    private Timestamp entryTime;    // Timestamp when vehicle entered the parking slot
    private Timestamp exitTime;     // Timestamp when vehicle exited (null if still active)
    private double totalHours;      // Total hours parked, calculated on checkout
    private double totalCharges;    // Total charge based on hours and slot hourly rate
    private String paymentStatus;   // Payment status: pending or paid
    private String status;          // Session status: active or completed
    private Timestamp createdAt;    // Timestamp when session record was created

    /** Default constructor required for JavaBean and object instantiation. */
    public ParkingSession() {}

    /**
     * Parameterized constructor for convenient session creation in DAO layer.
     * 
     * @param sessionId     unique session identifier
     * @param vehicleId     ID of the vehicle using the slot
     * @param slotId        ID of the parking slot being used
     * @param entryTime     time vehicle entered the slot
     * @param exitTime      time vehicle exited (null if still active)
     * @param totalHours    total hours parked
     * @param totalCharges  total amount charged for the session
     * @param paymentStatus payment status: pending or paid
     * @param status        session status: active or completed
     */
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

    /**
     * Returns a summary string of the session for debugging purposes.
     */
    @Override
    public String toString() {
        return "ParkingSession [ID=" + sessionId + ", Vehicle=" + vehicleId +
               ", Amount=Rs." + totalCharges + ", Status=" + status + "]";
    }
}