package com.atexpose;

import com.atexpose.api.API;
import com.atexpose.dispatcher.channels.CommandLineChannel;
import com.atexpose.dispatcher.channels.ScheduledReportChannel;
import com.atexpose.dispatcher.channels.ScheduledTaskChannel;
import com.atexpose.dispatcher.channels.ScriptFileChannel;
import com.atexpose.dispatcher.logging.Logger;
import com.atexpose.dispatcher.logging.LoggerBuilder;
import com.atexpose.dispatcher.logging.LoggerType;
import com.atexpose.dispatcher.logging.format.LogFormatterFactory;
import com.atexpose.dispatcher.logging.writer.LogWriterFactory;
import com.atexpose.dispatcher.parser.AbstractParser;
import com.atexpose.dispatcher.parser.TextParser;
import com.atexpose.dispatcher.wrapper.CsvWrapper;
import com.atexpose.util.DateTimeStrings;
import com.atexpose.util.mail.GmailEmailSender;
import com.atexpose.util.mail.IEmailSender;

import com.atexpose.util.mail.MockMailSender;
import io.schinzel.basicutils.collections.keyvalues.KeyValues;
import io.schinzel.basicutils.state.IStateNode;
import io.schinzel.basicutils.state.State;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.Accessors;
import org.json.JSONObject;
import io.schinzel.basicutils.Checker;
import io.schinzel.basicutils.EmptyObjects;
import io.schinzel.basicutils.Thrower;

/**
 * @author Schinzel
 */
@SuppressWarnings({"unused", "WeakerAccess", "SameParameterValue"})
@Accessors(prefix = "m")
public class AtExpose implements IStateNode {
    private static final int SINGLE_THREADED = 1;
    private final String mStartTime = DateTimeStrings.getDateTimeUTC();
    @Getter
    private final API mAPI;
    @Getter(AccessLevel.PROTECTED)
    private IEmailSender mMailSender;
    @Getter(AccessLevel.PRIVATE)
    KeyValues<Dispatcher> mDispatchers = KeyValues.create("Dispatchers");


    public static AtExpose create() {
        return new AtExpose();
    }


    AtExpose() {
        mAPI = new API();
        NativeSetup.setUp(mAPI);
        mAPI.expose(this);
    }
    //------------------------------------------------------------------------
    // Pret-a-porter Dispatchers
    //------------------------------------------------------------------------


    @Expose(
            arguments = {"FileName"},
            requiredAccessLevel = 3,
            requiredArgumentCount = 1,
            description = {"Reads and executes the argument script file.", "Useful for setting up your instance and executing scheduled tasks."},
            labels = {"@Expose", "AtExpose"}
    )
    public String loadScriptFile(String fileName) {
        Dispatcher dispatcher = Dispatcher.builder()
                .name("ScriptFile")
                .accessLevel(3)
                .channel(new ScriptFileChannel(fileName))
                .parser(new TextParser())
                .wrapper(new CsvWrapper())
                .noOfThreads(1)
                .api(mAPI)
                .build();
        this.startDispatcher(dispatcher, true, true);
        return "Script file '" + fileName + "' loaded.";
    }


    /**
     * Start command line interface.
     */
    public void startCLI() {
        Dispatcher dispatcher = Dispatcher.builder()
                .name("CommandLine")
                .accessLevel(3)
                .channel(new CommandLineChannel())
                .parser(new TextParser())
                .wrapper(new CsvWrapper())
                .noOfThreads(1)
                .api(mAPI)
                .build();
        this.startDispatcher(dispatcher, false, false);
    }


    public WebServerBuilder getWebServerBuilder() {
        return new WebServerBuilder(mAPI, mDispatchers);
    }


    @Expose(
            arguments = {"Port", "WebServerDir"},
            requiredAccessLevel = 3,
            description = {"Starts a web server."},
            labels = {"@Expose", "AtExpose"}
    )
    public String startWebServer(int port, String dir) {
        this.getWebServerBuilder()
                .port(port)
                .webServerDir(dir)
                .startWebServer();
        return "Web server started on port " + port;
    }
    //------------------------------------------------------------------------
    // SCHEDULED REPORTS
    //------------------------------------------------------------------------


