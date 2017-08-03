package com.atexpose.dispatcher.channels.tasks;

import com.atexpose.util.watch.IWatch;
import com.atexpose.util.watch.Watch;
import io.schinzel.basicutils.Thrower;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;

public class ScheduledTaskChannelMinute extends ScheduledTaskChannel {

    /**
     * Sets up a tasks that executes every argument amount of minutes.
     *
     * @param taskName          The name of the task.
     * @param request           The request to execute.
     * @param intervalInMinutes The interval in minutes.
     */
    public ScheduledTaskChannelMinute(String taskName, String request, int intervalInMinutes) {
        this(taskName, request, intervalInMinutes, Watch.create());
    }


    private ScheduledTaskChannelMinute(String taskName, String request, int intervalInMinutes, IWatch watch) {
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
        Thrower.throwIfVarOutsideRange(minuteInterval, "Minutes", 1, 1440);
        return minuteInterval;
    }


}
