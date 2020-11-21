package io.schinzel.samples;

import com.atexpose.AtExpose;
import com.atexpose.dispatcher.IDispatcher;
import com.atexpose.dispatcher_factories.CliFactory;
import com.atexpose.dispatcher_factories.ScheduledReportFactory;
import com.atexpose.util.mail.IEmailSender;
import com.atexpose.util.mail.MockMailSender;

/**
 * In this sample a scheduled report is set up. These are emailed once a day.
 * <p>
 * Note, works only with GMail SMTP server.
 * <p>
 * To get this sample to work you need to:
 * 1) Set your GMail username and password. The email will be sent using this account.
 * 2) Set recipient email address
 * 3) Set the time to send to send the report in UTC, a min or two from now*.
 * <p>
 * * = If you are unsure about the time in UTC, just start this sample and write "time" in the
 * terminal to get
 * the current time in UTC.
 */
class ScheduledReport {

    public static void main(String[] args) {
        AtExpose.create()
                .start(getScheduledReport())
                .start(CliFactory.create());
    }


    private static IDispatcher getScheduledReport() {
        IEmailSender emailSender = new MockMailSender();
        return ScheduledReportFactory.builder()
                .taskName("MyTask")
                .request("ping")
                .timeOfDay("10:24")
                .recipient("recipient@example.com")
                .fromName("Me")
                .emailSender(emailSender)
                .build();
    }

}
