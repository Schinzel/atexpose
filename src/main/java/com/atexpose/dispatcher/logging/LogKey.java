package com.atexpose.dispatcher.logging;

import java.util.Locale;

/**
 * /**
 * The purpose of this enum is to hold the keys of data being logged.
 * <p>
 * Created by Schinzel on 2017-03-09.
 */
public enum LogKey {
    CALL_TIME_UTC,
    ARGUMENTS,
    SENDER,
    THREAD,
    READ_TIME_IN_MS,
    EXEC_TIME_IN_MS,
    WRITE_TIME_IN_MS,
    METHOD_NAME,
    FILENAME,
    RESPONSE,
    REQUEST;


    @Override
    public String toString() {
        return this.name().toLowerCase(Locale.ENGLISH);
    }
}
