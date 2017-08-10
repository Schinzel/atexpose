package com.atexpose.dispatcher.logging.writer;

/**
 * The purpose of this enum is to create log writers.
 *
 * @author schinzel
 */
public enum LogWriterFactory {
    MAIL(MailLogWriter.class),
    SYSTEM_OUT(SystemOutLogWriter.class);

    private final Class mClass;


    LogWriterFactory(Class theClass) {
        mClass = theClass;
    }


    /**
     * @return A new log writer instance.
     */
    public ILogWriter getNewInstance() {
        try {
            return (ILogWriter) mClass.newInstance();
        } catch (InstantiationException | IllegalAccessException ex) {
            throw new RuntimeException(ex.getMessage());
        }
    }


    /**
     * @param logWriterName The name of the log writer
     * @return The log writer factory with the argument name.
     */
    public static LogWriterFactory get(String logWriterName) {
        for (LogWriterFactory logWriter : LogWriterFactory.values()) {
            if (logWriter.name().equalsIgnoreCase(logWriterName)) {
                return logWriter;
            }
        }
        throw new RuntimeException("Sorry. No LogWriter named '" + logWriterName + "' exists.");
    }
}
