package com.atexpose.dispatcher.channels.tasks;

import org.json.JSONObject;
import org.junit.Test;

import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

public class DailyTaskChannelTest {


    @Test
    public void constructor_InvalidTimeFormat_Exception() {
        assertThatExceptionOfType(RuntimeException.class)
                .isThrownBy(() ->
                        new DailyTaskChannel("theTaskName", "request", "12345678"))
                .withMessageStartingWith("Incorrect task time: ");
    }


    @Test
    public void getState_DailyTask_TaskTimeContainsSetTimeOfDay() {
        DailyTaskChannel stc = new DailyTaskChannel("TaskName", "TheRequest", "23:57");
        JSONObject status = stc.getState().getJson();
        assertThat(status.getString("task_time")).contains("23:57");
    }


    @Test
    public void constructor_SetCustomWatch_CustomWatch() {
        Instant myInstant = Instant.now();
        DailyTaskChannel dailyTaskChannel = new DailyTaskChannel("TaskName", "MyRequest", "14:30", Watch.create().setInstant(myInstant));
        assertThat(dailyTaskChannel.mWatch.getInstant()).isEqualTo(myInstant);
    }


    @Test
    public void constructor_DefaultWatch_WatchReturnsNow() {
        Instant start = Instant.now();
        DailyTaskChannel dailyTaskChannel = new DailyTaskChannel("TaskName", "MyRequest", "14:30");
        assertThat(dailyTaskChannel.mWatch.getInstant()).isBetween(start, Instant.now());
    }


    @Test
    public void getTimeToFireNext_Now1HourBeforeTaskTime_SameDayAtTaskTime() {
        IWatch watch = Watch.create().setDateTimeUTC(2017, 7, 27, 13, 30);
        DailyTaskChannel dailyTaskChannel = new DailyTaskChannel("TaskName", "MyRequest", "14:30", watch);
        assertThat(dailyTaskChannel.mTimeToFireNext.toInstant()).isEqualTo("2017-07-27T14:30:00Z");
    }


    @Test
    public void getTimeToFireNext_Now1HourAfterTaskTime_NextDayAtTaskTime() {
        IWatch watch = Watch.create().setDateTimeUTC(2017, 7, 27, 15, 30);
        DailyTaskChannel dailyTaskChannel = new DailyTaskChannel("TaskName", "MyRequest", "14:30", watch);
        assertThat(dailyTaskChannel.mTimeToFireNext.toInstant()).isEqualTo("2017-07-28T14:30:00Z");
    }
}