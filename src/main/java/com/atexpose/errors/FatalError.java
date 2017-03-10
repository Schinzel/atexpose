package com.atexpose.errors;

import com.atexpose.MyProperties;

/**
 * Fatal errors are thrown when system goes into a bad state that can not be
 * recuperated from. Shuts down the system.
 *
 * @author Schinzel
 */
public class FatalError extends RuntimeException {
    /**
     * 
     * @param error The error message. 
     */
    public FatalError(String error) {
        this(error, true);
    }


    /**
     * Exists for testing so that can test without the system
     * shutdown killing the tests.
     *
     * @param error The error message. 
     * @param systemShutdown If true, system is shutdown, else not.
     */
    FatalError(String error, boolean systemShutdown) {
        super(error);
        String stackTraceTop = this.getStackTrace()[0].toString();
        StringBuilder sb = new StringBuilder();
        sb.append("Fatal error: ").append(error).append(MyProperties.OS_LINE_SEPARATOR)
                .append("Error Source: ").append(stackTraceTop).append(MyProperties.OS_LINE_SEPARATOR)
                .append("System in a bad state that cannot be recuperated from. Shutting down...")
                .append(MyProperties.OS_LINE_SEPARATOR);
        System.err.print(sb.toString());
        if (systemShutdown) {
            System.exit(1);
        }
    }

}
