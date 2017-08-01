package com.atexpose.atexpose;

import com.atexpose.dispatcher.Dispatcher;
import com.atexpose.dispatcher.channels.tasks.ScheduledReportChannel;
import com.atexpose.dispatcher.logging.Logger;
import com.atexpose.dispatcher.logging.LoggerType;
import com.atexpose.dispatcher.logging.format.LogFormatterFactory;
import com.atexpose.dispatcher.logging.writer.LogWriterFactory;
import com.atexpose.dispatcher.parser.IParser;
import com.atexpose.dispatcher.parser.Request;
import com.atexpose.dispatcher.parser.TextParser;
import com.atexpose.dispatcher.wrapper.CsvWrapper;
import com.atexpose.util.mail.GmailEmailSender;
import com.atexpose.util.mail.IEmailSender;
import com.atexpose.util.mail.MockMailSender;
import io.schinzel.basicutils.Thrower;

import java.time.ZoneId;

/**
 * The purpose of this interface is to set up scheduled reports.
 * <p>
 * A scheduled report is a scheduled task where the result of the operation is sent as an email.
 * <p>
 * Created by schinzel on 2017-04-16.
 */
public interface IAtExposeReports<T extends IAtExpose<T>> extends IAtExpose<T> {

    T setMailSender(IEmailSender emailSender);


    IEmailSender getMailSender();


    /**
     * Sets the Gmail SMTP server used for sending scheduled reports.
     *
     * @param username The Gmail username
     * @param password The Gmail password
     * @return This for chaining.
     */
    default T setSMTPServerGmail(String username, String password) {
        return this.setMailSender(new GmailEmailSender(username, password));
    }


    /**
     * Set a mock smtp server. For debugging and testing purposes.
     *
     * @return This for chaining.
     */
    default T setMockSMTPServer() {
        return this.setMailSender(new MockMailSender());
    }


    /**
     * Sets up a scheduled report. This is a scheduled task where the result of the operation is
     * sent to the argument
     * recipient.
     *
     * @param taskName   The name of the report.
     * @param rawRequest The request to execute. Example: "echo hi"
     * @param timeOfDay  The time of day to run the report. Examples: "13:05" "07:55"
     * @param zoneId     The time zone
     * @param recipient  The recipient email address.
     * @param fromName   The name in the from field in the email
     * @return This for chaining.
     */
    default T addScheduledReport(String taskName, String rawRequest, String timeOfDay, ZoneId zoneId, String recipient, String fromName) {
        Thrower.throwIfTrue(this.getMailSender() == null, "You need to set SMTP settings before setting up a scheduled report. Use method setSMTPServer.");
        IParser parser = new TextParser();
        Request request1 = parser.getRequest(rawRequest);
        String methodName = request1.getMethodName();
        if (!this.getAPI().methodExits(methodName)) {
            throw new RuntimeException("No such method '" + methodName + "'");
        }
        ScheduledReportChannel scheduledReport = ScheduledReportChannel.builder()
                .emailSender(this.getMailSender())
                .recipient(recipient)
                .request(rawRequest)
                .taskName(taskName)
                .timeOfDay(timeOfDay)
                .zoneId(zoneId)
                .fromName(fromName)
                .build();
        String dispatcherName = "ScheduledReport_" + taskName;
        Dispatcher dispatcher = Dispatcher.builder()
                .name(dispatcherName)
                .accessLevel(3)
                .channel(scheduledReport)
                .parser(parser)
                .wrapper(new CsvWrapper())
                .noOfThreads(1)
                .api(this.getAPI())
                .build();
        Logger eventLogger = Logger.builder()
                .loggerType(LoggerType.EVENT)
                .logFormatter(LogFormatterFactory.JSON.getInstance())
                .logWriter(LogWriterFactory.SYSTEM_OUT.getInstance())
                .build();
        dispatcher.addLogger(eventLogger);
        return this.startDispatcher(dispatcher, false, false);
    }

}
