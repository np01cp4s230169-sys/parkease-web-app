package com.park.ease.dao;

import com.park.ease.model.Zone;
import java.util.List;

public interface ZoneDAO {
    boolean addZone(Zone zone);
    List<Zone> getAllZones();
    Zone getZoneById(int zoneId);
    boolean updateZone(Zone zone);
    boolean deleteZone(int zoneId);
}
