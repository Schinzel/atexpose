package com.atexpose.dispatcherfactories;

import com.atexpose.dispatcher.Dispatcher;
import com.atexpose.dispatcher.channels.tasks.ScheduledTaskChannel;
import com.atexpose.dispatcher.channels.tasks.ScheduledTaskChannelMinute;
import com.atexpose.dispatcher.parser.TextParser;
import com.atexpose.dispatcher.wrapper.CsvWrapper;
import org.junit.Test;

import static org.assertj.core.api.Assertions.*;

public class ScheduledTaskFactoryTest {

    @Test
    public void getChannel_DefaultSetUp_ArgumentTaks() {
        ScheduledTaskChannel scheduledTask =
                new ScheduledTaskChannelMinute("MyTaskName", "ping", 1);
        Dispatcher dispatcher = (Dispatcher) ScheduledTaskFactory.addTask(scheduledTask);
        assertThat(dispatcher.getChannel()).isEqualTo(scheduledTask);
    }


    @Test
    public void getParser_DefaultSetUp_CommandLineChannel() {
        ScheduledTaskChannel scheduledTask =
                new ScheduledTaskChannelMinute("MyTaskName", "ping", 1);
        Dispatcher dispatcher = (Dispatcher) ScheduledTaskFactory.addTask(scheduledTask);
        assertThat(dispatcher.getParser().getClass()).isEqualTo(TextParser.class);
    }


    @Test
    public void getWrapper_DefaultSetUp_CsvWrapper() {
        ScheduledTaskChannel scheduledTask =
                new ScheduledTaskChannelMinute("MyTaskName", "ping", 1);
        Dispatcher dispatcher = (Dispatcher) ScheduledTaskFactory.addTask(scheduledTask);
        assertThat(dispatcher.getWrapper().getClass())
                .isEqualTo(CsvWrapper.class);
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
    public void getKey_DefaultSetUp_CommandLine() {
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


    @Test
    public void getLoggers_DefaultSetUp_1EventLog() {
        ScheduledTaskChannel scheduledTask =
                new ScheduledTaskChannelMinute("MyTaskName", "ping", 1);
        Dispatcher dispatcher = (Dispatcher) ScheduledTaskFactory.addTask(scheduledTask);
    }
}