    /**
     * Sets the GMail SMTP server used for sending scheduled reports.
     *
     * @param username The GMail username
     * @param password The GMail password
     * @return Status of the operation message.
     */
    @Expose(
            arguments = {"Username", "Password"},
            requiredAccessLevel = 3,
            requiredArgumentCount = 2,
            description = {"Sets the SMTP server to user for outgoing mails."},
            labels = {"@Expose", "AtExpose"}
    )
    public String setSMTPServerGmail(String username, String password) {
        mMailSender = new GmailEmailSender(username, password);
        return "SMTP server settings set";
    }


    @Expose(
            requiredAccessLevel = 3,
            description = {"Set usage of Mock SMTP server, i.e. do not send any real e-mail."},
            labels = {"@Expose", "AtExpose"}
    )
    public String setMockSMTPServer() {
        mMailSender = new MockMailSender();
        return "Mock SMTP server settings is set";
    }


    /**
     * @param TaskName  The name of the report.
     * @param Request   The request to execute.
     * @param TimeOfDay The time of day to run the report.
     * @param recipient The recipient email address.
     * @param fromName  The name in the from field in the mail
     * @return Status of the operation message.
     */
    @Expose(
            arguments = {"TaskName", "Request", "TimeOfDay", "Recipient", "FromName"},
            requiredAccessLevel = 3,
            requiredArgumentCount = 4,
            description = {"Performs a task every day at the stated time of day.",
                    "The time stated is in UTC.",
                    "Scheduled reports are close relatives of scheduled tasks with the difference that the result of operations are sent as mail."
                            + "Reports are given an event logger with default logger and format."},
            labels = {"@Expose", "AtExpose", "ScheduledTasks"}
    )
    public String addScheduledReport(String TaskName, String Request, String TimeOfDay, String recipient, String fromName) {
        Thrower.throwIfTrue(mMailSender == null, "You need to set SMTP settings before setting up a scheduled report. Use method setSMTPServer.");
        AbstractParser parser = new TextParser();
        parser.parseRequest(Request);
        String methodName = parser.getMethodName();
        if (!mAPI.methodExits(methodName)) {
            throw new RuntimeException("No such method '" + methodName + "'");
        }
        ScheduledReportChannel scheduledReport = ScheduledReportChannel.builder()
                .emailSender(mMailSender)
                .recipient(recipient)
                .request(Request)
                .taskName(TaskName)
                .timeOfDay(TimeOfDay)
                .fromName(fromName)
                .build();
        String dispatcherName = "ScheduledReport_" + TaskName;
        Dispatcher dispatcher = Dispatcher.builder()
                .name(dispatcherName)
                .accessLevel(3)
                .channel(scheduledReport)
                .parser(parser)
                .wrapper(new CsvWrapper())
                .noOfThreads(1)
                .api(mAPI)
                .build();
        this.startDispatcher(dispatcher, false, false);
        String cryptoKey = EmptyObjects.EMPTY_STRING;
        this.addEventLogger(dispatcherName, "JsonFormatter", "SystemOutLogWriter", cryptoKey);
        return "Scheduled report '" + TaskName + "' has been set up";
    }


    //------------------------------------------------------------------------
    // SCHEDULED TASKS
    //------------------------------------------------------------------------
    @Expose(
            arguments = {"TaskName", "Request", "TimeOfDay"},
            requiredAccessLevel = 3,
            requiredArgumentCount = 3,
            description = {"Performs a task every day at the stated time of day.",
                    "The time stated is in UTC.",
                    "Tasks are given an event logger with default logger and format."},
            labels = {"@Expose", "AtExpose", "ScheduledTasks"}
    )
    public String addDailyTask(String TaskName, String Request, String TimeOfDay) {
        ScheduledTaskChannel scheduledTaskChannel = new ScheduledTaskChannel(TaskName, Request, TimeOfDay);
        return this.addTask(TaskName, scheduledTaskChannel);
    }


    @Expose(
            arguments = {"TaskName", "Request", "Minutes"},
            requiredAccessLevel = 3,
            requiredArgumentCount = 3,
            description = {"Adds a scheduled a task that will run every stated number of minutes.",
                    "The first time the task will run at the stated number minutes after the task was added.",
                    "The after the first time, the task will run the stated number of minutes after the execution of the previous task was finished.",
                    "Tasks are given an event logger with default logger and format."},
            labels = {"@Expose", "AtExpose", "ScheduledTasks"}
    )
    public String addTask(String TaskName, String Request, int Minutes) {
        ScheduledTaskChannel scheduledTaskChannel = new ScheduledTaskChannel(TaskName, Request, Minutes);
        return this.addTask(TaskName, scheduledTaskChannel);
    }


