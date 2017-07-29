package com.atexpose.dispatcher.channels.tasks;

import io.schinzel.basicutils.Thrower;

import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.regex.Pattern;

public class DailyTaskChannel extends ScheduledTaskChannel {
    /** Pattern for time for daily tasks. */
    private static final Pattern TIME_PATTERN = Pattern.compile("^[0-2][0-9]:[0-5][0-9]");


    /**
     * Sets up a daily task that executes once per days.
     *
     * @param taskName  The name of the task.
     * @param request   The request to execute.
     * @param timeOfDay What time of day to execute. Format HH:mm, e.g. 23:55
     */
    public DailyTaskChannel(String taskName, String request, String timeOfDay) {
        this(taskName, request, timeOfDay, Watch.create());
    }


    /**
     * Sets up a daily task that executes once per days.
     *
     * @param taskName  The name of the task.
     * @param request   The request to execute.
     * @param timeOfDay What time of day to execute. Format HH:mm, e.g. 23:55
     */
    DailyTaskChannel(String taskName, String request, String timeOfDay, IWatch watch) {
        super(taskName, request, ChronoUnit.DAYS, 1,
                "Every day at " + timeOfDay,
                LocalTime.parse(validateTimeOfDay(timeOfDay))
                        .atDate(watch.getLocalDate(watch.UTC))
                        .atZone(watch.UTC),
                watch);
    }


    /**
     * Validates argument day of time string. Throws exception if not valid.
     *
     * @param timeOfDay Time-string to validate. E.g. "23:55"
     * @return The argument time of day
     */
    private static String validateTimeOfDay(String timeOfDay) {
        Thrower.throwIfFalse(TIME_PATTERN.matcher(timeOfDay).matches())
                .message("Incorrect task time: '" + timeOfDay + "'. Correct format is HH:mm, e.g. 09:00 or 23:55.");
        return timeOfDay;
    }
}

