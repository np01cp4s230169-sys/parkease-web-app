package com.park.ease.dao;

import com.park.ease.model.ParkingSession;
import java.util.List;

public interface SessionDAO {
    boolean startSession(ParkingSession session);
    boolean endSession(int sessionId, ParkingSession session);
    ParkingSession getSessionById(int sessionId);
    List<ParkingSession> getSessionsByVehicle(int vehicleId);
    List<ParkingSession> getActiveSessions();

    double getTotalRevenue();
    int getOccupiedSlotsCount();
    int getTotalSlotsCount();
}