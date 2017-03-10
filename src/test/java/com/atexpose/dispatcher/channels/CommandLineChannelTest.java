package com.atexpose.dispatcher.channels;

import com.atexpose.errors.RuntimeError;
import org.json.JSONObject;
import org.junit.After;
import org.junit.Test;

import static org.junit.Assert.*;

import org.junit.Rule;
import org.junit.rules.ExpectedException;

/**
 * @author Schinzel
 */
public class CommandLineChannelTest {
    CommandLineChannel cli;
    @Rule
    public ExpectedException exception = ExpectedException.none();


    @After
    public void after() {
        cli.shutdown(Thread.currentThread());

    }


    @Test
    public void testClone() {
        cli = new CommandLineChannel();
        exception.expect(RuntimeError.class);
        exception.expectMessage("The command line interface cannot be multi-threaded.");
        cli.getClone();
    }


    @Test
    public void testStatsAndLog() {
        cli = new CommandLineChannel();
        assertEquals("CLI", cli.senderInfo());
        assertEquals(-1, cli.requestReadTime());
        JSONObject jo = cli.getState().getJson();
        assertEquals("[@Expose]>", jo.getString("Tag"));
    }

}
