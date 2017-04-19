package com.atexpose.dispatcher.logging;

import com.atexpose.dispatcher.logging.crypto.ICrypto;
import com.atexpose.dispatcher.logging.crypto.NoCrypto;
import com.atexpose.dispatcher.logging.format.ILogFormatter;
import com.atexpose.dispatcher.logging.format.LogFormatterFactory;
import com.atexpose.dispatcher.logging.writer.ILogWriter;
import com.atexpose.dispatcher.logging.writer.LogWriterFactory;
import lombok.Builder;
import lombok.experimental.Accessors;

import java.util.Map;

/**
 * The purpose of this class is to format and write log entries. For example
 * the log writer to write to file or system out. The formatter can be for
 * example JSON.
 *
 * @author schinzel
 */
@Builder
@Accessors(prefix = "m")
public class Logger {
    /** The type of the logger. E.g. error or event */
    @Builder.Default
    private LoggerType mLoggerType = LoggerType.EVENT;
    /** Handles the writing of log entries. */
    @Builder.Default
    private ILogWriter mLogWriter = LogWriterFactory.SYSTEM_OUT.getInstance();
    /** Handles the formatting of log entries. */
    @Builder.Default
    private ILogFormatter mLogFormatter = LogFormatterFactory.JSON.getInstance();
    /** Used to encrypt part of the log data. */
    @Builder.Default
    private ICrypto mCrypto = new NoCrypto();


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
