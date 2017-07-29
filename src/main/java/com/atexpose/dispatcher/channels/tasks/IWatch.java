package com.atexpose.dispatcher.channels.tasks;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;

/**
 * The purpose of this interface is to return the time now.
 */

public interface IWatch {
    ZoneId UTC = ZoneId.of("UTC");


    /**
     * @return The time now as an instant.
     */
    Instant getInstant();


    /**
     * @param zoneId The zone id of the local date time to return
     * @return The time now as local date time
     */
    default LocalDate getLocalDate(ZoneId zoneId) {
        return LocalDateTime.ofInstant(this.getInstant(), zoneId).toLocalDate();
    }


}
