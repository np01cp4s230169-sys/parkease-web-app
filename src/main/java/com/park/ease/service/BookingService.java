package com.park.ease.service;

import com.park.ease.dao.SessionDAO;
import com.park.ease.daoimpl.SessionDAOImpl;
import com.park.ease.model.ParkingSession;
import java.util.List;

public class BookingService {

    private SessionDAO sessionDAO = new SessionDAOImpl();

    /**
     * Handles the full booking process.
     */
    public boolean bookSlot(int vehicleId, int slotId) {
        ParkingSession session = new ParkingSession();
        session.setVehicleId(vehicleId);
        session.setSlotId(slotId);

        return sessionDAO.startSession(session);
    }

    /**
     * Handles checkout process.
     */
    public boolean releaseSlot(int sessionId, int slotId, double hours, double charges) {
        ParkingSession session = new ParkingSession();
        session.setSlotId(slotId);
        session.setTotalHours(hours);
        session.setTotalCharges(charges);

        return sessionDAO.endSession(sessionId, session);
    }

    public List<ParkingSession> getUserBookingHistory(int vehicleId) {
        return sessionDAO.getSessionsByVehicle(vehicleId);
    }

    public ParkingSession getSessionDetails(int sessionId) {
        return sessionDAO.getSessionById(sessionId);
    }
}