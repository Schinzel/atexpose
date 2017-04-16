package com.atexpose;

import com.atexpose.dispatcher.Dispatcher;
import com.atexpose.dispatcher.channels.ScheduledTaskChannel;
import com.atexpose.dispatcher.logging.Logger;
import com.atexpose.dispatcher.logging.LoggerType;
import com.atexpose.dispatcher.logging.format.LogFormatterFactory;
import com.atexpose.dispatcher.logging.writer.LogWriterFactory;
import com.atexpose.dispatcher.parser.AbstractParser;
import com.atexpose.dispatcher.parser.TextParser;
import com.atexpose.dispatcher.wrapper.CsvWrapper;
import io.schinzel.basicutils.Thrower;

/**
 * Sets up scheduled tasks.
 *
 * Created by schinzel on 2017-04-16.
 */
interface IAtExposeTasks<T extends IAtExpose<T>> extends IAtExpose<T> {

    /**
     * Sets up a task to run once a day at the argument time of the day.
     *
     * @param taskName  The name of the task.
     * @param request   The request to execute. Example: "time", "echo 123"
     * @param timeOfDay The time for the day. UTC. Examples "23:55" "07:05"
     * @return This for chaining.
     */
    default T addDailyTask(String taskName, String request, String timeOfDay) {
        ScheduledTaskChannel scheduledTaskChannel = new ScheduledTaskChannel(taskName, request, timeOfDay);
        return this.addTask(taskName, scheduledTaskChannel);
    }


    /**
     * Sets up a task to run every X minutes. Runs the first time after the argument number of minutes.
     *
     * @param taskName The name of the task.
     * @param request  The request to execute. Example: "time", "echo 123"
     * @param minutes  The interval at which to execute the task.
     * @return This for chaining.
     */
    default T addTask(String taskName, String request, int minutes) {
        ScheduledTaskChannel scheduledTaskChannel = new ScheduledTaskChannel(taskName, request, minutes);
        return this.addTask(taskName, scheduledTaskChannel);
    }


    /**
     * @param taskName   The name of the task.
     * @param request    The request to execute. Example: "time", "echo 123"
     * @param timeOfDay  The time for the day. UTC. Examples "23:55" "07:05"
     * @param dayOfMonth The day of the month the task can execute.
     * @return This for chaining.
     */
    default T addMonthlyTask(String taskName, String request, String timeOfDay, int dayOfMonth) {
        ScheduledTaskChannel scheduledTaskChannel = new ScheduledTaskChannel(taskName, request, timeOfDay, dayOfMonth);
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
        AbstractParser parser = new TextParser();
        parser.parseRequest(scheduledTask.getRequestAsString());
        String methodName = parser.getMethodName();
        Thrower.throwIfFalse(this.getAPI().methodExits(methodName), "No such method '" + methodName + "'");
        String dispatcherName = "ScheduledTask_" + taskName;
        Dispatcher dispatcher = Dispatcher.builder()
                .name(dispatcherName)
                .accessLevel(3)
                .channel(scheduledTask)
                .parser(parser)
                .wrapper(new CsvWrapper())
                .noOfThreads(1)
                .api(this.getAPI())
                .build();
        Logger errorLogger = Logger.builder()
                .loggerType(LoggerType.ERROR)
                .logFormat(LogFormatterFactory.JSON.getInstance())
                .logWriter(LogWriterFactory.SYSTEM_OUT.getInstance())
                .build();
        Logger eventLogger = Logger.builder()
                .loggerType(LoggerType.EVENT)
                .logFormat(LogFormatterFactory.JSON.getInstance())
                .logWriter(LogWriterFactory.SYSTEM_OUT.getInstance())
                .build();
        dispatcher.addLogger(errorLogger).addLogger(eventLogger);
        return this.startDispatcher(dispatcher, false, false);
    }
}
