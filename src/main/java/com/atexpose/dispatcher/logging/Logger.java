package com.atexpose.dispatcher.logging;

import com.atexpose.dispatcher.logging.crypto.ICrypto;
import com.atexpose.dispatcher.logging.crypto.NoCrypto;
import com.atexpose.dispatcher.logging.format.ILogFormatter;
import com.atexpose.dispatcher.logging.writer.ILogWriter;
import lombok.Builder;

import java.util.Map;

/**
 * The purpose of this class is to format and write log entries. For example
 * the log writer to write to file or system out. The formatter can be for
 * example JSON.
 *
 * @author schinzel
 */
public class Logger {
    /** The type of the logger. E.g. error or event */
    private final LoggerType mLoggerType;
    /** Handles the writing of log entries. */
    private final ILogWriter mLogWriter;
    /** Handles the formatting of log entries. */
    private final ILogFormatter mLogFormatter;
    /** Used to encrypt part of the log data. */
    private final ICrypto mCrypto;


    @Builder
    Logger(LoggerType loggerType, ILogFormatter logFormat, ILogWriter logWriter, ICrypto crypto) {
        mLoggerType = loggerType;
        mLogFormatter = logFormat;
        mLogWriter = logWriter;
        mCrypto = (crypto == null)
                ? new NoCrypto()
                : crypto;
    }


    /**
     * Format and write a log entry.
     *
     * @param logEntry The entry to add to log.
     */
    public void log(LogEntry logEntry) {
        //Should log this entry if this is an event logger
        boolean shouldLogThisLogEntry = this.isEventLogger();
        //If this was not an event logger
        if (!shouldLogThisLogEntry) {
            //Should log this entry if this is an error logger and the entry is an error
            shouldLogThisLogEntry = this.isErrorLogger() && logEntry.isError();
        }
        //If should log this log entry
        if (shouldLogThisLogEntry) {
            Map<LogKey, String> logData = logEntry.getLogData(mCrypto);
            String logEntryAsString = mLogFormatter.formatLogEntry(logData);
            mLogWriter.log(logEntryAsString);
        }
    }


    /**
     * @return True if this is an event logger, else false.
     */
    private boolean isEventLogger() {
        return (this.mLoggerType == LoggerType.EVENT);
    }


    /**
     * @return True if this is an error logger, else false.
     */
    private boolean isErrorLogger() {
        return (this.mLoggerType == LoggerType.ERROR);
    }

}
