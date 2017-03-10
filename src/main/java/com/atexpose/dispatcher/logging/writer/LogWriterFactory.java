package com.atexpose.dispatcher.logging.writer;

/**
 * The purpose of this enum is to create log writers.
 *
 * @author schinzel
 */
public enum LogWriterFactory {
    LogFile(LogFileWriter.class),
    MailLogger(MailLogSender.class),
    SystemOutLogger(SystemOutLogWriter.class);

    private final Class mClass;


    private LogWriterFactory(Class theClass) {
        mClass = theClass;
    }


    /**
     * 
     * @return A log writer instance. 
     */
    public ILogWriter getInstance() {
        try {
            return (ILogWriter) mClass.newInstance();
        } catch (InstantiationException | IllegalAccessException ex) {
            throw new RuntimeException(ex.getMessage());
        }
    }


    /**
     * 
     * @param className
     * @return 
     */
    public static LogWriterFactory create(String className) {
        for (LogWriterFactory logFormater : LogWriterFactory.values()) {
            if (logFormater.mClass.getSimpleName().equalsIgnoreCase(className)) {
                return logFormater;
            }
        }
        throw new RuntimeException("Sorry. No LogWriter named '" + className + "' exists.");
    }

}
