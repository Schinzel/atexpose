package com.atexpose.errors;

import org.junit.Test;
import org.junit.Rule;
import org.junit.rules.ExpectedException;

/**
 *
 * @author schinzel
 */
public class SetUpErrorTest {

    @Rule
    public ExpectedException exception = ExpectedException.none();


    @Test
    public void testSetUpError() {
        exception.expect(SetUpError.class);
        exception.expectMessage("my error message");
        throw new SetUpError("my error message", false);
    }
    
}
