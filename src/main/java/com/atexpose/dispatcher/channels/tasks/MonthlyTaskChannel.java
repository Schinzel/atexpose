package com.atexpose.dispatcher.channels.tasks;

import io.schinzel.basicutils.Thrower;

import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.regex.Pattern;

/**
 * The purpose of this class is to return a request monthly at a set day of month at a set time of
 * day
 */
public class MonthlyTaskChannel extends ScheduledTaskChannel {


    /**
     * Sets up a monthly task that executes once per month.
     *
     * @param taskName   The name of the task.
     * @param request    The request to execute.
     * @param timeOfDay  What time of day to execute. Format HH:mm, e.g. 23:55
     * @param dayOfMonth Day of month to execute. Min 1 and max 28.
     */
    public MonthlyTaskChannel(String taskName, String request, String timeOfDay, int dayOfMonth) {
        this(taskName, request, timeOfDay, dayOfMonth, Watch.create());
    }


    MonthlyTaskChannel(String taskName, String request, String timeOfDay, int dayOfMonth, IWatch watch) {
        super(taskName, request, ChronoUnit.MONTHS, 1,
                "Once a month at " + timeOfDay + " on day of month " + dayOfMonth,
                LocalTime.parse(validateTimeOfDay(timeOfDay))
                        .atDate(watch.getLocalDate(watch.UTC))
                        .withDayOfMonth(validateDayOfMonth(dayOfMonth))
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
        Pattern timePattern = Pattern.compile("^[0-2][0-9]:[0-5][0-9]");
        Thrower.throwIfFalse(timePattern.matcher(timeOfDay).matches())
                .message("Incorrect task time: '" + timeOfDay + "'. Correct format is HH:mm, e.g. 09:00 or 23:55.");
        return timeOfDay;
    }


    /**
     * @param dayOfMonth The day of the month. A number between 1 and 28.
     * @return The argument day of month
     */
    private static int validateDayOfMonth(int dayOfMonth) {
        Thrower.throwIfVarOutsideRange(dayOfMonth, "dayOfMonth", 1, 28);
        return dayOfMonth;
    }


}
