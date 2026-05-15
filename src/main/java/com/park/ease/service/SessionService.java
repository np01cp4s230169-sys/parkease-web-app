package com.park.ease.service;

import com.park.ease.dao.SessionDAO;
import com.park.ease.daoimpl.SessionDAOImpl;
import com.park.ease.model.ParkingSession;
import java.util.List;

public class SessionService {

    private SessionDAO sessionDAO = new SessionDAOImpl();

    // New Method: Fetch details for a specific receipt
    public ParkingSession getSessionDetails(int sessionId) {
        return sessionDAO.getSessionById(sessionId);
    }

    // New Method: Fetch all completed sessions for the reports table
    public List<ParkingSession> getCompletedSessions() {
        return sessionDAO.getAllCompletedSessions();
    }

    public double getTotalRevenue() {
        return sessionDAO.getTotalRevenue();
    }

    public int getOccupiedSlotsCount() {
        return sessionDAO.getOccupiedSlotsCount();
    }

    public int getTotalSlotsCount() {
        return sessionDAO.getTotalSlotsCount();
    }

    public double getOccupancyRate() {
        int occupied = getOccupiedSlotsCount();
        int total = getTotalSlotsCount();

        if (total == 0) {
            return 0;
        }

        return ((double) occupied / total) * 100;
    }
}