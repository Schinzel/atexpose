package com.atexpose.dispatcher.logging.writer;

import com.atexpose.util.mail.IEmailSender;

/**
 * The purpose of this class is to log data in mails.
 *
 * @author jorgen
 */
public class MailLogSender implements ILogWriter {
    private static final String MAIL_SUBJECT = "Expose error report";
    private final String mRecipient;
    private final IEmailSender mMailSender;


    MailLogSender(String recipient, IEmailSender emailSender) {
        mRecipient = recipient;
        mMailSender = emailSender;
    }


    @Override
    public void log(String logEntry) {
        sendMail(mMailSender, logEntry);
    }


    /**
     * Sends a log as a mail.
     *
     * @param sender   Object that does the actual sending
     * @param logEntry The entry to add to log
     */
    void sendMail(IEmailSender sender, String logEntry) {
        sender.send(mRecipient, MAIL_SUBJECT, logEntry, "@Expose Log");
    }


}
