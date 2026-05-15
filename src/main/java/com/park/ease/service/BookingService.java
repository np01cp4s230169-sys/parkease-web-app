package com.park.ease.service;

import java.util.List;

import com.park.ease.dao.SessionDAO;
import com.park.ease.daoimpl.SessionDAOImpl;
import com.park.ease.model.ParkingSession;

/**
 * BookingService handles the business logic for parking slot booking
 * and session management in the ParkEase system.
 * 
 * Coordinates between the controller and SessionDAO to create,
 * complete, and retrieve parking sessions.
 */
public class BookingService {

    // DAO dependency for parking session data operations
    private SessionDAO sessionDAO = new SessionDAOImpl();

    /**
     * Handles the full booking process by creating a new parking session.
     * Associates the given vehicle with the selected parking slot.
     * 
     * @param vehicleId ID of the vehicle being parked
     * @param slotId    ID of the parking slot being booked
     * @return true if booking was successful, false otherwise
     */
    public boolean bookSlot(int vehicleId, int slotId) {
        if (vehicleId <= 0 || slotId <= 0) {
            return false;
        }

        ParkingSession session = new ParkingSession();
        session.setVehicleId(vehicleId);
        session.setSlotId(slotId);
        return sessionDAO.startSession(session);
    }

    /**
     * Handles the checkout process by ending an active parking session.
     * Records total hours and charges, and releases the slot back to available.
     * 
     * @param sessionId ID of the active parking session
     * @param slotId    ID of the slot to release
     * @param hours     total hours parked
     * @param charges   total amount charged for the session
     * @return true if checkout was successful, false otherwise
     */
    public boolean releaseSlot(int sessionId, int slotId, double hours, double charges) {
        if (sessionId <= 0 || slotId <= 0) {
            return false;
        }

        ParkingSession session = new ParkingSession();
        session.setSlotId(slotId);
        session.setTotalHours(hours);
        session.setTotalCharges(charges);
        return sessionDAO.endSession(sessionId, session);
    }

    /**
     * Retrieves the booking history for a specific vehicle.
     * 
     * @param vehicleId ID of the vehicle
     * @return list of parking sessions for the vehicle
     */
    public List<ParkingSession> getUserBookingHistory(int vehicleId) {
        return sessionDAO.getSessionsByVehicle(vehicleId);
    }

    /**
     * Retrieves details of a specific parking session by ID.
     * 
     * @param sessionId ID of the parking session
     * @return ParkingSession object or null if not found
     */
    public ParkingSession getSessionDetails(int sessionId) {
        return sessionDAO.getSessionById(sessionId);
    }
}