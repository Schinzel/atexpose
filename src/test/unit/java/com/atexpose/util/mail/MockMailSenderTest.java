package com.atexpose.util.mail;

import static org.junit.Assert.*;

import org.junit.Test;

/**
 * @author Schinzel
 */
public class MockMailSenderTest {

    @Test
    public void testSendMail() {
        MockMailSender mms = new MockMailSender();
        mms.send("theRecipient", null, null, "fromName");
        assertTrue(mms.mRecipient.equalsIgnoreCase("theRecipient"));
    }

}
