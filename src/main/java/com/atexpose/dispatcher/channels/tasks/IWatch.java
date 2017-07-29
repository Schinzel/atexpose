package com.atexpose.dispatcher.channels.tasks;

import java.time.Instant;

/**
 * The purpose of this interface is to return an Instant that represents now.
 */

public interface IWatch {

    Instant getInstant();
}
