package com.atexpose.dispatcher.channels.tasks;

import com.atexpose.util.ByteStorage;
import org.json.JSONObject;
import org.junit.Test;

import java.time.ZoneOffset;
import java.time.ZonedDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

public class ScheduledTaskChannelMinuteTest {


    @Test
    public void constructor_IntervalTooLow_Exception() {
        assertThatExceptionOfType(RuntimeException.class).isThrownBy(() ->
                new ScheduledTaskChannelMinute("theTŒaskName", "request", 0)
        );
    }


    @Test
    public void constructor_IntervalTooHigh_Exception() {
        assertThatExceptionOfType(RuntimeException.class).isThrownBy(() ->
                new ScheduledTaskChannelMinute("theTaskName", "request", 1441)
        );
    }


    @Test
    public void getRequest_IntervalSetTo15Min_TimeToFireNextIs15Min() {
        ScheduledTaskChannelMinute stc = new ScheduledTaskChannelMinute("The task 1", "TheRequest", 15);
        new Thread(() -> stc.getRequest(new ByteStorage())).start();
        ZonedDateTime fifteenMinFromNow = ZonedDateTime.now(ZoneOffset.UTC).plusMinutes(15);
        assertThat(stc.getTimeToFireNext()).isBetween(fifteenMinFromNow.minusSeconds(1), fifteenMinFromNow.plusSeconds(1));
    }


    @Test
    public void getState_MinuteIntervalTask_TaskTimeContainsMinuteInterval() {
        ScheduledTaskChannelMinute stc = new ScheduledTaskChannelMinute("TaskName", "TheRequest", 55);
        JSONObject status = stc.getState().getJson();
        assertThat(status.getString("task_time")).contains("Every 55 minutes");
    }

}