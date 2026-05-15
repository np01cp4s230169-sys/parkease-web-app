package com.park.ease.util;

import java.sql.Timestamp;

/**
 * DateTimeUtil provides utility methods for date and time calculations
 * used across the ParkEase parking management system.
 * 
 * Used primarily for calculating parking duration and billing charges
 * based on entry and exit timestamps.
 */
public class DateTimeUtil {

    /**
     * Calculates the number of hours between entry and exit timestamps.
     * Returns a minimum of 1 hour for billing purposes to ensure
     * short stays are charged fairly.
     * 
     * @param entry the parking session entry timestamp
     * @param exit  the parking session exit timestamp
     * @return hours parked, rounded up with a minimum of 1.0
     */
    public static double calculateHours(Timestamp entry, Timestamp exit) {
        if (entry == null || exit == null) return 0;

        long milliseconds = exit.getTime() - entry.getTime();
        double hours = (double) milliseconds / (1000 * 60 * 60);

        // Round up to nearest hour with a minimum of 1 hour for billing
        return Math.max(1.0, Math.ceil(hours));
    }
}
