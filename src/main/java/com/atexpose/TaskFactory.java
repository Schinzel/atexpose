package com.atexpose;

import com.atexpose.api.API;
import com.atexpose.dispatcher.Dispatcher;
import com.atexpose.dispatcher.channels.tasks.ScheduledTaskChannel;
import com.atexpose.dispatcher.channels.tasks.ScheduledTaskChannelDaily;
import com.atexpose.dispatcher.channels.tasks.ScheduledTaskChannelMinute;
import com.atexpose.dispatcher.channels.tasks.ScheduledTaskChannelMonthly;
import com.atexpose.dispatcher.logging.Logger;
import com.atexpose.dispatcher.logging.LoggerType;
import com.atexpose.dispatcher.logging.format.LogFormatterFactory;
import com.atexpose.dispatcher.logging.writer.LogWriterFactory;
import com.atexpose.dispatcher.parser.TextParser;
import com.atexpose.dispatcher.wrapper.CsvWrapper;
import io.schinzel.basicutils.Thrower;
import lombok.AllArgsConstructor;
import lombok.Builder;

@AllArgsConstructor
public class TaskFactory {
    private final API mAPI;


    @Builder(builderClassName = "MinuteTaskBuilder", builderMethodName = "minuteTaskBuilder")
    Dispatcher minuteTaskBuilder(String taskName, String request, int minutes) {
        ScheduledTaskChannelMinute scheduledTaskChannel = new ScheduledTaskChannelMinute(taskName, request, minutes);
        return this.addTask(taskName, scheduledTaskChannel);
    }


    @Builder(builderClassName = "DailyTaskBuilder", builderMethodName = "dailyTaskBuilder")
    Dispatcher dailyTaskBuilder(String taskName, String request, String timeOfDay, String zoneId) {
        ScheduledTaskChannelDaily scheduledTaskChannel = new ScheduledTaskChannelDaily(taskName, request, timeOfDay, zoneId);
        return this.addTask(taskName, scheduledTaskChannel);
    }


    @Builder(builderClassName = "MonthlyTaskBuilder", builderMethodName = "monthlyTaskBuilder")
    Dispatcher addMonthlyTask(String taskName, String request, String timeOfDay, int dayOfMonth, String zoneId) {
        ScheduledTaskChannelMonthly scheduledTaskChannel = new ScheduledTaskChannelMonthly(taskName, request, timeOfDay, dayOfMonth, zoneId);
        return this.addTask(taskName, scheduledTaskChannel);
    }


    Dispatcher addTask(String taskName, ScheduledTaskChannel scheduledTask) {
        String methodName = new TextParser()
                .getRequest(scheduledTask.getTaskRequest())
                .getMethodName();
        Thrower.throwIfFalse(mAPI.methodExits(methodName), "No such method '" + methodName + "'");
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
                .name("ScheduledTask_" + taskName)
                .accessLevel(3)
                .isSynchronized(false)
                .channel(scheduledTask)
                .parser(new TextParser())
                .wrapper(new CsvWrapper())
                .noOfThreads(1)
                .api(mAPI)
                .build()
                .addLogger(errorLogger)
                .addLogger(eventLogger);
    }
}
