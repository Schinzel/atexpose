package com.atexpose.dispatcher.channels.tasks;

import com.atexpose.util.watch.IWatch;
import com.atexpose.util.watch.TestWatch;
import org.json.JSONObject;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;


public class ScheduledTaskChannelMonthlyTest {

    @Test
    public void constructor_DayOfMonthTooLow_Exception() {
        assertThatExceptionOfType(RuntimeException.class)
                .isThrownBy(() ->
                        new ScheduledTaskChannelMonthly("theTaskName", "request", "23:55", 0, "UTC"))
                .withMessageStartingWith("The value 0 in variable 'dayOfMonth' is too small.");
    }


    @Test
    public void constructor_DayOfMonthTooHigh_Exception() {
        assertThatExceptionOfType(RuntimeException.class)
                .isThrownBy(() -> new ScheduledTaskChannelMonthly("theTaskName", "request", "23:55", 29, "UTC"))
                .withMessageStartingWith("The value 29 in variable 'dayOfMonth' is too large");
    }


    @Test
    public void getState_TaskTimeContainsTimeOfDayAndDayOfMonth() {
        ScheduledTaskChannelMonthly stc = new ScheduledTaskChannelMonthly("TaskName3", "TheRequest3", "23:53", 28, "UTC");
        JSONObject status = stc.getState().getJson();
        assertThat(status.getString("task_time")).isEqualTo("Once a month at 23:53 on day of month 28 in time zone UTC");
    }


    @Test
    public void getTimeToFireNext_Now1HourBeforeTaskTime_SameDayAtTaskTime() {
        IWatch watch = TestWatch.create().setDateTimeUtc(2017, 7, 27, 13, 30);
        ScheduledTaskChannelMonthly dailyTaskChannel = new ScheduledTaskChannelMonthly("TaskName", "MyRequest", "14:30", 27, "UTC", watch);
        assertThat(dailyTaskChannel.getTimeToFireNext().toInstant()).isEqualTo("2017-07-27T14:30:00Z");
    }


    @Test
    public void getTimeToFireNext_Now1HourAfterTaskTime_NextMonth() {
        IWatch watch = TestWatch.create().setDateTimeUtc(2017, 7, 27, 15, 30);
        ScheduledTaskChannelMonthly dailyTaskChannel = new ScheduledTaskChannelMonthly("TaskName", "MyRequest", "14:30", 27, "UTC", watch);
        assertThat(dailyTaskChannel.getTimeToFireNext().toInstant()).isEqualTo("2017-08-27T14:30:00Z");
    }


    @Test
    public void getTimeToFireNext__NowEndFebruaryLeapYear_TaskTimeDay15__15March() {
        IWatch watch = TestWatch.create().setDateTimeUtc(2016, 2, 27, 14, 30);
        ScheduledTaskChannelMonthly dailyTaskChannel = new ScheduledTaskChannelMonthly("TaskName", "MyRequest", "14:30", 15, "UTC", watch);
        assertThat(dailyTaskChannel.getTimeToFireNext().toInstant()).isEqualTo("2016-03-15T14:30:00Z");
    }


}