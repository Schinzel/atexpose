package com.atexpose.dispatcher.channels;

import com.atexpose.util.mail.IEmailSender;
import io.schinzel.basicutils.UTF8;
import io.schinzel.basicutils.state.State;
import lombok.Builder;
import lombok.NonNull;

/**
 * The purpose of this class is to send reports as emails at a scheduled
 * interval.
 *
 * @author Schinzel
 */
public class ScheduledReportChannel extends ScheduledTaskChannel {
    /** The object that does the sending of emails. **/
    final IEmailSender mEmailSender;
    /** The recipient of the report **/
    final String mRecipient;
    /** The name in the from field in the email **/
    private final String mFromName;


    @Builder
    private ScheduledReportChannel(@NonNull String taskName, @NonNull String request, @NonNull String timeOfDay,
                                  @NonNull IEmailSender emailSender, @NonNull String recipient, @NonNull String fromName) {
        super(taskName, request, timeOfDay);
        mEmailSender = emailSender;
        mRecipient = recipient;
        mFromName = fromName;
    }


    @Override
    public void writeResponse(byte[] response) {
        String responseAsString = UTF8.getString(response);
        String subject = "Scheduled Report " + this.mTaskName;
        mEmailSender.send(mRecipient, subject, responseAsString, mFromName);
    }


    @Override
    public String senderInfo() {
        return "ScheduledReport: " + mTaskName;
    }


    @Override
    public State getState() {
        return State.getBuilder(super.getState())
                .add("Recipient", mRecipient)
                .addChild("MailSender", mEmailSender)
                .build();
    }

}
