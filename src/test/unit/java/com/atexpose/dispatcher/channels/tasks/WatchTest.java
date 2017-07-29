package com.atexpose.dispatcher.channels.tasks;

import org.junit.Test;

import java.time.*;

import static org.assertj.core.api.Assertions.assertThat;


public class WatchTest {


    @Test
    public void getInstant_BasicSetUp_TimeNow() {
        Instant start = Instant.now();
        assertThat(Watch.create().getInstant()).isBetween(start, Instant.now());
    }


    @Test
    public void getInstant_SetInstant_SameInstant() {
        Instant instant = Instant.parse("1973-11-17T23:55:35Z");
        Watch timepiece = Watch.create().setInstant(instant);
        assertThat(timepiece.getInstant()).isEqualTo(instant);
    }


    @Test
    public void getInstant_SetDateTimeUtc_SameTimeUtc() {
        Watch timepiece = Watch.create().setDateTimeUTC(1973, 11, 17, 23, 55);
        assertThat(timepiece.getInstant()).isEqualTo("1973-11-17T23:55:00Z");
    }


    @Test
    public void getInstant_SetDateTimeStockholm_2hourEarlierUtc() {
        Watch timepiece = Watch.create().setDateTime(2017, 7, 27, 23, 55, ZoneId.of("Europe/Stockholm"));
        assertThat(timepiece.getInstant()).isEqualTo("2017-07-27T21:55:00Z");
    }

}