package com.atexpose.dispatcher_factories;

import com.atexpose.dispatcher.Dispatcher;
import com.atexpose.dispatcher.IDispatcher;
import com.atexpose.dispatcher.channels.tasks.ScheduledReportChannel;
import com.atexpose.dispatcher.logging.Logger;
import com.atexpose.dispatcher.logging.LoggerType;
import com.atexpose.dispatcher.logging.format.LogFormatterFactory;
import com.atexpose.dispatcher.logging.writer.LogWriterFactory;
import com.atexpose.dispatcher.parser.text_parser.TextParser;
import com.atexpose.dispatcher.wrapper.CsvWrapper;
import com.atexpose.util.mail.IEmailSender;
import lombok.Builder;

public class ScheduledReportFactory {

    ScheduledReportFactory() {
    }


    /**
     * Sets up a scheduled report. This is a scheduled task where the result of the operation is
     * sent to the argument
     * recipient.
     *
     * @param taskName    The name of the report.
     * @param request     The request to execute. Example: "echo hi"
     * @param timeOfDay   The time of day in the argument time zone to run the report. Examples:
     *                    "13:05" "07:55"
     * @param timeZone    The time zone. E.g. "UTC", "Europe/Stockholm"
     * @param recipient   The recipient email address.
     * @param fromName    The name in the from field in the email
     * @param emailSender Handles sending of emails
     * @return Dispatcher
     */
    @Builder
    static IDispatcher newScheduledReport(String taskName, String request, String timeOfDay,
                                          String timeZone, String recipient, String fromName,
                                          IEmailSender emailSender) {
        ScheduledReportChannel scheduledReport = ScheduledReportChannel.builder()
                .emailSender(emailSender)
                .recipient(recipient)
                .request(request)
                .taskName(taskName)
                .timeOfDay(timeOfDay)
                .zoneId(timeZone)
                .fromName(fromName)
                .build();
        Logger eventLogger = Logger.builder()
                .loggerType(LoggerType.EVENT)
                .logFormatter(LogFormatterFactory.JSON.create())
                .logWriter(LogWriterFactory.SYSTEM_OUT.create())
                .build();
        return Dispatcher.builder()
                .name("ScheduledReport_" + taskName)
                .accessLevel(3)
                .isSynchronized(false)
                .channel(scheduledReport)
                .parser(new TextParser())
                .wrapper(new CsvWrapper())
                .noOfThreads(1)
                .build()
                .addLogger(eventLogger);

    }
}
