package com.atexpose.dispatcher.logging.format;

/**
 * The purpose of this class is create log formatters.
 *
 * @author schinzel
 */
public enum LogFormatterFactory {
    /** Formats the log entry as JSON. **/
    JSON(JsonFormatter.class),
    /** Formats the log entry as a easier to read for humans multi-line entry. **/
    MULTI_LINE(MultiLineFormatter.class),
    /** Formats the log entry as a concise one line log entry. **/
    SINGLE_LINE(SingleLineFormatter.class);

    private final Class mClass;


    LogFormatterFactory(Class theClass) {
        mClass = theClass;
    }


    public ILogFormatter getInstance() {
        try {
            return (ILogFormatter) mClass.newInstance();
        } catch (InstantiationException | IllegalAccessException ex) {
            throw new RuntimeException(ex.getMessage());
        }
    }


    public static LogFormatterFactory get(String className) {
        for (LogFormatterFactory logFormatter : LogFormatterFactory.values()) {
            String currentFormaterClassName = logFormatter.mClass.getSimpleName();
            if (currentFormaterClassName.equalsIgnoreCase(className)) {
                return logFormatter;
            }
        }
        throw new RuntimeException("Sorry. No LogFormatter named '" + className + "' exists.");
    }
}
