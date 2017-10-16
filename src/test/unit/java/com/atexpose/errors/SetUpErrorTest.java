package com.atexpose.errors;

import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

/**
 * @author schinzel
 */
public class SetUpErrorTest {


    @Test
    public void testSetUpError() {
        assertThatExceptionOfType(SetUpError.class)
                .isThrownBy(() -> doThrow())
                .withMessageStartingWith("my error message");
    }


    private static void doThrow() {
        throw new SetUpError("my error message", false);
    }
}
