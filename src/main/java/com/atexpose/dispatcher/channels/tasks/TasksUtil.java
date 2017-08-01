package com.atexpose.dispatcher.channels.tasks;

import io.schinzel.basicutils.Checker;
import io.schinzel.basicutils.Thrower;

import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.regex.Pattern;

/**
 * The purpose of this class is to hold that is shared between MonthlyTaskChannel and
 * DailyTaskChannel.
 */

class TasksUtil {

    /**
     * @param zoneId
     * @return The zone id of the argument zone id
     */
    static ZoneId getZoneId(String zoneId) {
        if (Checker.isEmpty(zoneId)) {
            return ZoneId.of("UTC");
        }
        return ZoneId.of(zoneId);
    }


    /**
     * @param timeOfDay  E.g. "23:55".
     * @param dayOfMonth Day of month. Min 1 and max 28.
     * @param zoneId     The zone which the argument time-of-day string and day-of-month is in
     * @param watch      Represents the time now
     * @return The argument time-of-day and day-of-month as a date in the argument zone.
     */
    static ZonedDateTime getZonedDateTime(String timeOfDay, int dayOfMonth, ZoneId zoneId, IWatch watch) {
        return TasksUtil.getZonedDateTime(timeOfDay, zoneId, watch).withDayOfMonth(dayOfMonth);
    }


    /**
     * @param timeOfDay E.g. "23:55"
     * @param zoneId    The zone which the argument time-of-day string is in
     * @param watch     Watch the returns the time now
     * @return The argument time as date today in the argument zone
     */
    static ZonedDateTime getZonedDateTime(String timeOfDay, ZoneId zoneId, IWatch watch) {
        return LocalTime
                //Get the local time from time-of-day string
                .parse(timeOfDay)
                //Get date by setting  the date to today in the argument zone
                .atDate(watch.getLocalDate(zoneId))
                //Get ZoneDateTime by setting the  argument zone id
                .atZone(zoneId);
    }


    /**
     * Validates argument day of time string. Throws exception if not valid.
     *
     * @param timeOfDay Time-string to validate. E.g. "23:55"
     * @return The argument time of day
     */
    static String validateTimeOfDay(String timeOfDay) {
        Pattern timePattern = Pattern.compile("^[0-2][0-4]:[0-5][0-9]");
        Thrower.throwIfFalse(timePattern.matcher(timeOfDay).matches())
                .message("Incorrect task time: '" + timeOfDay + "'. Correct format is HH:mm, e.g. 09:00 or 23:55.");
        return timeOfDay;
    }


    /**
     * @param dayOfMonth The day of the month. A number between 1 and 28.
     * @return The argument day of month
     */
    static int validateDayOfMonth(int dayOfMonth) {
        Thrower.throwIfVarOutsideRange(dayOfMonth, "dayOfMonth", 1, 28);
        return dayOfMonth;
    }

}