    @Expose(
            arguments = {"TaskName", "Request", "TimeOfDay", "DayOfMonth"},
            requiredAccessLevel = 3,
            description = {"Adds a scheduled a task that will run monthly at the stated time of day at the stated day of month",
                    "The time stated is in UTC.",
                    "Tasks are given an event logger with default logger and format."},
            requiredArgumentCount = 4,
            labels = {"@Expose", "AtExpose", "ScheduledTasks"}
    )
    public String addMonthlyTask(String TaskName, String Request, String TimeOfDay, int DayOfMonth) {
        ScheduledTaskChannel scheduledTaskChannel = new ScheduledTaskChannel(TaskName, Request, TimeOfDay, DayOfMonth);
        return this.addTask(TaskName, scheduledTaskChannel);
    }


    @Expose(
            arguments = {"TaskName"},
            requiredAccessLevel = 3,
            requiredArgumentCount = 1,
            description = {"Removes the argument scheduled task."},
            labels = {"@Expose", "AtExpose", "ScheduledTasks"}
    )
    public String removeTask(String TaskName) {
        String dispatcherName = "ScheduledTask_" + TaskName;
        this.closeDispatcher(dispatcherName);
        return "Task '" + TaskName + "' was removed";
    }


    private String addTask(String TaskName, ScheduledTaskChannel scheduledTask) {
        AbstractParser parser = new TextParser();
        parser.parseRequest(scheduledTask.getRequestAsString());
        String methodName = parser.getMethodName();
        Thrower.throwIfFalse(mAPI.methodExits(methodName), "No such method '" + methodName + "'");
        String dispatcherName = "ScheduledTask_" + TaskName;
        Dispatcher dispatcher = Dispatcher.builder()
                .name(dispatcherName)
                .accessLevel(3)
                .channel(scheduledTask)
                .parser(parser)
                .wrapper(new CsvWrapper())
                .noOfThreads(1)
                .api(mAPI)
                .build();
        this.startDispatcher(dispatcher, false, false);
        String cryptoKey = EmptyObjects.EMPTY_STRING;
        this.addEventLogger(dispatcherName, "JsonFormatter", "SystemOutLogWriter", cryptoKey);
        this.addErrorLogger(dispatcherName, "JsonFormatter", "SystemOutLogWriter", cryptoKey);
        return "Task '" + TaskName + "' set up";
    }


    // ---------------------------------
    // - Loggers  -
    // ---------------------------------
    @Expose(
            arguments = {"DispatcherName",
                    "LogWriter",
                    "LogFormatter",
                    "CryptoKey"},
            requiredAccessLevel = 3,
            description = {"Adds an event logger to a dispatcher."},
            labels = {"@Expose", "AtExpose", "Logs"},
            requiredArgumentCount = 1
    )
    public String addEventLogger(String DispatcherName, String LogFormatter, String LogWriter, String cryptoKey) {
        return this.addLogger(LoggerType.EVENT, DispatcherName, LogFormatter, LogWriter, cryptoKey);
    }


    @Expose(
            arguments = {"DispatcherName", "LogWriter", "LogFormatter", "CryptoKey"},
            requiredAccessLevel = 3,
            description = {"Adds an error logger to a dispatcher."},
            labels = {"@Expose", "AtExpose", "Logs"},
            requiredArgumentCount = 1
    )
    public String addErrorLogger(String DispatcherName, String LogFormatter, String LogWriter, String cryptoKey) {
        return this.addLogger(LoggerType.ERROR, DispatcherName, LogFormatter, LogWriter, cryptoKey);
    }


    private String addLogger(LoggerType loggerType, String DispatcherName, String LogFormatter, String LogWriter, String cryptoKey) {
        LogFormatterFactory logFormatter = LogFormatterFactory.get(LogFormatter);
        LogWriterFactory logWriter = LogWriterFactory.create(LogWriter);
        LoggerBuilder loggerBuilder = LoggerBuilder.getBuilder()
                .setLoggerType(loggerType)
                .setLogFormatter(logFormatter)
                .setLogWriter(logWriter);
        if (!Checker.isEmpty(cryptoKey)) {
            loggerBuilder.setCryptoKey(cryptoKey);
        }
        Dispatcher dispatcher = this.getDispatchers().get(DispatcherName);
        return this.addLogger(dispatcher, loggerBuilder);
    }


