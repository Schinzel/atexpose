package com.atexpose.atexpose;

import com.atexpose.dispatcher.Dispatcher;
import com.atexpose.dispatcher.channels.tasks.ScheduledTaskChannelDaily;
import com.atexpose.dispatcher.channels.tasks.ScheduledTaskChannelMinute;
import com.atexpose.dispatcher.channels.tasks.ScheduledTaskChannelMonthly;
import com.atexpose.dispatcher.channels.tasks.ScheduledTaskChannel;
import com.atexpose.dispatcher.logging.Logger;
import com.atexpose.dispatcher.logging.LoggerType;
import com.atexpose.dispatcher.logging.format.LogFormatterFactory;
import com.atexpose.dispatcher.logging.writer.LogWriterFactory;
import com.atexpose.dispatcher.parser.IParser;
import com.atexpose.dispatcher.parser.TextParser;
import com.atexpose.dispatcher.wrapper.CsvWrapper;
import io.schinzel.basicutils.Thrower;

/**
 * Sets up scheduled tasks.
 * <p>
 * Created by schinzel on 2017-04-16.
 */
public interface IAtExposeTasks<T extends IAtExpose<T>> extends IAtExpose<T> {


    /**
     * Sets up a task to run every X minutes. Runs the first time after the argument number of
     * minutes.
     *
     * @param taskName The name of the task.
     * @param request  The request to execute. Example: "time", "echo 123"
     * @param minutes  The interval at which to execute the task.
     * @return This for chaining.
     */
    default T addMinuteTask(String taskName, String request, int minutes) {
        ScheduledTaskChannelMinute scheduledTaskChannel = new ScheduledTaskChannelMinute(taskName, request, minutes);
        return this.addTask(taskName, scheduledTaskChannel);
    }


    /**
     * Sets up a task to run once a day at the argument time of the day.
     *
     * @param taskName  The name of the task.
     * @param request   The request to execute. E.g. "time", "echo 123"
     * @param timeOfDay The time of day in the argument time zone. E.g. "23:55" "07:05"
     * @param zoneId    The time zone. E.g. "UTC", "Europe/Stockholm"
     * @return This for chaining.
     */
    default T addDailyTask(String taskName, String request, String timeOfDay, String zoneId) {
        ScheduledTaskChannelDaily scheduledTaskChannel = new ScheduledTaskChannelDaily(taskName, request, timeOfDay, zoneId);
        return this.addTask(taskName, scheduledTaskChannel);
    }


    /**
     * @param taskName   The name of the task.
     * @param request    The request to execute. Examples: "time", "echo 123"
     * @param timeOfDay  TThe time of day in the argument time zone. Examples: "23:55" "07:05"
     * @param dayOfMonth The day of the month in the argument time zone.
     * @param zoneId     The time zone. E.g. "UTC", "Europe/Stockholm"
     * @return This for chaining.
     */
    default T addMonthlyTask(String taskName, String request, String timeOfDay, int dayOfMonth, String zoneId) {
        ScheduledTaskChannelMonthly scheduledTaskChannel = new ScheduledTaskChannelMonthly(taskName, request, timeOfDay, dayOfMonth, zoneId);
        return this.addTask(taskName, scheduledTaskChannel);
    }


    /**
     * Removes a running scheduled task. Is shut down immediately.
     *
     * @param taskName The name of the task to remove.
     * @return This for chaining.
     */
    default T removeTask(String taskName) {
        String dispatcherName = "ScheduledTask_" + taskName;
        this.closeDispatcher(dispatcherName);
        return this.getThis();
    }


    default T addTask(String taskName, ScheduledTaskChannel scheduledTask) {
        IParser parser = new TextParser();
        String methodName = parser
                .getRequest(scheduledTask.getTaskRequest())
                .getMethodName();
        Thrower.throwIfFalse(this.getAPI().methodExits(methodName), "No such method '" + methodName + "'");
        String dispatcherName = "ScheduledTask_" + taskName;
        Dispatcher dispatcher = Dispatcher.builder()
                .name(dispatcherName)
                .accessLevel(3)
                .isSynchronized(false)
                .channel(scheduledTask)
                .parser(parser)
                .wrapper(new CsvWrapper())
                .noOfThreads(1)
                .api(this.getAPI())
                .build();
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
        dispatcher.addLogger(errorLogger).addLogger(eventLogger);
        return this.startDispatcher(dispatcher, false);
    }
}
