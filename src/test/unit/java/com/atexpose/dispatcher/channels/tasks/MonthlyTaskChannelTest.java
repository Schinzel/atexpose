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
        assertThat(status.getString("task_time")).contains("23:53").contains("28");
    }

}