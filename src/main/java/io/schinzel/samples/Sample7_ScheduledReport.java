package io.schinzel.samples;

import com.atexpose.AtExpose;
import com.atexpose.dispatcher.IDispatcher;
import com.atexpose.dispatcher_factories.ScheduledReportFactory;
import com.atexpose.util.mail.IEmailSender;
import com.atexpose.util.mail.MockMailSender;

/**
 * <p></p>
 * The purpose of this sample is to show how scheduled reports are set up
 * </p>
 * <p>
 * Note, works only with GMail SMTP server.
 * </p>
 * <p>
 * To get this sample to work you need to:
 * </p>
 * <pre>
 * 1) Set your GMail username and password. The email will be sent using this account.
 * 2) Set recipient email address
 * 3) Set the time to send to send the report in UTC, a min or two from now*.
 * </pre>
 * <p>
 * If you are unsure about the time in UTC, just start this sample and write "time" in the
 * terminal to get the current time in UTC.
 * </p>
 */
class Sample7_ScheduledReport {

    public static void main(String[] args) {
        AtExpose.create()
                .start(getScheduledReport())
                .startCLI();
    }


    private static IDispatcher getScheduledReport() {
        IEmailSender emailSender = new MockMailSender();
        /*
         Replace the mock email sender to send an actual email
         IEmailSender emailSender = new GmailEmailSender("name@example.com", "my_gmail_password");
        */
        return ScheduledReportFactory.builder()
                .taskName("MyTask")
                .request("ping")
                .timeOfDay("07:45")
                .recipient("person@example.com")
                .fromName("Me")
                .emailSender(emailSender)
                .build();
    }

}
