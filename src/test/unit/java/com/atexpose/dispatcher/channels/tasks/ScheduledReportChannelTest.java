package com.atexpose.dispatcher.channels.tasks;

import com.atexpose.util.mail.GmailEmailSender;
import com.atexpose.util.mail.IEmailSender;
import com.icegreen.greenmail.util.DummySSLSocketFactory;
import com.icegreen.greenmail.util.GreenMail;
import com.icegreen.greenmail.util.GreenMailUtil;
import com.icegreen.greenmail.util.ServerSetupTest;
import io.schinzel.basicutils.UTF8;
import org.json.JSONObject;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.security.Security;

import static org.junit.Assert.assertEquals;

/**
 * @author Schinzel
 */
public class ScheduledReportChannelTest {
    IEmailSender mMailSender = new GmailEmailSender("noreply@example.com", "myPsw");

    @Rule
    public ExpectedException exception = ExpectedException.none();


    @Test
    public void testWriteResponse() throws NoSuchFieldException, MessagingException {
        //Set up dummy cert so that the gmail sender will trust greenmail recipient
        Security.setProperty("ssl.SocketFactory.provider", DummySSLSocketFactory.class.getName());
        //Start om the smtp test port, 3465. 
        //3465 is used as normal smtp port 465 cannot be used by non root users
        GreenMail greenMail = new GreenMail(ServerSetupTest.SMTPS);
        //Start green mail server
        greenMail.start();
        //Set server to be localhost and send port to test port (as can not use 465 as non root)
        GmailEmailSender es = new GmailEmailSender("user@example.com", "", "localhost", ServerSetupTest.SMTPS.getPort());
        String taskName = "TaskName";
        String request = "echo hej";
        String timeOfDay = "14:50";
        String recipient = "theFirstMonkey@monkey.se";
        ScheduledReportChannel src = ScheduledReportChannel.builder()
                .emailSender(es)
                .taskName(taskName)
                .request(request)
                .timeOfDay(timeOfDay)
                .recipient(recipient)
                .fromName("Skynet")
                .build();
        String sResponse = "The response";
        byte[] abResponse = UTF8.getBytes(sResponse);
        src.writeResponse(abResponse);
        //
        //Get message 
        MimeMessage message = greenMail.getReceivedMessages()[0];
        //Test body
        assertEquals(sResponse,
                GreenMailUtil.getBody(greenMail.getReceivedMessages()[0]));
        //Test to
        assertEquals(recipient, message.getRecipients(Message.RecipientType.TO)[0].toString());
        //Test reply to
        String from = "Skynet <user@example.com>";
        assertEquals(from, message.getReplyTo()[0].toString());
        greenMail.stop();

    }


    @Test
    public void testConstructor() {
        String taskName = "TaskName";
        String request = "echo hej";
        String timeOfDay = "14:50";
        String recipient = "theFirstMonkey@monkey.se";
        ScheduledReportChannel src = ScheduledReportChannel.builder()
                .emailSender(mMailSender)
                .taskName(taskName)
                .request(request)
                .timeOfDay(timeOfDay)
                .recipient(recipient)
                .fromName("fromName")
                .build();
        assertEquals(taskName, src.mTaskName);
        assertEquals(request, src.getTaskRequest());
        assertEquals(mMailSender.getClass().getSimpleName(), src.mEmailSender.getClass().getSimpleName());
        assertEquals(recipient, src.mRecipient);
    }


    @Test
    public void test_senderInfo() {
        String taskName = "TaskName";
        String request = "echo hej";
        String timeOfDay = "14:50";
        String recipient = "theFirstMonkey@monkey.se";
        ScheduledReportChannel src = ScheduledReportChannel.builder()
                .emailSender(mMailSender)
                .taskName(taskName)
                .request(request)
                .timeOfDay(timeOfDay)
                .recipient(recipient)
                .fromName("fromName")
                .build();
        assertEquals("ScheduledReport: " + taskName, src.senderInfo());
    }


    @Test
    public void test_getStatusAsJson() {
        String taskName = "theTaskName";
        String request = "echo hej";
        String timeOfDay = "14:50";
        String recipient1 = "theFirstMonkey@monkey.se";
        ScheduledReportChannel src = ScheduledReportChannel.builder()
                .emailSender(mMailSender)
                .taskName(taskName)
                .request(request)
                .timeOfDay(timeOfDay)
                .recipient(recipient1)
                .fromName("fromName")
                .build();
        JSONObject jo = src.getState().getJson();
        assertEquals(taskName, jo.getString("task_name"));
        assertEquals(request, jo.getString("request"));
        assertEquals(timeOfDay, jo.getString("time_of_day"));
        //
        String mailSenderName = jo.getJSONObject("MailSender").getString("name");
        assertEquals(mMailSender.getClass().getSimpleName(), mailSenderName);
        //
        assertEquals(recipient1, jo.getString("Recipient"));
    }

}
