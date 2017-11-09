package com.atexpose.errors;

import io.schinzel.basicutils.str.Str;

/**
 * Fatal errors are thrown when system goes into a bad state that can not be
 * recuperated from. Shuts down the system.
 *
 * @author Schinzel
 */
public class FatalError extends RuntimeException {
    /**
     * @param error The error message.
     */
    @SuppressWarnings("unused")
    public FatalError(String error) {
        this(error, true);
    }


    /**
     * Exists for testing so that can test without the system
     * shutdown killing the tests.
     *
     * @param error          The error message.
     * @param systemShutdown If true, system is shutdown, else not.
     */
    FatalError(String error, boolean systemShutdown) {
        super(error);
        String stackTraceTop = this.getStackTrace()[0].toString();
        String errorMessage = Str.create()
                .a("Fatal error: ").anl(error)
                .a("Error Source: ").anl(stackTraceTop)
                .anl("System in a bad state that cannot be recuperated from. Shutting down...")
                .toString();
        System.err.print(errorMessage);
        if (systemShutdown) {
            System.exit(1);
        }
    }

}
