package com.atexpose.dispatcherfactories;

import com.atexpose.dispatcher.Dispatcher;
import com.atexpose.dispatcher.channels.tasks.ScheduledTaskChannel;
import com.atexpose.dispatcher.channels.tasks.ScheduledTaskChannelDaily;
import com.atexpose.dispatcher.channels.tasks.ScheduledTaskChannelMinute;
import com.atexpose.dispatcher.channels.tasks.ScheduledTaskChannelMonthly;
import com.atexpose.dispatcher.parser.TextParser;
import com.atexpose.dispatcher.wrapper.CsvWrapper;
import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class ScheduledTaskFactoryTest extends ScheduledTaskFactory {

    @Test
    public void getChannel_NewMinuteTask_ScheduledTaskChannelMinute() {
        Dispatcher dispatcher = (Dispatcher) ScheduledTaskFactory.minuteTaskBuilder()
                .taskName("MyTaskName")
                .request("ping")
                .minutes(1)
                .build();
        assertThat(dispatcher.getChannel())
                .isInstanceOf(ScheduledTaskChannelMinute.class);
    }


    @Test
    public void getChannel_NewDailyTask_ScheduledTaskChannelDaily() {
        Dispatcher dispatcher = (Dispatcher) ScheduledTaskFactory.dailyTaskBuilder()
                .taskName("MyTaskName")
                .request("ping")
                .timeOfDay("14:15")
                .zoneId("UTC")
                .build();
        assertThat(dispatcher.getChannel())
                .isInstanceOf(ScheduledTaskChannelDaily.class);
    }


    @Test
    public void getChannel_NewMonthlyTask_ScheduledTaskChannelMonthly() {
        Dispatcher dispatcher = (Dispatcher) ScheduledTaskFactory.monthlyTaskBuilder()
                .taskName("MyTaskName")
                .request("ping")
                .timeOfDay("14:15")
                .dayOfMonth(5)
                .zoneId("UTC")
                .build();
        assertThat(dispatcher.getChannel())
                .isInstanceOf(ScheduledTaskChannelMonthly.class);
    }


    @Test
    public void getChannel_DefaultSetUp_ArgumentObject() {
        ScheduledTaskChannel scheduledTask =
                new ScheduledTaskChannelMinute("MyTaskName", "ping", 1);
        Dispatcher dispatcher = (Dispatcher) ScheduledTaskFactory.addTask(scheduledTask);
        assertThat(dispatcher.getChannel())
                .isEqualTo(scheduledTask);
    }


    @Test
    public void getParser_DefaultSetUp_TextParser() {
        ScheduledTaskChannel scheduledTask =
                new ScheduledTaskChannelMinute("MyTaskName", "ping", 1);
        Dispatcher dispatcher = (Dispatcher) ScheduledTaskFactory.addTask(scheduledTask);
        assertThat(dispatcher.getParser())
                .isInstanceOf(TextParser.class);
    }


    @Test
    public void getWrapper_DefaultSetUp_CsvWrapper() {
        ScheduledTaskChannel scheduledTask =
                new ScheduledTaskChannelMinute("MyTaskName", "ping", 1);
        Dispatcher dispatcher = (Dispatcher) ScheduledTaskFactory.addTask(scheduledTask);
        assertThat(dispatcher.getWrapper())
                .isInstanceOf(CsvWrapper.class);
    }


    @Test
    public void accessLevel_DefaultSetUp_3() {
        ScheduledTaskChannel scheduledTask =
                new ScheduledTaskChannelMinute("MyTaskName", "ping", 1);
        Dispatcher dispatcher = (Dispatcher) ScheduledTaskFactory.addTask(scheduledTask);
        assertThat(dispatcher.getAccessLevel())
                .isEqualTo(3);
    }


    @Test
    public void getKey_DefaultSetUp_ScheduledTask_MyTaskName() {
        ScheduledTaskChannel scheduledTask =
                new ScheduledTaskChannelMinute("MyTaskName", "ping", 1);
        Dispatcher dispatcher = (Dispatcher) ScheduledTaskFactory.addTask(scheduledTask);
        assertThat(dispatcher.getKey())
                .isEqualTo("ScheduledTask_MyTaskName");
    }


    @Test
    public void threadCount_DefaultSetUp_1() {
        ScheduledTaskChannel scheduledTask =
                new ScheduledTaskChannelMinute("MyTaskName", "ping", 1);
        Dispatcher dispatcher = (Dispatcher) ScheduledTaskFactory.addTask(scheduledTask);
        assertThat(dispatcher.getThreadNumber())
                .isEqualTo(1);
    }


    @Test
    public void isSynchronized_DefaultSetUp_False() {
        ScheduledTaskChannel scheduledTask =
                new ScheduledTaskChannelMinute("MyTaskName", "ping", 1);
        Dispatcher dispatcher = (Dispatcher) ScheduledTaskFactory.addTask(scheduledTask);
        assertThat(dispatcher.isSynchronized())
                .isEqualTo(false);
    }


    @Test
    public void getLoggers_DefaultSetUp_2loggers() {
        ScheduledTaskChannel scheduledTask =
                new ScheduledTaskChannelMinute("MyTaskName", "ping", 1);
        Dispatcher dispatcher = (Dispatcher) ScheduledTaskFactory.addTask(scheduledTask);
        assertThat(dispatcher.getLoggers()).hasSize(2);
    }


}