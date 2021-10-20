package com.atexpose.dispatcher.logging.writer;

import com.atexpose.ProjectProperties;
import io.schinzel.basicutils.UTF8;

import java.io.IOException;

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
            System.out.write(UTF8.getBytes(logEntry));
            System.out.write(UTF8.getBytes(ProjectProperties.OS_LINE_SEPARATOR));
            System.out.flush();
        } catch (IOException ex) {
            throw new RuntimeException("There was an error when SystemOutLogger was trying to log. " + ex.getMessage());
        }
    }

    
}
