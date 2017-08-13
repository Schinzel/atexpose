package com.atexpose.dispatcher.logging;

import com.atexpose.dispatcher.logging.format.ILogFormatter;
import com.atexpose.dispatcher.logging.format.LogFormatterFactory;
import com.atexpose.dispatcher.logging.writer.ILogWriter;
import com.atexpose.dispatcher.logging.writer.LogWriterFactory;
import io.schinzel.basicutils.crypto.cipher.ICipher;
import io.schinzel.basicutils.crypto.cipher.NoCipher;
import io.schinzel.basicutils.state.IStateNode;
import io.schinzel.basicutils.state.State;
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
public class Logger implements IStateNode {
    /** The type of the logger. E.g. error or event */
    @Builder.Default
    private LoggerType mLoggerType = LoggerType.EVENT;
    /** Handles the writing of log entries. */
    @Builder.Default
    private ILogWriter mLogWriter = LogWriterFactory.SYSTEM_OUT.create();
    /** Handles the formatting of log entries. */
    @Builder.Default
    private ILogFormatter mLogFormatter = LogFormatterFactory.JSON.create();
    /** Used to encrypt part of the log data. */
    @Builder.Default
    private ICipher mCipher = new NoCipher();


    /**
     * Format and write a log entry.
     *
     * @param logEntry The entry to add to log.
     */
    public void log(ILogEntry logEntry) {
        //If this is an event logger
        if (mLoggerType == LoggerType.EVENT
                //OR (this is an error logger AND this is an error)
                || (this.mLoggerType == LoggerType.ERROR && logEntry.isError())) {
            Map<String, String> logData = logEntry.getLogData(mCipher);
            String logEntryAsString = mLogFormatter.formatLogEntry(logData);
            mLogWriter.log(logEntryAsString);
        }
    }


    @Override
    public State getState() {
        return State.getBuilder()
                .add("Type", mLoggerType.name().toLowerCase())
                .add("Writer", mLogWriter.getClass().getSimpleName())
                .add("Formatter", mLogFormatter.getClass().getSimpleName())
                .build();
    }

}
