package com.atexpose.dispatcher.channels.tasks;

import org.json.JSONObject;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

public class DailyTaskChannelTest {


    @Test
    public void constructor_InvalidTimeFormat_Exception() {
        assertThatExceptionOfType(RuntimeException.class)
                .isThrownBy(() ->
                        new DailyTaskChannel("theTaskName", "request", "12345678", "UTC"))
                .withMessageStartingWith("Incorrect task time: ");
    }


    @Test
    public void getState_TaskTimeContainsSetTimeOfDay() {
        DailyTaskChannel stc = new DailyTaskChannel("TaskName", "TheRequest", "23:57", "UTC");
        JSONObject status = stc.getState().getJson();
        assertThat(status.getString("task_time")).isEqualTo("Every day at 23:57[UTC]");
    }


    @Test
    public void getState_NewYork_TaskTimeContainsSetTimeOfDay() {
        DailyTaskChannel stc = new DailyTaskChannel("TaskName", "TheRequest", "23:57", "America/New_York");
        JSONObject status = stc.getState().getJson();
        assertThat(status.getString("task_time")).isEqualTo("Every day at 23:57[America/New_York]");
    }


    @Test
    public void getTimeToFireNext_Now1HourBeforeTaskTime_SameDayAtTaskTime() {
        IWatch watch = Watch.create().setDateTimeUTC(2017, 7, 27, 13, 30);
        DailyTaskChannel dailyTaskChannel = new DailyTaskChannel("TaskName", "MyRequest", "14:30", "UTC", watch);
        assertThat(dailyTaskChannel.getTimeToFireNext().toInstant()).isEqualTo("2017-07-27T14:30:00Z");
    }


    @Test
    public void getTimeToFireNext_Now1HourBeforeTaskTimeUTC_SameDayAtTaskTime() {
        IWatch watch = Watch.create().setDateTime(2017, 7, 27, 13, 30, IWatch.UTC);
        DailyTaskChannel dtc = new DailyTaskChannel("TaskName", "MyRequest", "14:30", "UTC", watch);
        assertThat(dtc.getTimeToFireNext().toInstant()).isEqualTo("2017-07-27T14:30:00Z");
    }


    @Test
    public void getTimeToFireNext_Now1HourBeforeTaskTimeStockholm_SameDayTwoHoursEarlierUtc() {
        IWatch watch = Watch.create().setDateTime(2017, 7, 27, 13, 30, IWatch.STOCKHOLM);
        DailyTaskChannel dtc = new DailyTaskChannel("TaskName", "MyRequest", "14:30", "Europe/Stockholm", watch);
        assertThat(dtc.getTimeToFireNext().toInstant()).isEqualTo("2017-07-27T12:30:00Z");
    }


    @Test
    public void getTimeToFireNext_ConstructorTaskTimeNewYork_SameDayFourHoursLaterUtc() {
        IWatch watch = Watch.create().setDateTime(2017, 7, 27, 13, 30, IWatch.NEW_YORK);
        DailyTaskChannel dtc = new DailyTaskChannel("TaskName", "MyRequest", "14:30", "America/New_York", watch);
        assertThat(dtc.getTimeToFireNext().toInstant()).isEqualTo("2017-07-27T18:30:00Z");
    }


    @Test
    public void getTimeToFireNext_Now1HourAfterTaskTime_NextDayAtTaskTime() {
        IWatch watch = Watch.create().setDateTimeUTC(2017, 7, 27, 15, 30);
        DailyTaskChannel dailyTaskChannel = new DailyTaskChannel("TaskName", "MyRequest", "14:30", "UTC", watch);
        assertThat(dailyTaskChannel.getTimeToFireNext().toInstant()).isEqualTo("2017-07-28T14:30:00Z");
    }


    @Test
    public void getTimeToFireNext_Now1HourAfterTaskTimeUTC_NextDayAtTaskTime() {
        IWatch watch = Watch.create().setDateTime(2017, 7, 27, 15, 30, IWatch.UTC);
        DailyTaskChannel dtc = new DailyTaskChannel("TaskName", "MyRequest", "14:30", "UTC", watch);
        assertThat(dtc.getTimeToFireNext().toInstant()).isEqualTo("2017-07-28T14:30:00Z");
    }


    @Test
    public void getTimeToFireNext_Now1HourAfterTaskTimeStockholm_NextDayTwoHoursEarlierUtc() {
        IWatch watch = Watch.create().setDateTime(2017, 7, 27, 15, 30, IWatch.STOCKHOLM);
        DailyTaskChannel dtc = new DailyTaskChannel("TaskName", "MyRequest", "14:30", "Europe/Stockholm", watch);
        assertThat(dtc.getTimeToFireNext().toInstant()).isEqualTo("2017-07-28T12:30:00Z");
    }


    @Test
    public void getTimeToFireNext_ConstructorTaskTimeNewYork_NextDayFourHoursLaterUtc() {
        IWatch watch = Watch.create().setDateTime(2017, 7, 27, 15, 30, IWatch.NEW_YORK);
        DailyTaskChannel dtc = new DailyTaskChannel("TaskName", "MyRequest", "14:30", "America/New_York", watch);
        assertThat(dtc.getTimeToFireNext().toInstant()).isEqualTo("2017-07-28T18:30:00Z");
    }

}