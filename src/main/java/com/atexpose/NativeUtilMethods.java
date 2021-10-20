package com.atexpose;

import com.atexpose.errors.RuntimeError;
import com.atexpose.util.DateTimeStrings;
import io.schinzel.basicutils.Sandman;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

/**
 * Miscellaneous utility methods native to @expose
 *
 * @author Schinzel
 */
@SuppressWarnings({"WeakerAccess", "SameParameterValue", "UnusedReturnValue"})
public class NativeUtilMethods {
    /** The time the instance was started. */
    private final Instant mInstanceStartTime = Instant.now();


    /**
     * @param s The string to return
     * @return Returns the argument string.
     */
    @Expose(
            arguments = {"String"},
            description = "Returns the argument string. Util method for testing.",
            labels = {"@Expose", "Util"},
            requiredArgumentCount = 1
    )
    public String echo(String s) {
        return s;
    }


    @Expose(
            description = "Simply returns the string \"pong\". Util method for testing.",
            labels = {"@Expose", "Util"}
    )
    public String ping() {
        return "pong";
    }


    @Expose(
            description = "Returns the current server time in UTC.",
            labels = {"@Expose", "Util"}
    )
    public String time() {
        return DateTimeStrings.getDateTimeUTC();
    }


    @Expose(
            description = "Returns the time when the server was started",
            labels = {"@Expose", "Util"}
    )
    public String startTime() {
        DateTimeFormatter dateTimeFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String utcTime = LocalDateTime.ofInstant(mInstanceStartTime, ZoneId.of("UTC")).format(dateTimeFormat);
        String swedishTime = LocalDateTime.ofInstant(mInstanceStartTime, ZoneId.of("Europe/Stockholm")).format(dateTimeFormat);
        return "SWE:" + swedishTime + "  UTC:" + utcTime;
    }


    @Expose(
            requiredAccessLevel = 3,
            requiredArgumentCount = 1,
            arguments = {"Int"},
            description = "Put the invoking thread to sleep for the argument amount of milliseconds.",
            labels = {"@Expose", "Util"}
    )
    public String snooze(int timeInMilliseconds) {
        Sandman.snoozeMillis(timeInMilliseconds);
        return "Good morning! Slept for " + timeInMilliseconds + " milliseconds";
    }


    @Expose(
            description = "Throws an error. Useful for testing.",
            requiredAccessLevel = 3,
            labels = {"@Expose", "Util"}
    )
    public String throwError() {
        throw new RuntimeError("Requested error thrown");
    }


}
