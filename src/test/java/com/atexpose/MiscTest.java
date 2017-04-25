package com.atexpose;

import com.atexpose.errors.RuntimeError;
import io.schinzel.basicutils.Sandman;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.util.regex.Pattern;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
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
    public void snooze_snooze20ms_shouldSnoozeBetween20And30ms() {
        long start = System.nanoTime();
        Sandman.snoozeMillis(20);
        //Calc the time to do all iterations
        long executionTimeInMS = (System.nanoTime() - start) / 1000000;
        Assert.assertThat(executionTimeInMS, Matchers.lessThan(30l));
        Assert.assertThat(executionTimeInMS, Matchers.greaterThan(20l));
    }


    @Test
    public void testThrowError() {
        Misc misc = new Misc();
        exception.expect(RuntimeError.class);
        exception.expectMessage("Requested error thrown");
        misc.throwError();

    }

}
