package com.atexpose.errors;

import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

/**
 * @author schinzel
 */
public class FatalErrorTest {


    @Test
    public void testFatalError() {
        assertThatExceptionOfType(FatalError.class)
                .isThrownBy(() -> doThrow())
                .withMessageStartingWith("my error message");
    }


    private static void doThrow() {
        throw new FatalError("my error message", false);
    }
}
