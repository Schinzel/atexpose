package com.atexpose.dispatcher.channels.tasks;

import io.schinzel.basicutils.Thrower;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;

public class MinuteIntervalTaskChannel extends ScheduledTaskChannel {

    /**
     * Sets up a tasks that executes every argument amount of minutes.
     *
     * @param taskName          The name of the task.
     * @param request           The request to execute.
     * @param intervalInMinutes The interval in minutes.
     */
    public MinuteIntervalTaskChannel(String taskName, String request, int intervalInMinutes) {
        this(taskName, request, intervalInMinutes, Watch.create());
    }


    MinuteIntervalTaskChannel(String taskName, String request, int intervalInMinutes, IWatch watch) {
        super(taskName, request, ChronoUnit.MINUTES, intervalInMinutes,
                "Every " + intervalInMinutes + " minutes",
                ZonedDateTime.now(ZoneId.of("UTC")).plusMinutes(validateMinuteInterval(intervalInMinutes)),
                watch);
    }


    /**
     * @param minuteInterval The interval in minutes to validate
     * @return The argument minutes
     */
    private static int validateMinuteInterval(int minuteInterval) {
        Thrower.throwIfVarOutsideRange(minuteInterval, "Mintues", 1, 1440);
        return minuteInterval;
    }


}
