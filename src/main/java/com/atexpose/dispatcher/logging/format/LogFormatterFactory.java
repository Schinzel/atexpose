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


    /**
     *
     * @return A new log formatter instance.
     */
    public ILogFormatter create() {
        try {
            return (ILogFormatter) mClass.newInstance();
        } catch (InstantiationException | IllegalAccessException ex) {
            throw new RuntimeException(ex.getMessage());
        }
    }


    /**
     * @param formatterName The name of the log formatter
     * @return The log writer formatter with the argument name.
     */
    public static LogFormatterFactory get(String formatterName) {
        for (LogFormatterFactory logFormatter : LogFormatterFactory.values()) {
            if (logFormatter.name().equalsIgnoreCase(formatterName)) {
                return logFormatter;
            }
        }
        throw new RuntimeException("Sorry. No LogFormatter named '" + formatterName + "' exists.");
    }
}
