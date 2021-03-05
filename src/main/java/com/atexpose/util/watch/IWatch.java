package com.atexpose.util.watch;

import java.time.Instant;
import java.time.ZoneId;

/**
 * The purpose of this interface is to return the time now.
 */

public interface IWatch {
    ZoneId UTC = ZoneId.of("UTC");
    ZoneId NEW_YORK = ZoneId.of("America/New_York");
    ZoneId STOCKHOLM = ZoneId.of("Europe/Stockholm");

    /**
     * @return The time now as an instant.
     */
    Instant getNowAsInstant();
}