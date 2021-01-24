package com.atexpose.util.mail;

import io.schinzel.basicutils.state.IStateNode;

/**
 * An email sender that sends emails.
 *
 * @author Schinzel
 */
public interface IEmailSender extends IStateNode {

    /**
     * @param recipient The recipient that will receive the email
     * @return This for chaining
     */
    IEmailSender setRecipientEmailAddress(String recipient);


    /**
     * @param subject   The subject of the email
     * @return This for chaining
     */
    IEmailSender setSubject(String subject);


    /**
     * @param body      The body of the email
     * @return This for chaining
     */
    IEmailSender setBody(String body);


    /**
     * @param fromName  The name in the from-field in the email
     * @return This for chaining
     */
    IEmailSender setFromName(String fromName);


    /**
     * Send the email with the properties set with the setters.
     * @return This for chaining
     */
    IEmailSender send();

}
