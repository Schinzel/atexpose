package com.atexpose;

import org.junit.Test;

import java.util.regex.Pattern;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;


/**
 * @author schinzel
 */
public class MiscTest {
    Pattern DATE_TIME_PATTERN = Pattern.compile("20[0-9][0-9]-[01][0-9]-[0-3][0-9] " +
            "[012][0-9]:[0-5][0-9]:[0-5][0-9].[0-9]{3}");


    @Test
    public void testEcho() {
        Misc misc = new Misc();
        String result = misc.echo("monkey");
        assertEquals("monkey", result);
    }


    @Test
    public void testPing() {
        Misc misc = new Misc();
        String result = misc.ping();
        assertEquals("pong", result);
    }


    @Test
    public void testTime() {
        String result = new Misc().time();
        boolean matches = DATE_TIME_PATTERN.matcher(result).matches();
        assertTrue(matches);
    }


    @Test
    public void startTime_NormalCase_CorrectDateTimeFormat() {
        String result = new Misc().startTime();
        boolean matches = DATE_TIME_PATTERN.matcher(result).matches();
        assertTrue(matches);
    }


    @Test
    public void snooze_snooze20ms_shouldSnoozeBetween20And30ms() {
        long start = System.nanoTime();
        new Misc().snooze(20);
        //Calc the time to do all iterations
        long executionTimeInMS = (System.nanoTime() - start) / 1000000;
        assertThat(executionTimeInMS).isBetween(20L, 30L);
    }


    @Test
    public void testThrowError() {
        assertThatExceptionOfType(RuntimeException.class)
                .isThrownBy(() -> new Misc().throwError())
                .withMessageStartingWith("Requested error thrown");
    }

}
