package com.atexpose.dispatcher_factories;

import com.atexpose.dispatcher.Dispatcher;
import com.atexpose.dispatcher.IDispatcher;
import com.atexpose.dispatcher.channels.tasks.*;
import com.atexpose.dispatcher.logging.Logger;
import com.atexpose.dispatcher.logging.LoggerType;
import com.atexpose.dispatcher.logging.format.LogFormatterFactory;
import com.atexpose.dispatcher.logging.writer.LogWriterFactory;
import com.atexpose.dispatcher.parser.text_parser.TextParser;
import com.atexpose.dispatcher.wrapper.CsvWrapper;
import lombok.Builder;
import lombok.val;

@SuppressWarnings("unused")
public class ScheduledTaskFactory {


    ScheduledTaskFactory() {
    }

    @Builder(builderMethodName = "hourlyTaskBuilder", builderClassName = "HourlyTaskBuilder")
    static IDispatcher newHourlyTask(String taskName, String request, int minuteOfTheHour, String timeZone){
        val taskChannel =new ScheduledTaskChannelHourly(taskName, request, minuteOfTheHour, timeZone);
        return  ScheduledTaskFactory.addTask(taskChannel);
    }

    @Builder(builderMethodName = "minuteTaskBuilder", builderClassName = "MinuteTaskBuilder")
    static IDispatcher newMinuteTask(String taskName, String request, int minutes) {
        ScheduledTaskChannelMinute scheduledTaskChannel = new ScheduledTaskChannelMinute(taskName, request, minutes);
        return ScheduledTaskFactory.addTask(scheduledTaskChannel);
    }


    @Builder(builderMethodName = "dailyTaskBuilder", builderClassName = "DailyTaskBuilder")
    static IDispatcher newDailyTask(String taskName, String request, String timeOfDay, String timeZone) {
        ScheduledTaskChannelDaily scheduledTaskChannel = new ScheduledTaskChannelDaily(taskName, request, timeOfDay, timeZone);
        return ScheduledTaskFactory.addTask(scheduledTaskChannel);
    }


    @Builder(builderMethodName = "monthlyTaskBuilder", builderClassName = "MonthlyTaskBuilder")
    static IDispatcher newMonthlyTask(String taskName, String request, String timeOfDay, int dayOfMonth, String timeZone) {
        ScheduledTaskChannelMonthly scheduledTaskChannel = new ScheduledTaskChannelMonthly(taskName, request, timeOfDay, dayOfMonth, timeZone);
        return ScheduledTaskFactory.addTask(scheduledTaskChannel);
    }


    static IDispatcher addTask(ScheduledTaskChannel channel) {
        Logger errorLogger = Logger.builder()
                .loggerType(LoggerType.ERROR)
                .logFormatter(LogFormatterFactory.JSON.create())
                .logWriter(LogWriterFactory.SYSTEM_OUT.create())
                .build();
        Logger eventLogger = Logger.builder()
                .loggerType(LoggerType.EVENT)
                .logFormatter(LogFormatterFactory.JSON.create())
                .logWriter(LogWriterFactory.SYSTEM_OUT.create())
                .build();
        return Dispatcher.builder()
                .name("ScheduledTask_" + channel.getTaskName())
                .accessLevel(3)
                .isSynchronized(false)
                .channel(channel)
                .parser(new TextParser())
                .wrapper(new CsvWrapper())
                .noOfThreads(1)
                .build()
                .addLogger(errorLogger)
                .addLogger(eventLogger);
    }
}
