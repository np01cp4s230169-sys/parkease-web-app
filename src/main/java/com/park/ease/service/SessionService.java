package com.park.ease.service;

import java.util.List;

import com.park.ease.dao.SessionDAO;
import com.park.ease.daoimpl.SessionDAOImpl;
import com.park.ease.model.ParkingSession;

/**
 * SessionService handles business logic for parking session reporting
 * and statistics in the ParkEase system.
 * 
 * Provides data for the admin reports page including revenue,
 * occupancy rates, and completed session history.
 */
public class SessionService {

    // DAO dependency for session data operations
    private SessionDAO sessionDAO = new SessionDAOImpl();

    /**
     * Retrieves details of a specific parking session by ID.
     * Used for generating parking receipts.
     * 
     * @param sessionId the ID of the session to retrieve
     * @return ParkingSession object or null if not found
     */
    public ParkingSession getSessionDetails(int sessionId) {
        return sessionDAO.getSessionById(sessionId);
    }

    /**
     * Retrieves all completed parking sessions for report display.
     * 
     * @return list of completed ParkingSession objects
     */
    public List<ParkingSession> getCompletedSessions() {
        return sessionDAO.getAllCompletedSessions();
    }

    /**
     * Calculates total revenue from all completed parking sessions.
     * 
     * @return total revenue as a double value
     */
    public double getTotalRevenue() {
        return sessionDAO.getTotalRevenue();
    }

    /**
     * Retrieves the count of currently occupied parking slots.
     * 
     * @return number of occupied slots
     */
    public int getOccupiedSlotsCount() {
        return sessionDAO.getOccupiedSlotsCount();
    }

    /**
     * Retrieves the total number of parking slots in the system.
     * 
     * @return total slot count
     */
    public int getTotalSlotsCount() {
        return sessionDAO.getTotalSlotsCount();
    }

    /**
     * Calculates the current occupancy rate as a percentage.
     * Returns 0 if no slots exist to avoid division by zero.
     * 
     * @return occupancy rate as a percentage (0 to 100)
     */
    public double getOccupancyRate() {
        int occupied = getOccupiedSlotsCount();
        int total = getTotalSlotsCount();

        if (total == 0) {
            return 0;
        }

        return ((double) occupied / total) * 100;
    }
}