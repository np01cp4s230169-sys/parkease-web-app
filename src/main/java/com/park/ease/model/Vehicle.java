package com.park.ease.model;

import java.sql.Timestamp;

/**
 * Vehicle model class representing a user's registered vehicle in the ParkEase system.
 * Maps to the 'vehicles' table in the database.
 * 
 * Each vehicle belongs to a registered user and is identified by a unique
 * registration number. Vehicles are used when booking parking slots.
 * 
 * Vehicle types: CAR, BIKE, TRUCK
 */
public class Vehicle {

    private int vehicleId;               // Primary key - unique vehicle identifier
    private int userId;                  // Foreign key referencing the users table
    private String registrationNumber;   // Unique vehicle registration/license plate number
    private String vehicleType;          // Type of vehicle: CAR, BIKE, or TRUCK
    private String make;                 // Vehicle manufacturer (e.g., Toyota, Honda)
    private String model;                // Vehicle model name (e.g., Corolla, Civic)
    private String color;                // Vehicle color for identification
    private Timestamp createdAt;         // Timestamp when vehicle was registered

    /** Default constructor required for object instantiation. */
    public Vehicle() {}

    public int getVehicleId() { return vehicleId; }
    public void setVehicleId(int vehicleId) { this.vehicleId = vehicleId; }

    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }

    public String getRegistrationNumber() { return registrationNumber; }
    public void setRegistrationNumber(String registrationNumber) { this.registrationNumber = registrationNumber; }

    public String getVehicleType() { return vehicleType; }
    public void setVehicleType(String vehicleType) { this.vehicleType = vehicleType; }

    public String getMake() { return make; }
    public void setMake(String make) { this.make = make; }

    public String getModel() { return model; }
    public void setModel(String model) { this.model = model; }

    public String getColor() { return color; }
    public void setColor(String color) { this.color = color; }

    public Timestamp getCreatedAt() { return createdAt; }
    public void setCreatedAt(Timestamp createdAt) { this.createdAt = createdAt; }
}