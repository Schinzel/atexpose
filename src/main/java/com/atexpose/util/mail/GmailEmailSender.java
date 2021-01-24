package com.atexpose.util.mail;

import com.atexpose.errors.RuntimeError;
import io.schinzel.basicutils.state.State;
import io.schinzel.basicutils.thrower.Thrower;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.apache.commons.mail.DefaultAuthenticator;
import org.apache.commons.mail.Email;
import org.apache.commons.mail.EmailException;
import org.apache.commons.mail.SimpleEmail;
import org.apache.commons.validator.routines.EmailValidator;

/**
 * The purpose of this class is to send emails from a Gmail account.
 *
 * @author Schinzel
 */
@Accessors(prefix = "m", chain = true)
public class GmailEmailSender implements IEmailSender {
    /** The name of the SMTP server that messages will be sent to */
    private final String mHostName;
    /** The port that messages will be sent to.* */
    private final int mPort;
    /** The username of the Gmail account that will send the mail. **/
    final String mUsername;
    /** The password of the Gmail account that will send the mail. **/
    final String mPassword;
    private boolean mSSLCheckServerIdentity = true;
    /** Recipient email address */
    @Setter private String mRecipientEmailAddress;
    /** Subject of the mail */
    @Setter private String mSubject;
    /** Body of the mail */
    @Setter private String mBody;
    /** The name that the email will be from*/
    @Setter private String mFromName;

    /**
     * @param userName The username of the Gmail account that will send the mail.
     * @param password The password of the Gmail account that will send the mail.
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


    public GmailEmailSender disableSSLCheckServerIdentityForTest() {
        mSSLCheckServerIdentity = false;
        return this;
    }


    @Override
    public GmailEmailSender send() {
        try {
            Thrower.createInstance()
                    .throwIfVarNull(mRecipientEmailAddress, "Recipient email address")
                    .throwIfVarNull(mSubject, "Subject")
                    .throwIfVarNull(mBody, "Body")
                    .throwIfVarNull(mFromName, "From Name");
            Email email = new SimpleEmail();
            email.setHostName(mHostName);
            email.setSslSmtpPort(String.valueOf(mPort));
            email.setAuthenticator(new DefaultAuthenticator(mUsername, mPassword));
            email.setSSLOnConnect(true);
            email.setSSLCheckServerIdentity(mSSLCheckServerIdentity);
            email.setFrom(mUsername, mFromName);
            //Per mail
            email.setSubject(mSubject);
            email.setMsg(mBody);
            if (!EmailValidator.getInstance().isValid(mRecipientEmailAddress)) {
                throw new RuntimeError(String.format("'%s' is not a valid email address.", mRecipientEmailAddress));
            }
            email.addTo(mRecipientEmailAddress);
            email.send();
            return this;
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