package com.atexpose.dispatcher.logging.format;

import com.atexpose.dispatcher.logging.LogKey;

import java.util.Map;

/**
 * The purpose of this class is to take a set of log data values and its keys
 * and format these for adding to a log.
 *
 * @author schinzel
 */
public interface ILogFormatter {

    String formatLogEntry(Map<LogKey, String> logData);


}
