package com.atexpose.errors;

import org.junit.Test;
import org.junit.Rule;
import org.junit.rules.ExpectedException;

/**
 *
 * @author schinzel
 */
public class FatalErrorTest {

    @Rule
    public ExpectedException exception = ExpectedException.none();


    @Test
    public void testFatalError() {
        exception.expect(FatalError.class);
        exception.expectMessage("my error message");
        throw new FatalError("my error message", false);
    }

}
