package com.atexpose.util;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

/**
 * The purpose of this class is to return time as various format as strings.
 *
 * @author Schinzel
 */
public class DateTimeStrings {
    /**
     * The format of the date time with milliseconds.
     */
    private static final DateTimeFormatter DATE_TIME_FORMAT_WITH_MS = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS");
    //------------------------------------------------------------------------
    // UTC
    //------------------------------------------------------------------------


    /**
     * @return Now in UTC as a formatted string.
     */
    public static String getDateTimeUTC() {
        return DateTimeStrings.getDateTimeUTC(Instant.now());
    }


    /**
     * @param instant The instant to format to string.
     * @return Get the argument instant as formatted string. , e.g. 2015-01-08
     * 19:02:59.997
     */
    public static String getDateTimeUTC(Instant instant) {
        return LocalDateTime.ofInstant(instant, ZoneId.of("UTC")).format(DATE_TIME_FORMAT_WITH_MS);
    }

}
