package com.park.ease.daoimpl;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import com.park.ease.dao.SessionDAO;
import com.park.ease.model.ParkingSession;
import com.park.ease.util.DBConnectionUtil;

/**
 * SessionDAOImpl provides JDBC-based implementation of the SessionDAO interface.
 * Handles all database operations for parking sessions in the parking_sessions table.
 * 
 * Uses database transactions with rollback support for startSession and endSession
 * to ensure data consistency between session and slot status updates.
 */
public class SessionDAOImpl implements SessionDAO {

    /**
     * Creates a new parking session and marks the slot as occupied.
     * Uses a transaction to ensure both operations succeed or both are rolled back.
     */
    @Override
    public boolean startSession(ParkingSession session) {
        String insertSessionSql = "INSERT INTO parking_sessions (vehicle_id, slot_id, entry_time, payment_status, status) VALUES (?, ?, CURRENT_TIMESTAMP, 'pending', 'active')";
        String updateSlotSql = "UPDATE parking_slots SET status = 'occupied' WHERE slot_id = ?";

        Connection con = null;
        try {
            con = DBConnectionUtil.getConnection();
            con.setAutoCommit(false);

            // Insert new parking session record
            try (PreparedStatement psSession = con.prepareStatement(insertSessionSql, Statement.RETURN_GENERATED_KEYS)) {
                psSession.setInt(1, session.getVehicleId());
                psSession.setInt(2, session.getSlotId());

                int affectedRows = psSession.executeUpdate();
                if (affectedRows == 0) {
                    throw new SQLException("Creating session failed, no rows affected.");
                }
            }

            // Mark the parking slot as occupied
            try (PreparedStatement psSlot = con.prepareStatement(updateSlotSql)) {
                psSlot.setInt(1, session.getSlotId());
                int slotUpdated = psSlot.executeUpdate();

                if (slotUpdated == 0) {
                    throw new SQLException("Slot update failed, slot may not exist.");
                }
            }

            con.commit();
            return true;

        } catch (SQLException e) {
            // Rollback both operations if any step fails
            if (con != null) {
                try {
                    con.rollback();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
            e.printStackTrace();
            return false;
        } finally {
            closeConnection(con);
        }
    }

    /**
     * Ends an active session by recording exit time, hours, and charges.
     * Releases the slot back to available status using a transaction.
     */
    @Override
    public boolean endSession(int sessionId, ParkingSession session) {
        String updateSessionSql = "UPDATE parking_sessions SET exit_time = CURRENT_TIMESTAMP, total_hours = ?, total_charges = ?, payment_status = 'paid', status = 'completed' WHERE session_id = ?";
        String updateSlotSql = "UPDATE parking_slots SET status = 'available' WHERE slot_id = ?";

        Connection con = null;
        try {
            con = DBConnectionUtil.getConnection();
            con.setAutoCommit(false);

            // Update session with checkout details
            try (PreparedStatement psSession = con.prepareStatement(updateSessionSql)) {
                psSession.setDouble(1, session.getTotalHours());
                psSession.setDouble(2, session.getTotalCharges());
                psSession.setInt(3, sessionId);

                int sessionUpdated = psSession.executeUpdate();
                if (sessionUpdated == 0) {
                    throw new SQLException("Session update failed, session may not exist.");
                }
            }

            // Release the slot back to available status
            try (PreparedStatement psSlot = con.prepareStatement(updateSlotSql)) {
                psSlot.setInt(1, session.getSlotId());

                int slotUpdated = psSlot.executeUpdate();
                if (slotUpdated == 0) {
                    throw new SQLException("Slot release failed, slot may not exist.");
                }
            }

            con.commit();
            return true;

        } catch (SQLException e) {
            // Rollback both operations if any step fails
            if (con != null) {
                try {
                    con.rollback();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
            e.printStackTrace();
            return false;
        } finally {
            closeConnection(con);
        }
    }

    /**
     * Retrieves all parking sessions for a specific vehicle ordered by entry time.
     */
    @Override
    public List<ParkingSession> getSessionsByVehicle(int vehicleId) {
        List<ParkingSession> sessions = new ArrayList<>();
        String sql = "SELECT * FROM parking_sessions WHERE vehicle_id = ? ORDER BY entry_time DESC";

        try (Connection con = DBConnectionUtil.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, vehicleId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    sessions.add(mapResultSetToSession(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return sessions;
    }

    /**
     * Retrieves all currently active parking sessions.
     */
    @Override
    public List<ParkingSession> getActiveSessions() {
        List<ParkingSession> sessions = new ArrayList<>();
        String sql = "SELECT * FROM parking_sessions WHERE status = 'active'";

        try (Connection con = DBConnectionUtil.getConnection();
             Statement st = con.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                sessions.add(mapResultSetToSession(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return sessions;
    }

    /**
     * Retrieves a specific parking session by its unique ID.
     */
    @Override
    public ParkingSession getSessionById(int sessionId) {
        String sql = "SELECT * FROM parking_sessions WHERE session_id = ?";

        try (Connection con = DBConnectionUtil.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, sessionId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToSession(rs);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Retrieves all completed parking sessions ordered by exit time.
     * Used for generating admin reports and receipt downloads.
     */
    @Override
    public List<ParkingSession> getAllCompletedSessions() {
        List<ParkingSession> sessions = new ArrayList<>();
        String sql = "SELECT * FROM parking_sessions WHERE status = 'completed' ORDER BY exit_time DESC";

        try (Connection con = DBConnectionUtil.getConnection();
             Statement st = con.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                sessions.add(mapResultSetToSession(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return sessions;
    }

    /**
     * Calculates total revenue by summing charges from all paid sessions.
     */
    @Override
    public double getTotalRevenue() {
        String sql = "SELECT SUM(total_charges) FROM parking_sessions WHERE payment_status = 'paid'";

        try (Connection con = DBConnectionUtil.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                return rs.getDouble(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0.0;
    }

    /**
     * Returns the count of parking slots with occupied status.
     */
    @Override
    public int getOccupiedSlotsCount() {
        String sql = "SELECT COUNT(*) FROM parking_slots WHERE status = 'occupied'";

        try (Connection con = DBConnectionUtil.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    /**
     * Returns the total count of all parking slots in the system.
     */
    @Override
    public int getTotalSlotsCount() {
        String sql = "SELECT COUNT(*) FROM parking_slots";

        try (Connection con = DBConnectionUtil.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    /**
     * Maps a database ResultSet row to a ParkingSession model object.
     * Reused across all methods that retrieve session records.
     */
    private ParkingSession mapResultSetToSession(ResultSet rs) throws SQLException {
        ParkingSession s = new ParkingSession();
        s.setSessionId(rs.getInt("session_id"));
        s.setVehicleId(rs.getInt("vehicle_id"));
        s.setSlotId(rs.getInt("slot_id"));
        s.setEntryTime(rs.getTimestamp("entry_time"));
        s.setExitTime(rs.getTimestamp("exit_time"));
        s.setTotalHours(rs.getDouble("total_hours"));
        s.setTotalCharges(rs.getDouble("total_charges"));
        s.setPaymentStatus(rs.getString("payment_status"));
        s.setStatus(rs.getString("status"));
        return s;
    }

    /**
     * Safely closes a database connection and resets auto-commit mode.
     * Called in the finally block of transaction methods.
     */
    private void closeConnection(Connection con) {
        if (con != null) {
            try {
                if (!con.getAutoCommit()) {
                    con.setAutoCommit(true);
                }
                con.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}