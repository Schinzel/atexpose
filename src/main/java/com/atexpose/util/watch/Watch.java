package com.atexpose.util.watch;

import java.time.*;

/**
 * The run-time purpose of this class is simply to return an instant that represents now.
 */

public class Watch implements IWatch {

    /**
     * @return A new instance.
     */
    public static Watch create() {
        return new Watch();
    }


    /**
     * @return An instant representing now.
     */
    public Instant getNowAsInstant() {
        return Instant.now();
    }
}