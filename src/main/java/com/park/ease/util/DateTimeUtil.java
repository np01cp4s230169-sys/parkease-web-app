package com.park.ease.util;

import java.sql.Timestamp;
import java.util.concurrent.TimeUnit;

public class DateTimeUtil {

    /**
     * Calculates hours between entry and exit.
     * Returns a minimum of 1 hour for billing purposes.
     */
    public static double calculateHours(Timestamp entry, Timestamp exit) {
        if (entry == null || exit == null) return 0;
        
        long milliseconds = exit.getTime() - entry.getTime();
        double hours = (double) milliseconds / (1000 * 60 * 60);
        
        // Round up to 1 hour minimum for business logic
        return Math.max(1.0, Math.ceil(hours));
    }
}
