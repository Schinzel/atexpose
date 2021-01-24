package com.atexpose.util.mail;

import com.icegreen.greenmail.util.DummySSLSocketFactory;
import com.icegreen.greenmail.util.GreenMail;
import com.icegreen.greenmail.util.GreenMailUtil;
import com.icegreen.greenmail.util.ServerSetupTest;
import io.schinzel.basicutils.RandomUtil;
import org.json.JSONObject;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.security.Security;

import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.junit.Assert.assertEquals;


/**
 * @author Schinzel
 */
public class GmailEmailSenderTest {
    GreenMail mGreenMail;


    @Before
    public void before() {
        //Set up dummy cert so that the gmail sender will trust greenmail recipient
        Security.setProperty("ssl.SocketFactory.provider", DummySSLSocketFactory.class.getName());
        //Start om the smtp test port, 3465. 
        //3465 is used as normal smtp port 465 cannot be used by non root users
        mGreenMail = new GreenMail(ServerSetupTest.SMTPS);
        //Start green mail server
        mGreenMail.start();
    }


    @After
    public void after() {
        mGreenMail.stop();
    }


    @Test
    public void testConstructor() {
        GmailEmailSender gms = new GmailEmailSender("theUsername", "thePassword");
        assertEquals("theUsername", gms.mUsername);
        assertEquals("thePassword", gms.mPassword);
    }


    @Test
    public void testSend() throws MessagingException {
        String subject = "theSubject" + RandomUtil.getRandomString(3);
        String body = "theBody" + RandomUtil.getRandomString(3);
        String userName = RandomUtil.getRandomString(3) + "@domain.com";
        String fromName = "theFromName" + RandomUtil.getRandomString(3);
        String recipient = RandomUtil.getRandomString(3) + "@monkey.com";
        new GmailEmailSender(userName, "thePassword",
                "localhost", ServerSetupTest.SMTPS.getPort())
                .disableSSLCheckServerIdentityForTest()
                .setRecipientEmailAddress(recipient)
                .setSubject(subject)
                .setBody(body)
                .setFromName(fromName)
                .send();
        //Get message
        MimeMessage message = mGreenMail.getReceivedMessages()[0];
        //Test body
        assertEquals(body,
                GreenMailUtil.getBody(mGreenMail.getReceivedMessages()[0]));
        //Test subject
        assertEquals(subject, message.getSubject());
        //Test to
        assertEquals(recipient, message.getRecipients(Message.RecipientType.TO)[0].toString());
        //Test reply to
        String from = fromName + " <" + userName + ">";
        assertEquals(from, message.getReplyTo()[0].toString());
    }


    @Test
    public void testSendingToInvalidEmailAddress() {
        String subject = "theSubject" + RandomUtil.getRandomString(3);
        String body = "theBody" + RandomUtil.getRandomString(3);
        String userName = RandomUtil.getRandomString(3) + "@domain.com";
        GmailEmailSender gms = new GmailEmailSender(userName, "thePassword",
                "localhost", ServerSetupTest.SMTPS.getPort())
                .setRecipientEmailAddress("I_AM_AN_INVALID_EMAIL_ADDRESS")
                .setSubject(subject)
                .setBody(body)
                .setFromName("fromName");
        assertThatExceptionOfType(RuntimeException.class)
                .isThrownBy(gms::send)
                .withMessageStartingWith("'I_AM_AN_INVALID_EMAIL_ADDRESS' is not a valid");
    }


    @Test
    public void test_getStatusAsJson() {
        //Send mail
        GmailEmailSender gms = new GmailEmailSender("theUsername", "thePassword");
        JSONObject jo = gms.getState().getJson();
        assertEquals("GmailEmailSender", jo.getString("name"));
        assertEquals("smtp.googlemail.com", jo.getString("host"));
        assertEquals(465, jo.getInt("port"));
        assertEquals("theUsername", jo.getString("username"));
    }
}
