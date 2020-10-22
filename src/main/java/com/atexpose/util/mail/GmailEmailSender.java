package com.atexpose.util.mail;

import com.atexpose.errors.RuntimeError;
import io.schinzel.basicutils.state.State;
import lombok.experimental.Accessors;
import org.apache.commons.mail.DefaultAuthenticator;
import org.apache.commons.mail.Email;
import org.apache.commons.mail.EmailException;
import org.apache.commons.mail.SimpleEmail;
import org.apache.commons.validator.routines.EmailValidator;

/**
 * The purpose of this class is to send emails from a GMail account.
 *
 * @author Schinzel
 */
@Accessors(prefix = "m")
public class GmailEmailSender implements IEmailSender {
    /** The name of the SMTP server that messages will be sent to */
    private final String mHostName;
    /** The port that messages will be sent to.* */
    private final int mPort;
    /** The username of the GMail account that will send the mail. **/
    final String mUsername;
    /** The password of the GMail account that will send the mail. **/
    final String mPassword;
    private boolean mSSLCheckServerIdentity = true;


    /**
     * @param userName The username of the GMail account that will send the mail.
     * @param password The password of the GMail account that will send the mail.
     */
    public GmailEmailSender(String userName, String password) {
        this(userName, password, "smtp.googlemail.com", 465);

    }


    public GmailEmailSender(String userName, String password, String hostName, int port) {
        mHostName = hostName;
        mPort = port;
        mUsername = userName;
        mPassword = password;
    }


    public GmailEmailSender disableSSLCheckServerIdentityForTest(){
        mSSLCheckServerIdentity = false;
        return this;
    }


    /**
     * Sends an email to the argument recipients.
     *
     * @param recipient The recipient that will receive the email.
     * @param subject   The subject of the email.
     * @param body      The body of the email.
     */
    @Override
    public void send(String recipient, String subject, String body, String fromName) {
        try {
            Email email = new SimpleEmail();
            email.setHostName(mHostName);
            email.setSslSmtpPort(String.valueOf(mPort));
            email.setAuthenticator(new DefaultAuthenticator(mUsername, mPassword));
            email.setSSLOnConnect(true);
            email.setSSLCheckServerIdentity(mSSLCheckServerIdentity);
            email.setFrom(mUsername, fromName);
            //Per mail
            email.setSubject(subject);
            email.setMsg(body);
            if (!EmailValidator.getInstance().isValid(recipient)) {
                throw new RuntimeError(String.format("'%s' is not a valid email address.", recipient));
            }
            email.addTo(recipient);
            email.send();
        } catch (EmailException ex) {
            throw new RuntimeError("Problems sending mail. Error message: " + ex.getMessage());
        }
    }


    @Override
    public State getState() {
        return State.getBuilder()
                .add("name", this.getClass().getSimpleName())
                .add("host", mHostName)
                .add("port", mPort)
                .add("username", mUsername)
                .build();
    }

}


