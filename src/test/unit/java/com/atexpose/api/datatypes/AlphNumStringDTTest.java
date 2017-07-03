package com.atexpose.api.datatypes;

import org.apache.commons.lang3.StringUtils;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import org.junit.Test;

/**
 *
 * @author Schinzel
 */
public class AlphNumStringDTTest {
    

    @Test
    public void testVerifyValue() {
       AlphNumStringDT string = new AlphNumStringDT();
        assertFalse(string.verifyValue("null"));
        assertFalse(string.verifyValue(null));
        String longString = StringUtils.repeat('a', StringDT.MAX_LENGTH + 1);
        assertFalse(string.verifyValue(longString));
        longString = StringUtils.repeat('a', StringDT.MAX_LENGTH);
        assertTrue(string.verifyValue(longString));
        assertFalse(string.verifyValue("åäö"));
    }
    
}