    /**
     * Note that sending in loggerBuilder instead of logger is to make sure
     * that the same logger is not sent to more than one dispatcher.
     *
     * @param dispatcher    The dispatcher that will get the logger build by argument loggerBuilder
     * @param loggerBuilder The builder for the logger.
     * @return Status of the operation.
     */
    public String addLogger(Dispatcher dispatcher, LoggerBuilder loggerBuilder) {
        Logger logger = loggerBuilder.createLogger(dispatcher.getKey());
        dispatcher.addLogger(logger);
        return "Dispatcher " + dispatcher.getKey() + " got a logger";
    }


    @Expose(
            arguments = {"DispatcherName"},
            requiredAccessLevel = 3,
            description = {"Removes all loggers from argument dispatcher."},
            labels = {"@Expose", "AtExpose", "Logs"},
            requiredArgumentCount = 1
    )
    public String removeAllLoggers(String DispatcherName) {
        mDispatchers.get(DispatcherName).removeLoggers();
        return "All loggers removed from '" + DispatcherName + "'";
    }


    // ---------------------------------
    // - PUBLIC  -
    // ---------------------------------
    @Expose(
            description = {"Shuts down the system"},
            requiredAccessLevel = 3,
            aliases = {"close", "bye", "exit"},
            labels = {"@Expose"}
    )
    public synchronized String shutdown() {
        //Shutdown all the dispatchers
        this.getDispatchers().forEach(Dispatcher::shutdown);
        //Empty the dispatcher collection.
        this.getDispatchers().clear();
        return "Shutting down...";
    }


    @Expose(
            arguments = {"DispatcherName"},
            requiredAccessLevel = 3,
            description = {"Closes the argument dispatcher."},
            labels = {"@Expose", "AtExpose"}
    )
    public String closeDispatcher(String name) {
        Dispatcher dispatcher = this.getDispatchers().get(name);
        dispatcher.shutdown();
        this.getDispatchers().remove(name);
        return "Dispatcher " + name + " has been closed";
    }


    /**
     * Central method for starting a dispatcher.
     *
     * @param dispatcher       The dispatcher to start
     * @param isSynchronized   If true the dispatcher, is executed before this method returns.
     * @param oneOffDispatcher If true the dispatcher is a one-off that executes and then terminates. Is never added
     *                         to the dispatcher collection.
     * @return The dispatcher that was just started.
     */
    public Dispatcher startDispatcher(Dispatcher dispatcher, boolean isSynchronized, boolean oneOffDispatcher) {
        //If this is not a temporary dispatcher, i.e. a dispatcher that dies once it has read its requests and delivered its responses
        if (!oneOffDispatcher) {
            //Add the newly created dispatcher to the dispatcher collection
            this.getDispatchers().add(dispatcher);
        }
        //Start the messaging!
        dispatcher.commenceMessaging(isSynchronized);
        return dispatcher;
    }
    // ---------------------------------
    // - API  -
    // ---------------------------------


    @Expose(
            description = {"Returns the API."},
            requiredAccessLevel = 3,
            labels = {"@Expose"}
    )
    public JSONObject apiAsJson() {
        return new JSONObject().put("API", mAPI.getState().getJson());
    }


    @Expose(
            description = {"Returns the API."},
            requiredAccessLevel = 3,
            labels = {"@Expose"}
    )
    public String api() {
        return mAPI.getState().getString();
    }


    // ---------------------------------
    // - STATUS  -
    // ---------------------------------
    @Expose(
            description = {"Returns the current state of this instance as json."},
            requiredAccessLevel = 2,
            labels = {"@Expose"}
    )
    public JSONObject statusAsJson() {
        return this.getState().getJson();
    }


    @Expose(
            description = {"Returns the current state of this instance."},
            requiredAccessLevel = 2,
            labels = {"@Expose"}
    )
    public String status() {
        return this.getState().getString();
    }


    @Override
    public State getState() {
        return State.getBuilder()
                .add("TimeNow", DateTimeStrings.getDateTimeUTC())
                .add("StartTime", mStartTime)
                .add("EmailSender", mMailSender)
                .add("Dispatchers", this.getDispatchers())
                .build();
    }


}
