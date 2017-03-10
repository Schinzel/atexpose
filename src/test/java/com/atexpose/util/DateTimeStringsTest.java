package com.atexpose.util;

import java.time.Instant;
import java.util.regex.Pattern;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * @author schinzel
 */
public class DateTimeStringsTest extends DateTimeStrings {
    private static final String YEAR_MONTH_DAY = "20[0-9][0-9]-[01][0-9]-[0-3][0-9]";
    private static final String YEAR_MONTH_DAY_HOUR_MINUTE_SECOND = YEAR_MONTH_DAY + " [012][0-9]:[0-5][0-9]:[0-5][0-9]";
    private static final String YEAR_MONTH_DAY_HOUR_MINUTE_SECOND_MS = YEAR_MONTH_DAY_HOUR_MINUTE_SECOND + ".[0-9]{3}";


    @Test
    public void testGetDateTimeUTC() {
        Pattern pattern = Pattern.compile(YEAR_MONTH_DAY_HOUR_MINUTE_SECOND_MS);
        String result = DateTimeStrings.getDateTimeUTC(Instant.now());
        boolean matches = pattern.matcher(result).matches();
        assertTrue(matches);
    }


    @Test
    public void testGetDateTimeUTC_NoArguments() {
        Pattern pattern = Pattern.compile(YEAR_MONTH_DAY_HOUR_MINUTE_SECOND_MS);
        String result = DateTimeStrings.getDateTimeUTC();
        boolean matches = pattern.matcher(result).matches();
        assertTrue(matches);
    }


}
