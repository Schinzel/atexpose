package com.atexpose.dispatcher.logging;

import com.atexpose.dispatcher.logging.format.ILogFormatter;
import com.atexpose.dispatcher.logging.format.LogFormatterFactory;
import com.atexpose.dispatcher.logging.writer.ILogWriter;
import com.atexpose.dispatcher.logging.writer.LogWriterFactory;
import com.atexpose.dispatcher.logging.crypto.Crypto;
import com.atexpose.dispatcher.logging.crypto.ICrypto;
import com.atexpose.dispatcher.logging.crypto.NoCrypto;

/**
 * The purpose of this class is to build loggers.
 *
 * @author Schinzel
 */
public class LoggerBuilder {
    private LoggerType mLoggerType = LoggerType.EVENT;
    private LogFormatterFactory mLogFormatFactory = LogFormatterFactory.Json;
    private LogWriterFactory mLogWriterFactory = LogWriterFactory.SystemOutLogger;
    private ICrypto mCrypto = new NoCrypto();


    public static LoggerBuilder getBuilder() {
        return new LoggerBuilder();
    }


    private LoggerBuilder() {
    }


    public LoggerBuilder setLoggerType(LoggerType loggerType) {
        this.mLoggerType = loggerType;
        return this;
    }


    public LoggerBuilder setLogFormatter(LogFormatterFactory logFormat) {
        this.mLogFormatFactory = logFormat;
        return this;
    }


    public LoggerBuilder setLogWriter(LogWriterFactory logWriter) {
        this.mLogWriterFactory = logWriter;
        return this;
    }


    public LoggerBuilder setCryptoKey(String cryptoKey) {
        this.mCrypto = Crypto.getInstance(cryptoKey);
        return this;
    }


    public Logger createLogger(String DispatcherName) {
        //Set the log format to use
        ILogFormatter logFormat = mLogFormatFactory.getInstance();
        //Set the log writer to use
        ILogWriter logWriter = mLogWriterFactory.getInstance();
        logWriter.setUp(DispatcherName, mLoggerType);
        //Create the logger
        Logger logger = new Logger(mLoggerType, logFormat, logWriter, mCrypto);
        //Reset the builder so that it is not reused in another dispatcher. 
        mLoggerType = null;
        mLogWriterFactory = null;
        mLogFormatFactory = null;
        mCrypto = null;
        return logger;
    }

}
