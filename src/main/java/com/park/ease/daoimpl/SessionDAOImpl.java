package com.park.ease.daoimpl;

import com.park.ease.dao.SessionDAO;
import com.park.ease.model.ParkingSession;
import com.park.ease.util.DBConnectionUtil;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class SessionDAOImpl implements SessionDAO {

    @Override
    public boolean startSession(ParkingSession session) {
        String insertSessionSql = "INSERT INTO parking_sessions (vehicle_id, slot_id, entry_time, payment_status, status) VALUES (?, ?, CURRENT_TIMESTAMP, 'pending', 'active')";
        String updateSlotSql = "UPDATE parking_slots SET status = 'occupied' WHERE slot_id = ?";

        Connection con = null;
        try {
            con = DBConnectionUtil.getConnection();
            con.setAutoCommit(false);

            try (PreparedStatement psSession = con.prepareStatement(insertSessionSql, Statement.RETURN_GENERATED_KEYS)) {
                psSession.setInt(1, session.getVehicleId());
                psSession.setInt(2, session.getSlotId());

                int affectedRows = psSession.executeUpdate();
                if (affectedRows == 0) {
                    throw new SQLException("Creating session failed.");
                }
            }

            try (PreparedStatement psSlot = con.prepareStatement(updateSlotSql)) {
                psSlot.setInt(1, session.getSlotId());
                int slotUpdated = psSlot.executeUpdate();
                
                if(slotUpdated == 0) {
                    throw new SQLException("Slot is not available or update failed");
                }
            }

            con.commit();
            return true;

        } catch (SQLException e) {
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

    @Override
    public boolean endSession(int sessionId, ParkingSession session) {
        String updateSessionSql = "UPDATE parking_sessions SET exit_time = CURRENT_TIMESTAMP, total_hours = ?, total_charges = ?, payment_status = 'paid', status = 'completed' WHERE session_id = ?";
        String updateSlotSql = "UPDATE parking_slots SET status = 'available' WHERE slot_id = ?";

        Connection con = null;
        try {
            con = DBConnectionUtil.getConnection();
            con.setAutoCommit(false);

            try (PreparedStatement psSession = con.prepareStatement(updateSessionSql)) {
                psSession.setDouble(1, session.getTotalHours());
                psSession.setDouble(2, session.getTotalCharges());
                psSession.setInt(3, sessionId);

                int sessionUpdated = psSession.executeUpdate();
                if (sessionUpdated == 0) {
                    throw new SQLException("Updating session failed.");
                }
            }

            try (PreparedStatement psSlot = con.prepareStatement(updateSlotSql)) {
                psSlot.setInt(1, session.getSlotId());

                int slotUpdated = psSlot.executeUpdate();
                if (slotUpdated == 0) {
                    throw new SQLException("Updating slot availability failed.");
                }
            }

            con.commit();
            return true;

        } catch (SQLException e) {
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

    // ──────────────────────────────────────────────────────────────────────────
    // ⬇️ NEW METHOD: This fixes the "must implement inherited method" error
    // ──────────────────────────────────────────────────────────────────────────
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

    private void closeConnection(Connection con) {
        if (con != null) {
            try {
                // Reset auto-commit before closing if it was changed
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
