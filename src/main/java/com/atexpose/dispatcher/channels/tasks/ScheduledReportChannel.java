package com.atexpose.dispatcher.channels.tasks;

import com.atexpose.util.mail.IEmailSender;
import io.schinzel.basicutils.UTF8;
import io.schinzel.basicutils.state.State;
import lombok.Builder;

/**
 * The purpose of this class is to send reports as emails at a scheduled
 * interval.
 *
 * @author Schinzel
 */
public class ScheduledReportChannel extends ScheduledTaskChannelDaily {
    /** The object that does the sending of emails. **/
    final IEmailSender mEmailSender;
    /** The recipient of the report **/
    final String mRecipient;
    /** The name in the from field in the email **/
    private final String mFromName;


    @Builder
    private ScheduledReportChannel(String taskName, String request, String timeOfDay, String zoneId,
                                   IEmailSender emailSender, String recipient, String fromName) {
        super(taskName, request, timeOfDay, zoneId);
        mEmailSender = emailSender;
        mRecipient = recipient;
        mFromName = fromName;
    }


    @Override
    public void writeResponse(byte[] response) {
        String responseAsString = UTF8.getString(response);
        String subject = "Scheduled Report " + this.mTaskName;
        mEmailSender
                .setRecipientEmailAddress(mRecipient)
                .setSubject(subject)
                .setBody(responseAsString)
                .setFromName(mFromName)
                .send();
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
