package com.atexpose.util.mail;

import static org.junit.Assert.*;

import org.junit.Test;

/**
 * @author Schinzel
 */
public class MockMailSenderTest {

    @Test
    public void testSendMail() {
        MockMailSender mms = new MockMailSender()
                .setRecipientEmailAddress("theRecipient");
        assertTrue(mms.mRecipientEmailAddress.equalsIgnoreCase("theRecipient"));
    }

}
