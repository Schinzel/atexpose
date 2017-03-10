package com.atexpose;

import com.atexpose.errors.RuntimeError;
import java.util.regex.Pattern;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.Rule;
import org.junit.rules.ExpectedException;

/**
 *
 * @author schinzel
 */
public class MiscTest {

    @Rule
    public ExpectedException exception = ExpectedException.none();


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
        String regex = "20[0-9][0-9]-[01][0-9]-[0-3][0-9] [012][0-9]:[0-5][0-9]:[0-5][0-9].[0-9]{3}";
        Pattern pattern = Pattern.compile(regex);
        Misc misc = new Misc();
        String result = misc.time();
        boolean matches = pattern.matcher(result).matches();
        assertTrue(matches);
    }


    @Test
    public void testSnooze() {
        Misc misc = new Misc();
        int msToSleepFor = 100;
        String result = misc.snooze(msToSleepFor);
        assertEquals("Good morning! Slept for " + msToSleepFor + " milliseconds", result);
        int nrOfItterations = 20;
        int snoozeTime = 100;
        long start = System.nanoTime();
        for (int i = 0; i < nrOfItterations; i++) {
            misc.snooze(snoozeTime);
        }
        long end = System.nanoTime();
        //Calc the time to do all iterations
        long executionTimeInMS = (end - start) / 1000000;
        //Calc the actual average snooze time
        long averageSnoozeTime = executionTimeInMS / nrOfItterations;
        //True if average snooze time is less than requestion snooze time +10%
        boolean notTenPercentOver = (averageSnoozeTime < (snoozeTime + snoozeTime / 10));
        //True if average snooze time is more than requestion snooze time -10%
        boolean notTenPercentUnder = (averageSnoozeTime > (snoozeTime - snoozeTime / 10));
        //Assert that snooze time is withint plus minus 10%
        assertTrue(notTenPercentOver && notTenPercentUnder);
    }


    @Test
    public void testThrowError() {
        Misc misc = new Misc();
        exception.expect(RuntimeError.class);
        exception.expectMessage("Requested error thrown");
        misc.throwError();

    }

}
