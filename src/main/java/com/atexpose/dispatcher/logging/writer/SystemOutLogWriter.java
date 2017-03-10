package com.atexpose.dispatcher.logging.writer;

import java.io.IOException;
import com.atexpose.util.EncodingUtil;
import com.atexpose.MyProperties;
import com.atexpose.dispatcher.logging.LoggerType;

/**
 * The purpose of this class is to write a log entry to system out.
 *
 * @author schinzel
 */
public class SystemOutLogWriter implements ILogWriter {

    @Override
    public void log(String logEntry) {
        SystemOutLogWriter.syncedOutputWrite(logEntry);
    }


    /**
     * Writes logs to system out. Is synchronized so that the log outputs
     * are not written over each other.
     *
     * @param logEntry The entry to add to log.
     */
    private static synchronized void syncedOutputWrite(String logEntry) {
        try {
            System.out.write(EncodingUtil.convertToByteArray(logEntry));
            System.out.write(EncodingUtil.convertToByteArray(MyProperties.OS_LINE_SEPARATOR));
            System.out.flush();
        } catch (IOException ex) {
            throw new RuntimeException("There was an error when SystemOutLogger was trying to log. " + ex.getMessage());
        }
    }


    @Override
    public void setUp(String dispatcherName, LoggerType loggerType) {
    }

}
