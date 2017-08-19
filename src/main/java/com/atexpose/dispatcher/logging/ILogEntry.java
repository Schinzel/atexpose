package com.atexpose.dispatcher.logging;

import io.schinzel.basicutils.crypto.cipher.ICipher;

import java.util.Map;

/**
 * The purpose of this class is to represent a single log entry.
 */
public interface ILogEntry {
    /**
     * @param crypto Applied to a subset of the data.
     * @return An immutable map. Keys are log keys and values are log values. Example
     * "read_time_in_ms":"12"
     */
    Map<String, String> getLogData(ICipher crypto);


    /**
     * @return True entry is an error. Else false.
     */
    boolean isError();
}
