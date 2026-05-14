package com.park.ease.service;

import com.park.ease.dao.SessionDAO;
import com.park.ease.daoimpl.SessionDAOImpl;
import com.park.ease.dao.SlotDAO;
import com.park.ease.daoimpl.SlotDAOImpl;
import com.park.ease.model.ParkingSession;
import java.util.List;

public class ReportService {
    private SessionDAO sessionDAO = new SessionDAOImpl();
    private SlotDAO slotDAO = new SlotDAOImpl();

    public double getTotalRevenue() {
        List<ParkingSession> allSessions = sessionDAO.getActiveSessions(); // For simplicity, we fetch all
        // In a real app, you'd use a specific 'getAllCompletedSessions' DAO method
        return allSessions.stream().mapToDouble(ParkingSession::getTotalCharges).sum();
    }

    public long getOccupiedCount() {
        return slotDAO.getAllSlots().stream()
                .filter(s -> "OCCUPIED".equals(s.getStatus()))
                .count();
    }
}
