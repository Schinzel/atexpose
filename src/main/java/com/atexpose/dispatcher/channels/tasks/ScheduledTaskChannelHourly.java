package com.atexpose.dispatcher.channels.tasks;

import com.atexpose.util.watch.IWatch;
import com.atexpose.util.watch.Watch;
import io.schinzel.basicutils.thrower.Thrower;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;

/**
 * The purpose of this class is to fire a task at a certain minute each hour.
 *
 * ZoneId is an argument as some time zones like Indian Standard time is not
 * full hours ahead or behind.
 */
public class ScheduledTaskChannelHourly extends ScheduledTaskChannel {

    public ScheduledTaskChannelHourly(String taskName, String request, int minuteOfHour, String zoneId) {
        this(taskName, request, minuteOfHour, zoneId, Watch.create());
    }


    private ScheduledTaskChannelHourly(String taskName, String request, int minuteOfHour, String zoneId, IWatch watch) {
        super(taskName, request, ChronoUnit.HOURS, 1,
                "Every hour at " + minuteOfHour + " minutes",
                getInitialFireTime(minuteOfHour, TaskUtil.getZoneId(zoneId), watch),
                watch);
    }


    /**
     * @param minuteOfHour The minute of the hour
     * @return The argument minute of the hour
     */
    static int validateMinuteOfHour(int minuteOfHour) {
        Thrower.throwIfVarOutsideRange(minuteOfHour, "minutesOfHour", 0, 59);
        return minuteOfHour;
    }


    /**
     *
     * @param minuteOfHour The minute of the hour to start
     * @param zoneId Which time zone this is for
     * @param watch A watch for testing
     * @return The initial time to fire
     */
    static ZonedDateTime getInitialFireTime(int minuteOfHour, ZoneId zoneId, IWatch watch) {
        ZonedDateTime now = watch.getNowAsInstant()
                .atZone(zoneId);
        ZonedDateTime fireTime = now
                .withMinute(minuteOfHour)
                .withSecond(0)
                .withNano(0);
        ZonedDateTime aFewSecondsInTheFuture = now
                .plusSeconds(30);
        return fireTime.isBefore(aFewSecondsInTheFuture)
                ? fireTime.plusHours(1)
                : fireTime;
    }
}