package io.schinzel.samples.sample_email_report;

import com.atexpose.AtExpose;

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
 * * = If you are unsure about the time in UTC, just start this sample and write "time" in the terminal to get
 * the current time in UTC.
 */
class Main {

    public static void main(String[] args) {
        AtExpose.create()
                //Set your Gmail username and password
                .setSMTPServerGmail("myusername@example.com", "mypassword")
                //Set the the time to send in UTC a
                .addScheduledReport("MyTask", "ping", "10:24", "recipient@example.com", "Me")
                //Start command line interface
                .startCLI();
    }
}
