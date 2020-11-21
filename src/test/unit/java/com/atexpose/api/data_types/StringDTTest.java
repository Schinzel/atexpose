package com.atexpose.api.data_types;

import io.schinzel.basicutils.FunnyChars;
import org.apache.commons.lang3.StringUtils;
import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 *
 * @author Schinzel
 */
public class StringDTTest {



    @Test
    public void testSomeMethod() {
        StringDT string = new StringDT();
        assertFalse(string.verifyValue("null"));
        assertFalse(string.verifyValue(null));
        String longString = StringUtils.repeat(' ', StringDT.MAX_LENGTH + 1);
        assertFalse(string.verifyValue(longString));
        longString = StringUtils.repeat(' ', StringDT.MAX_LENGTH);
        assertTrue(string.verifyValue(longString));
        assertTrue(string.verifyValue("åäö"));
        for (FunnyChars charset : FunnyChars.values()) {
            assertTrue(string.verifyValue(charset.getString()));
        }
    }

}
