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
        MailLogSender logger = new MailLogSender(recipient, sender);
        logger.sendMail(sender, "This is the message");
        assertTrue(sender.getRecipient().contains("monkey@example.com"));
    }
}
