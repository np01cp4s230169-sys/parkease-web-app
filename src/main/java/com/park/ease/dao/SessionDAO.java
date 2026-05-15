package com.park.ease.dao;

import java.util.List;

import com.park.ease.model.ParkingSession;

/**
 * SessionDAO defines the data access contract for parking session database operations.
 * Implemented by SessionDAOImpl using JDBC and MySQL.
 * 
 * Manages the lifecycle of parking sessions from booking (start)
 * through checkout (end), and provides analytics data for reports.
 */
public interface SessionDAO {

    /** Creates a new active parking session when a slot is booked. */
    boolean startSession(ParkingSession session);

    /** Ends an active session on checkout, recording hours and charges. */
    boolean endSession(int sessionId, ParkingSession session);

    /** Retrieves a specific parking session by its unique ID. */
    ParkingSession getSessionById(int sessionId);

    /** Retrieves all parking sessions associated with a specific vehicle. */
    List<ParkingSession> getSessionsByVehicle(int vehicleId);

    /** Retrieves all currently active parking sessions. */
    List<ParkingSession> getActiveSessions();

    /** Retrieves all completed parking sessions for reports and receipts. */
    List<ParkingSession> getAllCompletedSessions();

    /** Calculates and returns total revenue from all completed sessions. */
    double getTotalRevenue();

    /** Returns the number of currently occupied parking slots. */
    int getOccupiedSlotsCount();

    /** Returns the total number of parking slots in the system. */
    int getTotalSlotsCount();
}