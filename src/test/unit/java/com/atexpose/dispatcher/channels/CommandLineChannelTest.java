package com.atexpose.dispatcher.channels;

import org.json.JSONObject;
import org.junit.After;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.junit.Assert.assertEquals;


public class CommandLineChannelTest {
    private CommandLineChannel cli;


    @After
    public void after() {
        cli.shutdown(Thread.currentThread());

    }


    @Test
    public void testClone() {
        cli = new CommandLineChannel();
        assertThatExceptionOfType(RuntimeException.class)
                .isThrownBy(() -> cli.getClone());
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
