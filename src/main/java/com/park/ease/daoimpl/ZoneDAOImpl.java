package com.park.ease.daoimpl;

import com.park.ease.dao.ZoneDAO;
import com.park.ease.model.Zone;
import com.park.ease.util.DBConnectionUtil;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ZoneDAOImpl implements ZoneDAO {

    @Override
    public boolean addZone(Zone zone) {
        String sql = "INSERT INTO zones (zone_name, capacity, description) VALUES (?, ?, ?)";
        try (Connection con = DBConnectionUtil.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, zone.getZoneName());
            ps.setInt(2, zone.getCapacity());
            ps.setString(3, zone.getDescription());
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public List<Zone> getAllZones() {
        List<Zone> zones = new ArrayList<>();
        String sql = "SELECT * FROM zones";
        try (Connection con = DBConnectionUtil.getConnection();
             Statement st = con.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                Zone zone = new Zone();
                zone.setZoneId(rs.getInt("zone_id"));
                zone.setZoneName(rs.getString("zone_name"));
                zone.setCapacity(rs.getInt("capacity"));
                zone.setDescription(rs.getString("description"));
                zone.setCreatedAt(rs.getTimestamp("created_at"));
                zones.add(zone);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return zones;
    }

    @Override
    public Zone getZoneById(int zoneId) {
        String sql = "SELECT * FROM zones WHERE zone_id = ?";
        try (Connection con = DBConnectionUtil.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, zoneId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                Zone zone = new Zone();
                zone.setZoneId(rs.getInt("zone_id"));
                zone.setZoneName(rs.getString("zone_name"));
                zone.setCapacity(rs.getInt("capacity"));
                zone.setDescription(rs.getString("description"));
                return zone;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public boolean updateZone(Zone zone) {
        String sql = "UPDATE zones SET zone_name = ?, capacity = ?, description = ? WHERE zone_id = ?";
        try (Connection con = DBConnectionUtil.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, zone.getZoneName());
            ps.setInt(2, zone.getCapacity());
            ps.setString(3, zone.getDescription());
            ps.setInt(4, zone.getZoneId());
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean deleteZone(int zoneId) {
        String sql = "DELETE FROM zones WHERE zone_id = ?";
        try (Connection con = DBConnectionUtil.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, zoneId);
            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
