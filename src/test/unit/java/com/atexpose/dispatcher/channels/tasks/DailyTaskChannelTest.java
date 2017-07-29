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
                        new DailyTaskChannel("theTaskName", "request", "12345678"))
                .withMessageStartingWith("Incorrect task time: ");
    }


    @Test
    public void getState_DailyTask_TaskTimeContainsSetTimeOfDay() {
        DailyTaskChannel stc = new DailyTaskChannel("TaskName", "TheRequest", "23:57");
        JSONObject status = stc.getState().getJson();
        assertThat(status.getString("task_time")).contains("23:57");
    }


}