package com.atexpose.util.watch;

import org.junit.Test;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

public class IWatchTest {

    @Test
    public void getLocalDate_SummerTimeJustBeforeMidnightUtc_SameDate() {
        IWatch watch = TestWatch.create().setDateTimeUtc(2017, 7, 20, 23, 55);
        LocalDate localDate = watch.getInstant()
                .atZone(IWatch.UTC)
                .toLocalDate();
        assertThat(localDate).isEqualTo("2017-07-20");
    }

    @Test
    public void getLocalDate_SummerTimeJustBeforeMidnightStockholm_SameDate() {
        IWatch watch = TestWatch.create().setDateTimeUtc(2017, 7, 20, 23, 55);
        LocalDate localDate = watch.getInstant()
                .atZone(IWatch.STOCKHOLM)
                .toLocalDate();
        assertThat(localDate).isEqualTo("2017-07-21");
    }
}