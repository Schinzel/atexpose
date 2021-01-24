package com.atexpose.dispatcher.logging.writer;

import com.atexpose.util.mail.MockMailSender;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

/**
 * @author jorgen
 */
public class MailLoggerTest {


    @Test
    public void testSend() {
        String recipient = "monkey@example.com";
        MockMailSender sender = new MockMailSender();
        MailLogWriter logger = new MailLogWriter(recipient, sender);
        logger.sendMail(sender, "This is the message");
        assertTrue(sender.getRecipientEmailAddress().contains("monkey@example.com"));
    }
}
