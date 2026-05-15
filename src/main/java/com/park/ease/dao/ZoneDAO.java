package com.park.ease.dao;

import java.util.List;

import com.park.ease.model.Zone;

/**
 * ZoneDAO defines the data access contract for parking zone database operations.
 * Implemented by ZoneDAOImpl using JDBC and MySQL.
 */
public interface ZoneDAO {

    /** Adds a new parking zone record to the database. */
    boolean addZone(Zone zone);

    /** Retrieves all parking zones from the database. */
    List<Zone> getAllZones();

    /** Retrieves a specific zone by its unique ID. */
    Zone getZoneById(int zoneId);

    /** Updates an existing zone record (name, capacity, description). */
    boolean updateZone(Zone zone);

    /** Deletes a zone record by its ID. */
    boolean deleteZone(int zoneId);
}