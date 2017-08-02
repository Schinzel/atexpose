package com.atexpose.dispatcher.channels.tasks;

import com.atexpose.util.watch.IWatch;
import com.atexpose.util.watch.TestWatch;
import org.junit.Test;

import java.time.ZoneId;
import java.time.ZonedDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

/**
 * The extending ony for vanity coverage points.
 */
public class TaskUtilTest extends TaskUtil {


    @Test
    public void getZoneId_null_UTC() {
        assertThat(TaskUtil.getZoneId(null)).isEqualTo(ZoneId.of("UTC"));

    }


    @Test
    public void getZoneId_EmptyString_UTC() {
        assertThat(TaskUtil.getZoneId("")).isEqualTo(ZoneId.of("UTC"));

    }


    @Test
    public void getZoneId_AmericaNew_York_AmericaNew_York() {
        assertThat(TaskUtil.getZoneId("America/New_York"))
                .isEqualTo(ZoneId.of("America/New_York"));
    }


    @Test
    public void getZonedDateTime_MidDayNewYorkInSummerTime_4HoursLaterUtc() {
        //Exact time does not matter. Only that is summer time
        IWatch watch = TestWatch.create().setDateTimeUtc(2017, 7, 27, 15, 30);
        ZonedDateTime actual = TaskUtil.getZonedDateTime("14:30", IWatch.NEW_YORK, watch);
        assertThat(actual).isEqualTo("2017-07-27T18:30:00Z");
        assertThat(actual).isEqualTo("2017-07-27T14:30-04:00[America/New_York]");
    }


    @Test
    public void getZonedDateTime_EveningNewYorkInTheSummerTime_NextDayUtc() {
        //Exact time does not matter. Only that is summer time
        IWatch watch = TestWatch.create().setDateTimeUtc(2017, 7, 27, 15, 30);
        ZonedDateTime actual = TaskUtil.getZonedDateTime("22:30", IWatch.NEW_YORK, watch);
        assertThat(actual).isEqualTo("2017-07-28T02:30:00Z");
        assertThat(actual).isEqualTo("2017-07-27T22:30-04:00[America/New_York]");
    }


    @Test
    public void getZonedDateTime_EveningNewYorkInTheSummerTimeDayOfMonth14_DayOfMonth15Utc() {
        //Exact time does not matter. Only that is summer time
        IWatch watch = TestWatch.create().setDateTimeUtc(2017, 7, 27, 15, 30);
        ZonedDateTime actual = TaskUtil.getZonedDateTime("22:30", 14, IWatch.NEW_YORK, watch);
        assertThat(actual).isEqualTo("2017-07-15T02:30:00Z");
        assertThat(actual).isEqualTo("2017-07-14T22:30-04:00[America/New_York]");
    }


    @Test
    public void validateTimeOfDay_CorrectTime_CorrectTime() {
        String actual = TaskUtil.validateTimeOfDay("23:55");
        assertThat(actual).isEqualTo("23:55");
    }


    @Test
    public void validateTimeOfDay_IncorrectTime_Exception() {
        assertThatExceptionOfType(RuntimeException.class)
                .isThrownBy(() ->
                        TaskUtil.validateTimeOfDay("25:55")
                ).withMessageStartingWith("Incorrect task time: ");
    }


    @Test
    public void validateDayOfMonth_CorrectDayOfMonth_CorrectDayOfMonth() {
        int actual = TaskUtil.validateDayOfMonth(13);
        assertThat(actual).isEqualTo(13);
    }


    @Test
    public void validateDayOfMonth_ToLow_Exception() {
        assertThatExceptionOfType(RuntimeException.class)
                .isThrownBy(() ->
                        TaskUtil.validateDayOfMonth(0)
                ).withMessageStartingWith("The value 0 in variable 'dayOfMonth' is too small.");
    }


    @Test
    public void validateDayOfMonth_ToHigh_Exception() {
        assertThatExceptionOfType(RuntimeException.class)
                .isThrownBy(() ->
                        TaskUtil.validateDayOfMonth(29)
                ).withMessageStartingWith("The value 29 in variable 'dayOfMonth' is too large.");

    }

}