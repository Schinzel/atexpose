package com.atexpose.errors;

import io.schinzel.basicutils.str.Str;

/**
 * Set up errors are thrown when set up and system initialization could not
 * be done and system needs to be shut down. Terminates the system. Sends statusAsJson
 * code 2.
 *
 * @author Schinzel
 */
public class SetUpError extends RuntimeException {
    /**
     * @param error The error message.
     */
    public SetUpError(String error) {
        this(error, true);
    }


    /**
     * Exists for testing so that can test without the system
     * shutdown killing the tests.
     *
     * @param error          The error message.
     * @param systemShutdown If true, system is shutdown, else not.
     */
    SetUpError(String error, boolean systemShutdown) {
        super(error);
        String stackTraceTop = this.getStackTrace()[0].toString();
        Str str = Str.create().a("Set up error: ").a(error).anl()
                .a("Error Source: ").a(stackTraceTop).anl()
                .a("System could not be set up properly. Shutting down...").anl();
        System.err.print(str.toString());
        if (systemShutdown) {
            System.exit(2);
        }
    }

}
