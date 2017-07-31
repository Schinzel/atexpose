package com.atexpose.dispatcher.channels.tasks;

import org.json.JSONObject;
import org.junit.Test;

import java.time.ZonedDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

public class DailyTaskChannelTest {


    @Test
    public void constructor_InvalidTimeFormat_Exception() {
        assertThatExceptionOfType(RuntimeException.class)
                .isThrownBy(() ->
                        new DailyTaskChannel("theTaskName", "request", "12345678", IWatch.UTC))
                .withMessageStartingWith("Incorrect task time: ");
    }


    @Test
    public void getState_TaskTimeContainsSetTimeOfDay() {
        DailyTaskChannel stc = new DailyTaskChannel("TaskName", "TheRequest", "23:57", IWatch.UTC);
        JSONObject status = stc.getState().getJson();
        assertThat(status.getString("task_time")).isEqualTo("Every day at 23:57[UTC]");
    }


    @Test
    public void getState_NewYork_TaskTimeContainsSetTimeOfDay() {
        DailyTaskChannel stc = new DailyTaskChannel("TaskName", "TheRequest", "23:57", IWatch.NEW_YORK);
        JSONObject status = stc.getState().getJson();
        assertThat(status.getString("task_time")).isEqualTo("Every day at 23:57[America/New_York]");
    }


    @Test
    public void getTimeToFireNext_Now1HourBeforeTaskTime_SameDayAtTaskTime() {
        IWatch watch = Watch.create().setDateTimeUTC(2017, 7, 27, 13, 30);
        DailyTaskChannel dailyTaskChannel = new DailyTaskChannel("TaskName", "MyRequest", "14:30", watch.UTC, watch);
        assertThat(dailyTaskChannel.getTimeToFireNext().toInstant()).isEqualTo("2017-07-27T14:30:00Z");
    }


    @Test
    public void getTimeToFireNext_Now1HourBeforeTaskTimeUTC_SameDayAtTaskTime() {
        IWatch watch = Watch.create().setDateTime(2017, 7, 27, 13, 30, IWatch.UTC);
        DailyTaskChannel dtc = new DailyTaskChannel("TaskName", "MyRequest", "14:30", watch.UTC, watch);
        assertThat(dtc.getTimeToFireNext().toInstant()).isEqualTo("2017-07-27T14:30:00Z");
    }


    @Test
    public void getTimeToFireNext_Now1HourBeforeTaskTimeStockholm_SameDayTwoHoursEarlierUtc() {
        IWatch watch = Watch.create().setDateTime(2017, 7, 27, 13, 30, IWatch.STOCKHOLM);
        DailyTaskChannel dtc = new DailyTaskChannel("TaskName", "MyRequest", "14:30", IWatch.STOCKHOLM, watch);
        assertThat(dtc.getTimeToFireNext().toInstant()).isEqualTo("2017-07-27T12:30:00Z");
    }


    @Test
    public void getTimeToFireNext_ConstructorTaskTimeNewYork_SametDayFourHoursLaterUtc() {
        IWatch watch = Watch.create().setDateTime(2017, 7, 27, 13, 30, IWatch.NEW_YORK);
        DailyTaskChannel dtc = new DailyTaskChannel("TaskName", "MyRequest", "14:30", IWatch.NEW_YORK, watch);
        assertThat(dtc.getTimeToFireNext().toInstant()).isEqualTo("2017-07-27T18:30:00Z");
    }


    @Test
    public void getTimeToFireNext_Now1HourAfterTaskTime_NextDayAtTaskTime() {
        IWatch watch = Watch.create().setDateTimeUTC(2017, 7, 27, 15, 30);
        DailyTaskChannel dailyTaskChannel = new DailyTaskChannel("TaskName", "MyRequest", "14:30", watch.UTC, watch);
        assertThat(dailyTaskChannel.getTimeToFireNext().toInstant()).isEqualTo("2017-07-28T14:30:00Z");
    }


    @Test
    public void getTimeToFireNext_Now1HourAfterTaskTimeUTC_NextDayAtTaskTime() {
        IWatch watch = Watch.create().setDateTime(2017, 7, 27, 15, 30, IWatch.UTC);
        DailyTaskChannel dtc = new DailyTaskChannel("TaskName", "MyRequest", "14:30", watch.UTC, watch);
        assertThat(dtc.getTimeToFireNext().toInstant()).isEqualTo("2017-07-28T14:30:00Z");
    }


    @Test
    public void getTimeToFireNext_Now1HourAfterTaskTimeStockholm_NextDayTwoHoursEarlierUtc() {
        IWatch watch = Watch.create().setDateTime(2017, 7, 27, 15, 30, IWatch.STOCKHOLM);
        DailyTaskChannel dtc = new DailyTaskChannel("TaskName", "MyRequest", "14:30", IWatch.STOCKHOLM, watch);
        assertThat(dtc.getTimeToFireNext().toInstant()).isEqualTo("2017-07-28T12:30:00Z");
    }


    @Test
    public void getTimeToFireNext_ConstructorTaskTimeNewYork_NextDayFourHoursLaterUtc() {
        IWatch watch = Watch.create().setDateTime(2017, 7, 27, 15, 30, IWatch.NEW_YORK);
        DailyTaskChannel dtc = new DailyTaskChannel("TaskName", "MyRequest", "14:30", IWatch.NEW_YORK, watch);
        assertThat(dtc.getTimeToFireNext().toInstant()).isEqualTo("2017-07-28T18:30:00Z");
    }


    @Test
    public void getZonedDateTime_MidDayNewYork_4HoursLaterUtc() {
        //Exact time does not matter. Only that is summer time
        IWatch watch = Watch.create().setDateTimeUTC(2017, 7, 27, 15, 30);
        ZonedDateTime actual = DailyTaskChannel.getZonedDateTime("14:30", IWatch.NEW_YORK, watch);
        assertThat(actual).isEqualTo("2017-07-27T18:30:00Z");
        assertThat(actual).isEqualTo("2017-07-27T14:30-04:00[America/New_York]");
    }


    @Test
    public void getZonedDateTime_EveningNewYork_NextDayUtc() {
        //Exact time does not matter. Only that is summer time
        IWatch watch = Watch.create().setDateTimeUTC(2017, 7, 27, 15, 30);
        ZonedDateTime actual = DailyTaskChannel.getZonedDateTime("22:30", IWatch.NEW_YORK, watch);
        assertThat(actual).isEqualTo("2017-07-28T02:30:00Z");
        assertThat(actual).isEqualTo("2017-07-27T22:30-04:00[America/New_York]");
    }
}