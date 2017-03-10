package com.atexpose;

import com.atexpose.errors.RuntimeError;
import com.atexpose.util.DateTimeStrings;
import io.schinzel.basicutils.MiscUtil;

/**
 * Miscellaneous utility methods.
 *
 * @author Schinzel
 */
@SuppressWarnings({"WeakerAccess", "SameParameterValue"})
public class Misc {

    /**
     * @param s The string to return
     * @return Returns the argument string.
     */
    @Expose(
            arguments = {"String"},
            description = {"Returns the argument string.", "Util method for testing."},
            labels = {"@Expose", "Util"},
            requiredArgumentCount = 1
    )
    public String echo(String s) {
        return s;
    }


    @Expose(
            description = {"Simply returns the string \"pong\".", "Util method for testing."},
            labels = {"@Expose", "Util"}
    )
    public String ping() {
        return "pong";
    }


    @Expose(
            description = {"Returns the current server time."},
            labels = {"@Expose", "Util"}
    )
    public String time() {
        return DateTimeStrings.getDateTimeUTC();
    }


    @Expose(
            requiredAccessLevel = 3,
            requiredArgumentCount = 1,
            arguments = {"Int"},
            description = {"Put the invoking thread to sleep for the argument amount of milliseconds."},
            labels = {"@Expose", "Util"}
    )
    public String snooze(int timeInMilliseconds) {
        MiscUtil.snooze(timeInMilliseconds);
        return "Good morning! Slept for " + timeInMilliseconds + " milliseconds";
    }


    @Expose(
            description = {"Throws an error. Useful for testing."},
            requiredAccessLevel = 3,
            labels = {"@Expose", "Util"}
    )
    public String throwError() {
        throw new RuntimeError("Requested error thrown");
    }


}
