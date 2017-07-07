package com.atexpose;

import com.atexpose.dispatcher.logging.Logger;
import com.atexpose.dispatcher.logging.LoggerType;
import com.atexpose.dispatcher.logging.format.LogFormatterFactory;
import com.atexpose.dispatcher.logging.writer.LogWriterFactory;
import io.schinzel.basicutils.Checker;
import io.schinzel.basicutils.crypto.cipher.Aes256Gcm;
import io.schinzel.basicutils.crypto.cipher.ICipher;
import io.schinzel.basicutils.crypto.cipher.NoCipher;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.Accessors;
import org.apache.commons.lang3.StringUtils;
import org.json.JSONObject;

/**
 * The purpose of this class is to expose @Expose and allow string returns that contain status of
 * the operation
 * messages.
 * <p>
 * Created by schinzel on 2017-04-16.
 */
@SuppressWarnings("unused")
@Accessors(prefix = "m")
@AllArgsConstructor(access = AccessLevel.PRIVATE)
class ExposedAtExpose {
    @Getter(AccessLevel.PACKAGE)
    private final AtExpose mAtExpose;


    static ExposedAtExpose create(AtExpose atExpose) {
        return new ExposedAtExpose(atExpose);
    }


    @Expose(
            arguments = {"FileName"},
            requiredAccessLevel = 3,
            requiredArgumentCount = 1,
            description = "Reads and executes the argument script file. Useful for setting up settings, scheduled tasks and so on.",
            labels = {"@Expose", "AtExpose"}
    )
    public String loadScriptFile(String fileName) {
        this.getAtExpose().loadScriptFile(fileName);
        return "Script file '" + fileName + "' loaded.";
    }


    @Expose(
            arguments = {"Port", "WebServerDir"},
            requiredAccessLevel = 3,
            description = "Starts a web server.",
            labels = {"@Expose", "AtExpose"}
    )
    public String startWebServer(int port, String dir) {
        this.getAtExpose().getWebServerBuilder()
                .port(port)
                .webServerDir(dir)
                .startWebServer();
        return "Web server started on port " + port;
    }


    @Expose(
            arguments = {"Username", "Password"},
            requiredAccessLevel = 3,
            requiredArgumentCount = 2,
            description = "Sets the SMTP server to user for outgoing mails.",
            labels = {"@Expose", "AtExpose"}
    )
    public String setSMTPServerGmail(String username, String password) {
        this.getAtExpose().setSMTPServerGmail(username, password);
        return "SMTP server settings set";
    }


    @Expose(
            requiredAccessLevel = 3,
            description = "Set usage of Mock SMTP server, i.e. do not send any real e-mail.",
            labels = {"@Expose", "AtExpose"}
    )
    public String setMockSMTPServer() {
        this.getAtExpose().setMockSMTPServer();
        return "Mock SMTP server settings is set";
    }


    @Expose(
            arguments = {"TaskName", "Request", "TimeOfDay", "Recipient", "FromName"},
            requiredAccessLevel = 3,
            requiredArgumentCount = 4,
            description = "Performs a task every day at the stated time of day. " +
                    "The time stated is in UTC. " +
                    "Scheduled reports are close relatives of scheduled tasks with the difference that the result of operations are sent as mail. " +
                    "Reports are given an event logger with default logger and format.",
            labels = {"@Expose", "AtExpose", "ScheduledTasks"}
    )
    public String addScheduledReport(String taskName, String request, String timeOfDay, String recipient, String fromName) {
        this.getAtExpose().addScheduledReport(taskName, request, timeOfDay, recipient, fromName);
        return "Scheduled report '" + taskName + "' has been set up";
    }


    @Expose(
            arguments = {"TaskName", "Request", "TimeOfDay"},
            requiredAccessLevel = 3,
            requiredArgumentCount = 3,
            description = "Performs a task every day at the stated time of day. " +
                    "The time stated is in UTC. " +
                    "Tasks are given an event logger with default logger and format. ",
            labels = {"@Expose", "AtExpose", "ScheduledTasks"}
    )
    public String addDailyTask(String taskName, String request, String timeOfDay) {
        this.getAtExpose().addDailyTask(taskName, request, timeOfDay);
        return "Daily task '" + taskName + "' set up";
    }


    @Expose(
            arguments = {"TaskName", "Request", "Minutes"},
            requiredAccessLevel = 3,
            requiredArgumentCount = 3,
            description = "Adds a scheduled a task that will run every stated number of minutes. " +
                    "The first time the task will run at the stated number minutes after the task was added. " +
                    "The after the first time, the task will run the stated number of minutes after the execution of the previous task was finished. " +
                    "Tasks are given an event logger with default logger and format.",
            labels = {"@Expose", "AtExpose", "ScheduledTasks"}
    )
    public String addTask(String taskName, String request, int minutes) {
        this.getAtExpose().addTask(taskName, request, minutes);
        return "Task that runs every '" + minutes + "' minutes set up";
    }


