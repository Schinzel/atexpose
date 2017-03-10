package com.atexpose.errors;

/**
 * Runtime errors that does that are handled gracefully.
 *
 * @author Schinzel
 */
public class RuntimeError extends RuntimeException {
    public RuntimeError(String error) {
        super(error);
    }

}
