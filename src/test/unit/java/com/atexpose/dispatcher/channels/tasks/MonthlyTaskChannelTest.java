package com.atexpose.dispatcher.channels.tasks;

import org.json.JSONObject;
import org.junit.Test;

import static org.assertj.core.api.Assertions.*;


public class MonthlyTaskChannelTest {

    @Test
    public void constructor_DayOfMonthTooLow_Exception() {
        assertThatExceptionOfType(RuntimeException.class)
                .isThrownBy(() ->
                        new MonthlyTaskChannel("theTaskName", "request", "23:55", 0))
                .withMessageStartingWith("The value 0 in variable 'dayOfMonth' is too small.");
    }


    @Test
    public void constructor_DayOfMonthTooHigh_Exception() {
        assertThatExceptionOfType(RuntimeException.class)
                .isThrownBy(() -> new MonthlyTaskChannel("theTaskName", "request", "23:55", 29))
                .withMessageStartingWith("The value 29 in variable 'dayOfMonth' is too large");
    }


    @Test
    public void getState_MonthlyTask_TaskTimeContainsTimeOfDayAndDayOfMonth() {
        MonthlyTaskChannel stc = new MonthlyTaskChannel("TaskName3", "TheRequest3", "23:53", 28);
        JSONObject status = stc.getState().getJson();
        assertThat(status.getString("task_time")).isEqualTo("Once a month at 23:53 on day of month 28");
    }


    @Test
    public void getTimeToFireNext_Now1HourBeforeTaskTime_SameDayAtTaskTime() {
        IWatch watch = Watch.create().setDateTimeUTC(2017, 7, 27, 13, 30);
        MonthlyTaskChannel dailyTaskChannel = new MonthlyTaskChannel("TaskName", "MyRequest", "14:30", 27, watch);
        assertThat(dailyTaskChannel.mTimeToFireNext.toInstant()).isEqualTo("2017-07-27T14:30:00Z");
    }


    @Test
    public void getTimeToFireNext_Now1HourAfterTaskTime_NextMonth() {
        IWatch watch = Watch.create().setDateTimeUTC(2017, 7, 27, 15, 30);
        MonthlyTaskChannel dailyTaskChannel = new MonthlyTaskChannel("TaskName", "MyRequest", "14:30", 27, watch);
        assertThat(dailyTaskChannel.mTimeToFireNext.toInstant()).isEqualTo("2017-08-27T14:30:00Z");
    }

    @Test
    public void getTimeToFireNext__NowEndFebruaryLeapYear_TaskTimeDay15__15March() {
        IWatch watch = Watch.create().setDateTimeUTC(2016, 2, 27, 14, 30);
        MonthlyTaskChannel dailyTaskChannel = new MonthlyTaskChannel("TaskName", "MyRequest", "14:30", 15, watch);
        assertThat(dailyTaskChannel.mTimeToFireNext.toInstant()).isEqualTo("2016-03-15T14:30:00Z");
    }
}