    @Expose(
            arguments = {"TaskName", "Request", "TimeOfDay", "DayOfMonth"},
            requiredAccessLevel = 3,
            description = "Adds a scheduled a task that will run monthly at the stated time of day at the stated day of month " +
                    "The time stated is in UTC. " +
                    "Tasks are given an event logger with default logger and format.",
            requiredArgumentCount = 4,
            labels = {"@Expose", "AtExpose", "ScheduledTasks"}
    )
    public String addMonthlyTask(String taskName, String request, String timeOfDay, int dayOfMonth) {
        this.getAtExpose().addMonthlyTask(taskName, request, timeOfDay, dayOfMonth);
        return "Montly task '" + taskName + "' set up";
    }


    @Expose(
            arguments = {"TaskName"},
            requiredAccessLevel = 3,
            requiredArgumentCount = 1,
            description = "Removes the argument scheduled task.",
            labels = {"@Expose", "AtExpose", "ScheduledTasks"}
    )
    public String removeTask(String taskName) {
        this.getAtExpose().removeTask(taskName);
        return "Task '" + taskName + "' was removed";
    }


    @Expose(
            arguments = {"DispatcherName", "LogWriter", "LogFormatter", "CryptoKey"},
            requiredAccessLevel = 3,
            description = "Adds an event logger to a dispatcher.",
            labels = {"@Expose", "AtExpose", "Logs"},
            requiredArgumentCount = 1
    )
    public String addEventLogger(String dispatcherName, String logFormatter, String logWriter, String cryptoKey) {
        return this.addLogger(dispatcherName, logFormatter, logWriter, cryptoKey, LoggerType.EVENT);
    }


    @Expose(
            arguments = {"DispatcherName", "LogWriter", "LogFormatter", "CryptoKey"},
            requiredAccessLevel = 3,
            description = "Adds an error logger to a dispatcher.",
            labels = {"@Expose", "AtExpose", "Logs"},
            requiredArgumentCount = 1
    )
    public String addErrorLogger(String dispatcherName, String logFormatter, String logWriter, String cryptoKey) {
        return this.addLogger(dispatcherName, logFormatter, logWriter, cryptoKey, LoggerType.ERROR);
    }


    String addLogger(String dispatcherName, String logFormatter, String logWriter, String cryptoKey, LoggerType loggerType) {
        ICipher crypto = Checker.isEmpty(cryptoKey)
                ? new NoCipher()
                : new Aes256Gcm(cryptoKey);
        Logger logger = Logger.builder()
                .loggerType(LoggerType.EVENT)
                .logFormatter(LogFormatterFactory.get(logFormatter).getInstance())
                .logWriter(LogWriterFactory.get(logWriter).getInstance())
                .cipher(crypto)
                .build();
        this.getAtExpose().getDispatchers().get(dispatcherName).addLogger(logger);
        return "Dispatcher " + dispatcherName + " got an " + loggerType.name().toLowerCase() + " logger";
    }


    @Expose(
            arguments = {"DispatcherName"},
            requiredAccessLevel = 3,
            description = "Removes all loggers from a dispatcher.",
            labels = {"@Expose", "AtExpose", "Logs"},
            requiredArgumentCount = 1
    )
    public String removeAllLoggers(String dispatcherName) {
        this.getAtExpose().removeAllLoggers(dispatcherName);
        return "Removed all loggers from dispatcher '" + dispatcherName + "'";
    }


    @Expose(
            description = "Shuts down the system",
            requiredAccessLevel = 3,
            aliases = {"close", "bye", "exit"},
            labels = {"@Expose"}
    )
    public synchronized String shutdown() {
        this.getAtExpose().shutdown();
        return "Shutting down...";
    }


    @Expose(
            arguments = {"DispatcherName"},
            requiredAccessLevel = 3,
            description = "Closes the argument dispatcher.",
            labels = {"@Expose"}
    )
    public String closeDispatcher(String name) {
        this.getAtExpose().closeDispatcher(name);
        return "Dispatcher " + name + " has been closed";
    }


    @Expose(
            description = "Returns the API.",
            requiredAccessLevel = 2,
            labels = {"@Expose"}
    )
    public JSONObject api() {
        return this.getAtExpose().getAPI().getState().getJson();
    }


    @Expose(
            description = "Returns the current @Expose state.",
            requiredAccessLevel = 2,
            labels = {"@Expose"}
    )
    public JSONObject status() {
        return this.getAtExpose().getState().getJson();
    }


    @Expose(
            arguments = {"QueueProducerName", "Message"},
            requiredAccessLevel = 3,
            description = "Sends the argument message to a queue, e.g. AWS SQS.",
            labels = {"@Expose"}
    )
    public String sendToQueue(String queueProducerName, String message) {
        this.getAtExpose().sendToQueue(queueProducerName, message);
        return "Message sent. SqsSender: '" + queueProducerName + ". Message: '"
                + StringUtils.abbreviate(message, 50) + "'";
    }


}
