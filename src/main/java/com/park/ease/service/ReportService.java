package com.park.ease.service;

import java.util.List;

import com.park.ease.dao.SessionDAO;
import com.park.ease.dao.SlotDAO;
import com.park.ease.daoimpl.SessionDAOImpl;
import com.park.ease.daoimpl.SlotDAOImpl;
import com.park.ease.model.ParkingSession;

/**
 * ReportService handles business logic for generating system reports
 * in the ParkEase admin dashboard.
 * 
 * Provides revenue calculations and slot occupancy statistics
 * by aggregating data from SessionDAO and SlotDAO.
 */
public class ReportService {

    // DAO dependencies for session and slot data operations
    private SessionDAO sessionDAO = new SessionDAOImpl();
    private SlotDAO slotDAO = new SlotDAOImpl();

    /**
     * Calculates total revenue from all completed parking sessions.
     * Sums up total charges from sessions with completed status.
     * 
     * @return total revenue as a double value
     */
    public double getTotalRevenue() {
        List<ParkingSession> completedSessions = sessionDAO.getAllCompletedSessions();
        return completedSessions.stream()
                .mapToDouble(ParkingSession::getTotalCharges)
                .sum();
    }

    /**
     * Retrieves the count of currently occupied parking slots.
     * 
     * @return number of slots with occupied status
     */
    public long getOccupiedCount() {
        return slotDAO.getAllSlots().stream()
                .filter(s -> "occupied".equalsIgnoreCase(s.getStatus()))
                .count();
    }
}