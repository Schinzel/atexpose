package com.atexpose.dispatcher.channels.tasks;

import com.atexpose.util.watch.IWatch;
import com.atexpose.util.watch.TestWatch;
import lombok.val;
import org.junit.Test;
import java.time.ZonedDateTime;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatExceptionOfType;

public class ScheduledTaskChannelHourlyTest {

    @Test
    public void constructor() {
        val task = new ScheduledTaskChannelHourly("Any task", "Request", 55, "UTC");
        String humanReadableTaskInterval = task.getHumanReadableTaskInterval();
        assertThat(humanReadableTaskInterval).isEqualTo("Every hour at 55 minutes");
    }

    @Test
    public void validateMinuteOfHour_minusOne_exception() {
        assertThatExceptionOfType(RuntimeException.class).isThrownBy(() ->
                ScheduledTaskChannelHourly.validateMinuteOfHour(-1)
        );
    }


    @Test
    public void validateMinuteOfHour_zero_zero() {
        int actual = ScheduledTaskChannelHourly.validateMinuteOfHour(0);
        assertThat(actual).isZero();
    }


    @Test
    public void validateMinuteOfHour_59_59() {
        int actual = ScheduledTaskChannelHourly.validateMinuteOfHour(59);
        assertThat(actual).isEqualTo(59);
    }

    @Test
    public void validateMinuteOfHour_60_exception() {
        assertThatExceptionOfType(RuntimeException.class).isThrownBy(() ->
                ScheduledTaskChannelHourly.validateMinuteOfHour(60)
        );
    }

    @Test
    public void getInitialFireTime_startAt10minNowIs15Past_Start55Minutes() {
        IWatch watchNow = TestWatch.create().setDateTimeUtc(2021, 3, 3, 11, 15);
        final ZonedDateTime fireTime = ScheduledTaskChannelHourly
                .getInitialFireTime(10, TaskUtil.getZoneId("UTC"), watchNow);
        assertThat(fireTime).isEqualTo("2021-03-03T12:10:00Z");
    }

    @Test
    public void getInitialFireTime_startAt10minNowIs5Past_StartInFiveMinutes() {
        IWatch watchNow = TestWatch.create().setDateTimeUtc(2021, 3, 3, 11, 5);
        final ZonedDateTime fireTime = ScheduledTaskChannelHourly
                .getInitialFireTime(10, TaskUtil.getZoneId("UTC"), watchNow);
        assertThat(fireTime).isEqualTo("2021-03-03T11:10:00Z");
    }



    @Test
    public void getInitialFireTime_NowIs35SecondsBeforeMinOfHour_FireTimeIn35Sec() {
        IWatch watchNow = TestWatch.create().setDateTime(2021, 12, 30, 22, 54, 25, 0, IWatch.UTC);
        final ZonedDateTime fireTime = ScheduledTaskChannelHourly
                .getInitialFireTime(55, TaskUtil.getZoneId("UTC"), watchNow);
        assertThat(fireTime).isEqualTo("2021-12-30T22:55:00Z");
    }

    @Test
    public void getInitialFireTime_NowIs25SecBeforeMinOfHour_FireTimeIn25SecAndOneHour() {
        IWatch watchNow = TestWatch.create().setDateTime(2021, 12, 30, 22, 54, 35, 0, IWatch.UTC);
        final ZonedDateTime fireTime = ScheduledTaskChannelHourly
                .getInitialFireTime(55, TaskUtil.getZoneId("UTC"), watchNow);
        assertThat(fireTime).isEqualTo("2021-12-30T23:55:00Z");
    }


    @Test
    public void getInitialFireTime_NowIs10SecondsAfterMinOfHour_FireTimeInOneHourMinus10Sec() {
        IWatch watchNow = TestWatch.create().setDateTime(2021, 12, 30, 22, 55, 10, 0, IWatch.UTC);
        final ZonedDateTime fireTime = ScheduledTaskChannelHourly
                .getInitialFireTime(55, TaskUtil.getZoneId("UTC"), watchNow);
        assertThat(fireTime).isEqualTo("2021-12-30T23:55:00Z");
    }

}