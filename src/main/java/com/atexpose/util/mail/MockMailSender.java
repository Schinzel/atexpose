package com.atexpose.util.mail;

import io.schinzel.basicutils.state.State;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

/**
 * Moved from EmailSenderTest to be used on more places
 *
 * @author jorgen
 */
@Accessors(prefix = "m", chain = true)
public class MockMailSender implements IEmailSender {
    @Getter @Setter String mRecipientEmailAddress;


    @Override
    public State getState() {
        return State.getBuilder()
                .add("name", this.getClass().getSimpleName())
                .build();
    }


    @Override
    public MockMailSender setSubject(String subject) {
        return this;
    }

    @Override
    public MockMailSender setBody(String body) {
        return this;
    }

    @Override
    public MockMailSender setFromName(String fromName) {
        return this;
    }

    @Override
    public MockMailSender send() {
        return this;
    }
}
