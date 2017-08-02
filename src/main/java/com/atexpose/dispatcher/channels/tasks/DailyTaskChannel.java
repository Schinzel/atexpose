package com.atexpose.dispatcher.channels.tasks;

import java.time.temporal.ChronoUnit;

/**
 * The purpose of this class is to return a request daily at a set time of day.
 */
public class DailyTaskChannel extends ScheduledTaskChannel {


    /**
     * Sets up a task that executes once per days.
     *
     * @param taskName  The name of the task.
     * @param request   The request to execute.
     * @param timeOfDay What time of day to execute. Format HH:mm, e.g. 23:55
     * @param zoneId    The zone of the time-of-day
     */
    public DailyTaskChannel(String taskName, String request, String timeOfDay, String zoneId) {
        this(taskName, request, timeOfDay, zoneId, Watch.create());
    }


    /**
     * Sets up a daily task that executes once per days.
     *
     * @param taskName  The name of the task.
     * @param request   The request to execute.
     * @param timeOfDay What time of day to execute. Format HH:mm, e.g. 23:55
     * @param zoneId    The zone of the time-of-day
     * @param watch     Represents the time now
     */
    DailyTaskChannel(String taskName, String request, String timeOfDay, String zoneId, IWatch watch) {
        super(taskName, request, ChronoUnit.DAYS, 1,
                "Every day at " + timeOfDay + "[" + TaskUtil.getZoneId(zoneId).getId() + "]",
                TaskUtil.getZonedDateTime(TaskUtil.validateTimeOfDay(timeOfDay), TaskUtil.getZoneId(zoneId), watch),
                watch);
    }


}

