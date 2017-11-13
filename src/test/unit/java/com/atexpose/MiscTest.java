package com.atexpose;

import org.junit.Test;

import java.util.regex.Pattern;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;


/**
 * @author schinzel
 */
public class MiscTest {
    Pattern DATE_TIME_PATTERN = Pattern.compile("20[0-9][0-9]-[01][0-9]-[0-3][0-9] " +
            "[012][0-9]:[0-5][0-9]:[0-5][0-9].[0-9]{3}");


    @Test
    public void testEcho() {
        String result = new Misc().echo("monkey");
        assertThat(result).isEqualTo("monkey");
    }


    @Test
    public void testPing() {
        Misc misc = new Misc();
        String result = misc.ping();
        assertThat(result).isEqualTo("pong");
    }


    @Test
    public void testTime() {
        String result = new Misc().time();
        assertThat(result).matches(DATE_TIME_PATTERN);
    }


    @Test
    public void startTime_NormalCase_CorrectDateTimeFormat() {
        Pattern pattern = Pattern.compile("20[0-9][0-9]-[01][0-9]-[0-3][0-9] " +
                "[012][0-9]:[0-5][0-9]:[0-5][0-9]");
        String result = new Misc().startTime();
        assertThat(result).containsPattern(pattern);
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
