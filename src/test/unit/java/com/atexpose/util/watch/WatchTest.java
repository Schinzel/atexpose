package com.atexpose.util.watch;

import org.junit.Test;

import java.time.Instant;
import java.time.ZoneId;

import static org.assertj.core.api.Assertions.assertThat;


public class WatchTest {


    @Test
    public void getInstant_BasicSetUp_TimeNow() {
        Instant start = Instant.now();
        assertThat(Watch.create().getNowAsInstant()).isBetween(start, Instant.now());
    }


    @Test
    public void getInstant_SetInstant_SameInstant() {
        Instant instant = Instant.parse("1973-11-17T23:55:35Z");
        TestWatch timepiece = TestWatch.create().setInstant(instant);
        assertThat(timepiece.getNowAsInstant()).isEqualTo(instant);
    }


    @Test
    public void getInstant_SetDateTimeUtc_SameTimeUtc() {
        TestWatch timepiece = TestWatch.create().setDateTimeUtc(1973, 11, 17, 23, 55);
        assertThat(timepiece.getNowAsInstant()).isEqualTo("1973-11-17T23:55:00Z");
    }


    @Test
    public void getInstant_SetDateTimeStockholm_2hourEarlierUtc() {
        TestWatch timepiece = TestWatch.create().setDateTime(2017, 7, 27, 23, 55, ZoneId.of("Europe/Stockholm"));
        assertThat(timepiece.getNowAsInstant()).isEqualTo("2017-07-27T21:55:00Z");
    }


    @Test
    public void toString_SetDate_TheSetDate() {
        TestWatch timepiece = TestWatch.create().setDateTime(2017, 7, 27, 23, 55, ZoneId.of("Europe/Stockholm"));
        assertThat(timepiece.toString()).isEqualTo("2017-07-27T21:55:00Z");
    }

}