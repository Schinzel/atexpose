package com.atexpose.dispatcher.channels.tasks;

import java.time.temporal.ChronoUnit;

/**
 * The purpose of this class is to return a request monthly at a set day of month at a set time of
 * day
 */
public class MonthlyTaskChannel extends ScheduledTaskChannel {


    /**
     * Sets up a task that executes once per month.
     *
     * @param taskName   The name of the task.
     * @param request    The request to execute.
     * @param timeOfDay  What time of day to execute in the argument time-zone. E.g. 23:55
     * @param dayOfMonth Day of month to execute. Min 1 and max 28.
     * @param zoneId     The zone of the argument time-of-day and day-of-month
     */
    public MonthlyTaskChannel(String taskName, String request, String timeOfDay, int dayOfMonth, String zoneId) {
        this(taskName, request, timeOfDay, dayOfMonth, zoneId, Watch.create());
    }


    MonthlyTaskChannel(String taskName, String request, String timeOfDay, int dayOfMonth, String zoneId, IWatch watch) {
        super(taskName, request, ChronoUnit.MONTHS, 1,
                "Once a month at " + timeOfDay + " on day of month " + dayOfMonth + " in time zone " + TasksUtil.getZoneId(zoneId).getId(),
                TasksUtil.getZonedDateTime(TasksUtil.validateTimeOfDay(timeOfDay), TasksUtil.validateDayOfMonth(dayOfMonth), TasksUtil.getZoneId(zoneId), watch),
                watch);
    }


}
