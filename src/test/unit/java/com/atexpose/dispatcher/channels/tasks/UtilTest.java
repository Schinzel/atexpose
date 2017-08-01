package com.atexpose.dispatcher.channels.tasks;

import org.junit.Test;

import java.time.ZonedDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

/**
 * The extending ony for vanity coverage points.
 */
public class UtilTest extends TasksUtil {


    @Test
    public void getZonedDateTime_MidDayNewYorkInSummerTime_4HoursLaterUtc() {
        //Exact time does not matter. Only that is summer time
        IWatch watch = Watch.create().setDateTimeUTC(2017, 7, 27, 15, 30);
        ZonedDateTime actual = TasksUtil.getZonedDateTime("14:30", IWatch.NEW_YORK, watch);
        assertThat(actual).isEqualTo("2017-07-27T18:30:00Z");
        assertThat(actual).isEqualTo("2017-07-27T14:30-04:00[America/New_York]");
    }


    @Test
    public void getZonedDateTime_EveningNewYorkInTheSummerTime_NextDayUtc() {
        //Exact time does not matter. Only that is summer time
        IWatch watch = Watch.create().setDateTimeUTC(2017, 7, 27, 15, 30);
        ZonedDateTime actual = TasksUtil.getZonedDateTime("22:30", IWatch.NEW_YORK, watch);
        assertThat(actual).isEqualTo("2017-07-28T02:30:00Z");
        assertThat(actual).isEqualTo("2017-07-27T22:30-04:00[America/New_York]");
    }


    @Test
    public void getZonedDateTime_EveningNewYorkInTheSummerTimeDayOfMonth14_DayOfMonth15Utc() {
        //Exact time does not matter. Only that is summer time
        IWatch watch = Watch.create().setDateTimeUTC(2017, 7, 27, 15, 30);
        ZonedDateTime actual = TasksUtil.getZonedDateTime("22:30", 14, IWatch.NEW_YORK, watch);
        assertThat(actual).isEqualTo("2017-07-15T02:30:00Z");
        assertThat(actual).isEqualTo("2017-07-14T22:30-04:00[America/New_York]");
    }


    @Test
    public void validateTimeOfDay_CorrectTime_CorrectTime() {
        String actual = TasksUtil.validateTimeOfDay("23:55");
        assertThat(actual).isEqualTo("23:55");
    }


    @Test
    public void validateTimeOfDay_IncorrectTime_Exception() {
        assertThatExceptionOfType(RuntimeException.class)
                .isThrownBy(() ->
                        TasksUtil.validateTimeOfDay("25:55")
                ).withMessageStartingWith("Incorrect task time: ");
    }


    @Test
    public void validateDayOfMonth_CorrectDayOfMonth_CorrectDayOfMonth() {
        int actual = TasksUtil.validateDayOfMonth(13);
        assertThat(actual).isEqualTo(13);
    }


    @Test
    public void validateDayOfMonth_ToLow_Exception() {
        assertThatExceptionOfType(RuntimeException.class)
                .isThrownBy(() ->
                        TasksUtil.validateDayOfMonth(0)
                ).withMessageStartingWith("The value 0 in variable 'dayOfMonth' is too small.");
    }


    @Test
    public void validateDayOfMonth_ToHigh_Exception() {
        assertThatExceptionOfType(RuntimeException.class)
                .isThrownBy(() ->
                        TasksUtil.validateDayOfMonth(29)
                ).withMessageStartingWith("The value 29 in variable 'dayOfMonth' is too large.");

    }

}