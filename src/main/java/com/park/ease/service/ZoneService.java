package com.park.ease.service;

import com.park.ease.dao.ZoneDAO;
import com.park.ease.daoimpl.ZoneDAOImpl;
import com.park.ease.model.Zone;
import java.util.List;

public class ZoneService {

    private ZoneDAO zoneDAO = new ZoneDAOImpl();

    /**
     * Adds a new parking zone to the system.
     * Business Rule: Ensure zone name is not empty and capacity is positive.
     */
    public boolean createZone(Zone zone) {
        if (zone.getZoneName() == null || zone.getZoneName().isEmpty() || zone.getCapacity() <= 0) {
            return false;
        }
        return zoneDAO.addZone(zone);
    }

    /**
     * Fetches all zones for display on dashboards or dropdowns.
     */
    public List<Zone> getAllZones() {
        return zoneDAO.getAllZones();
    }

    /**
     * Finds a specific zone by its ID.
     */
    public Zone getZoneById(int zoneId) {
        return zoneDAO.getZoneById(zoneId);
    }

    /**
     * Updates existing zone details.
     */
    public boolean updateZoneInfo(Zone zone) {
        return zoneDAO.updateZone(zone);
    }

    /**
     * Deletes a zone from the system.
     */
    public boolean removeZone(int zoneId) {
        return zoneDAO.deleteZone(zoneId);
    }
}
