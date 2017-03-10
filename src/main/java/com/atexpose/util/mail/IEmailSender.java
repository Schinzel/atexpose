package com.atexpose.util.mail;

import io.schinzel.basicutils.state.IStateNode;

/**
 * An email sender that sends emails.
 *
 * @author Schinzel
 */
public interface IEmailSender extends IStateNode {

    /**
     * /**
     * Sends an email to the argument recipients.
     *
     * @param recipient The recipient that will receive the email.
     * @param subject   The subject of the email.
     * @param body      The body of the email.
     * @param fromName  The name in the from-field in the email.
     */
    void send(String recipient, String subject, String body, String fromName);


}
