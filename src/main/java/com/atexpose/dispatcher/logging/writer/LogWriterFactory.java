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
     * @return The log writer factory for the argument class.
     */
    public static LogWriterFactory get(String className) {
        for (LogWriterFactory logWriter : LogWriterFactory.values()) {
            if (logWriter.mClass.getSimpleName().equalsIgnoreCase(className)) {
                return logWriter;
            }
        }
        throw new RuntimeException("Sorry. No LogWriter named '" + className + "' exists.");
    }

}
