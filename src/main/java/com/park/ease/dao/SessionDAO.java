package com.park.ease.dao;

import com.park.ease.model.ParkingSession;
import java.util.List;

public interface SessionDAO {
    // Existing session management
    boolean startSession(ParkingSession session);
    boolean endSession(int sessionId, ParkingSession session);
    ParkingSession getSessionById(int sessionId);
    List<ParkingSession> getSessionsByVehicle(int vehicleId);
    List<ParkingSession> getActiveSessions();

    // New method for Reports/Receipts functionality
    // Fetches all sessions where status is 'COMPLETED' or 'PAID'
    List<ParkingSession> getAllCompletedSessions();

    // Analytics methods
    double getTotalRevenue();
    int getOccupiedSlotsCount();
    int getTotalSlotsCount();
}