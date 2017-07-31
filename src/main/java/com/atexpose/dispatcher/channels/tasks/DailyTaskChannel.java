package com.atexpose.dispatcher.channels.tasks;

import io.schinzel.basicutils.Thrower;

import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.regex.Pattern;

/**
 * The purpose of this class is to return a request daily at a set time.
 */
public class DailyTaskChannel extends ScheduledTaskChannel {


    /**
     * Sets up a daily task that executes once per days.
     *
     * @param taskName  The name of the task.
     * @param request   The request to execute.
     * @param timeOfDay What time of day to execute. Format HH:mm, e.g. 23:55
     * @param zoneId    The zone of the time-of-day
     */
    public DailyTaskChannel(String taskName, String request, String timeOfDay, ZoneId zoneId) {
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
    DailyTaskChannel(String taskName, String request, String timeOfDay, ZoneId zoneId, IWatch watch) {
        super(taskName, request, ChronoUnit.DAYS, 1,
                "Every day at " + timeOfDay + "[" + zoneId.getId() + "]",
                getZonedDateTime(validateTimeOfDay(timeOfDay), zoneId, watch),
                watch);
    }


    /**
     * @param timeOfDay E.g. "23:55"
     * @param zoneId    The zone which the argument time-of-day string is in
     * @param watch     Watch the returns the time now
     * @return The argument data as a ZonedDateTime
     */
    static ZonedDateTime getZonedDateTime(String timeOfDay, ZoneId zoneId, IWatch watch) {
        return //Get the local time from time-of-day string
                LocalTime.parse(timeOfDay)
                        //Get LocalDateTime by setting the date to today in the argument zone
                        .atDate(watch.getLocalDate(zoneId))
                        //Get ZoneDateTime by setting the  argument zone
                        .atZone(zoneId);
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


}

