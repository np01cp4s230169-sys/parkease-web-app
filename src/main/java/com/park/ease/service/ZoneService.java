package com.park.ease.service;

import java.util.List;

import com.park.ease.dao.ZoneDAO;
import com.park.ease.daoimpl.ZoneDAOImpl;
import com.park.ease.model.Zone;

/**
 * ZoneService handles all business logic for parking zone management.
 * Validates zone data before passing to the DAO layer for database operations.
 */
public class ZoneService {

    // DAO dependency for zone data operations
    private ZoneDAO zoneDAO = new ZoneDAOImpl();

    /**
     * Creates a new parking zone after validating name and capacity.
     * 
     * @param zone the Zone object with name, capacity, and description
     * @return true if zone was created successfully, false if validation fails
     */
    public boolean createZone(Zone zone) {
        if (zone.getZoneName() == null || zone.getZoneName().trim().isEmpty()
                || zone.getCapacity() <= 0) {
            return false;
        }
        return zoneDAO.addZone(zone);
    }

    /**
     * Retrieves all parking zones for display on dashboards and dropdowns.
     * 
     * @return list of all Zone objects
     */
    public List<Zone> getAllZones() {
        return zoneDAO.getAllZones();
    }

    /**
     * Retrieves a specific zone by its ID.
     * 
     * @param zoneId the ID of the zone to retrieve
     * @return Zone object or null if not found
     */
    public Zone getZoneById(int zoneId) {
        return zoneDAO.getZoneById(zoneId);
    }

    /**
     * Updates an existing zone's name, capacity, and description.
     * 
     * @param zone the Zone object with updated details
     * @return true if update was successful, false otherwise
     */
    public boolean updateZoneInfo(Zone zone) {
        return zoneDAO.updateZone(zone);
    }

    /**
     * Removes a zone from the system by its ID.
     * 
     * @param zoneId the ID of the zone to remove
     * @return true if deletion was successful, false otherwise
     */
    public boolean removeZone(int zoneId) {
        return zoneDAO.deleteZone(zoneId);
    }
}