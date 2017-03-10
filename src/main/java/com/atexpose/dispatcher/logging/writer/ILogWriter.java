package com.atexpose.dispatcher.logging.writer;

import com.atexpose.dispatcher.logging.LoggerType;

/**
 * The purpose of this interface is to write a log entry to a generic output.
 * The output could be a file, system out or similar.
 *
 * @author schinzel
 */
public interface ILogWriter {

    /**
     * This is the main method of the class. Should do the actual logging.
     *
     * @param logEntry The log entry to log.
     */
    void log(String logEntry);


    /**
     * A pseudo constructor. 
     * 
     * @param dispatcherName The name of the dispatchers
     * @param loggerType The type of logger, error or event.
     */
    void setUp(String dispatcherName, LoggerType loggerType);

}
