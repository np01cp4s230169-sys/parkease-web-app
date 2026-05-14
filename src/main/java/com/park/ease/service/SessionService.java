package com.park.ease.service;

import com.park.ease.dao.SessionDAO;
import com.park.ease.daoimpl.SessionDAOImpl;

public class SessionService {

    private SessionDAO sessionDAO = new SessionDAOImpl();

    public double getTotalRevenue() {
        return sessionDAO.getTotalRevenue();
    }

    public int getOccupiedSlotsCount() {
        return sessionDAO.getOccupiedSlotsCount();
    }

    public int getTotalSlotsCount() {
        return sessionDAO.getTotalSlotsCount();
    }

    public double getOccupancyRate() {
        int occupied = getOccupiedSlotsCount();
        int total = getTotalSlotsCount();

        if (total == 0) {
            return 0;
        }

        return ((double) occupied / total) * 100;
    }
}