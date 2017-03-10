package com.atexpose.util.mail;

import io.schinzel.basicutils.state.State;
import lombok.Getter;
import lombok.experimental.Accessors;

/**
 * Moved from EmailSenderTest to be used on more places
 *
 * @author jorgen
 */
@Accessors(prefix = "m")
public class MockMailSender implements IEmailSender {
    @Getter String mRecipient;


    @Override
    public void send(String recipient, String subject, String body, String fromName) {
        mRecipient = recipient;
    }


    @Override
    public State getState() {
        return State.getBuilder()
                .add("name", this.getClass().getSimpleName())
                .build();
    }


}
