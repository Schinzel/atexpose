package com.atexpose;

import com.atexpose.api.API;
import com.atexpose.dispatcher.channels.CommandLineChannel;
import com.atexpose.dispatcher.channels.ScheduledReportChannel;
import com.atexpose.dispatcher.channels.ScheduledTaskChannel;
import com.atexpose.dispatcher.channels.ScriptFileChannel;
import com.atexpose.dispatcher.logging.Logger;
import com.atexpose.dispatcher.logging.LoggerType;
import com.atexpose.dispatcher.logging.crypto.Crypto;
import com.atexpose.dispatcher.logging.crypto.ICrypto;
import com.atexpose.dispatcher.logging.crypto.NoCrypto;
import com.atexpose.dispatcher.logging.format.ILogFormatter;
import com.atexpose.dispatcher.logging.format.LogFormatterFactory;
import com.atexpose.dispatcher.logging.format.MultiLineFormatter;
import com.atexpose.dispatcher.logging.writer.ILogWriter;
import com.atexpose.dispatcher.logging.writer.LogWriterFactory;
import com.atexpose.dispatcher.logging.writer.MailLogWriter;
import com.atexpose.dispatcher.parser.AbstractParser;
import com.atexpose.dispatcher.parser.TextParser;
import com.atexpose.dispatcher.wrapper.CsvWrapper;
import com.atexpose.util.DateTimeStrings;
import com.atexpose.util.mail.GmailEmailSender;
import com.atexpose.util.mail.IEmailSender;
import com.atexpose.util.mail.MockMailSender;
import io.schinzel.basicutils.Checker;
import io.schinzel.basicutils.EmptyObjects;
import io.schinzel.basicutils.Thrower;
import io.schinzel.basicutils.collections.keyvalues.KeyValues;
import io.schinzel.basicutils.state.IStateNode;
import io.schinzel.basicutils.state.State;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.Accessors;

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
        mAPI.expose(ExposedAtExpose.create(this));
    }
    //------------------------------------------------------------------------
    // Pret-a-porter Dispatchers
    //------------------------------------------------------------------------


    public AtExpose loadScriptFile(String fileName) {
        return this.loadScriptFile(fileName, EmptyObjects.EMPTY_STRING);
    }


    public AtExpose loadScriptFile(String fileName, String recipient) {
        Dispatcher dispatcher = Dispatcher.builder()
                .name("ScriptFile")
                .accessLevel(3)
                .channel(new ScriptFileChannel(fileName))
                .parser(new TextParser())
                .wrapper(new CsvWrapper())
                .noOfThreads(1)
                .api(mAPI)
                .build();
        //If there was a recipient argument
        if (!Checker.isEmpty(recipient)) {
            //Set up an error email logger
            Logger logger = Logger.builder()
                    .loggerType(LoggerType.ERROR)
                    .logFormat(new MultiLineFormatter())
                    .logWriter(new MailLogWriter(recipient, mMailSender))
                    .build();
            dispatcher.addLogger(logger);
        }
        this.startDispatcher(dispatcher, true, true);
        return this;
    }


    /**
     * Start command line interface.
     */
    public AtExpose startCLI() {
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
        return this;
    }


    public WebServerBuilder getWebServerBuilder() {
        return new WebServerBuilder(mAPI, mDispatchers);
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
    public AtExpose setSMTPServerGmail(String username, String password) {
        mMailSender = new GmailEmailSender(username, password);
        return this;
    }


    public AtExpose setMockSMTPServer() {
        mMailSender = new MockMailSender();
        return this;
    }


    /**
     * @param taskName  The name of the report.
     * @param request   The request to execute.
     * @param timeOfDay The time of day to run the report.
     * @param recipient The recipient email address.
     * @param fromName  The name in the from field in the mail
     * @return Status of the operation message.
     */
    public AtExpose addScheduledReport(String taskName, String request, String timeOfDay, String recipient, String fromName) {
        Thrower.throwIfTrue(mMailSender == null, "You need to set SMTP settings before setting up a scheduled report. Use method setSMTPServer.");
        AbstractParser parser = new TextParser();
        parser.parseRequest(request);
        String methodName = parser.getMethodName();
        if (!mAPI.methodExits(methodName)) {
            throw new RuntimeException("No such method '" + methodName + "'");
        }
        ScheduledReportChannel scheduledReport = ScheduledReportChannel.builder()
                .emailSender(mMailSender)
                .recipient(recipient)
                .request(request)
                .taskName(taskName)
                .timeOfDay(timeOfDay)
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
                .api(mAPI)
                .build();
        this.startDispatcher(dispatcher, false, false);
        String cryptoKey = EmptyObjects.EMPTY_STRING;
        this.addEventLogger(dispatcherName, "JsonFormatter", "SystemOutLogWriter", cryptoKey);
        return this;
    }


    //------------------------------------------------------------------------
    // SCHEDULED TASKS
    //------------------------------------------------------------------------
    public AtExpose addDailyTask(String taskName, String request, String timeOfDay) {
        ScheduledTaskChannel scheduledTaskChannel = new ScheduledTaskChannel(taskName, request, timeOfDay);
        this.addTask(taskName, scheduledTaskChannel);
        return this;
    }


    public AtExpose addTask(String taskName, String request, int minutes) {
        ScheduledTaskChannel scheduledTaskChannel = new ScheduledTaskChannel(taskName, request, minutes);
        return this.addTask(taskName, scheduledTaskChannel);
    }


    public AtExpose addMonthlyTask(String taskName, String request, String timeOfDay, int dayOfMonth) {
        ScheduledTaskChannel scheduledTaskChannel = new ScheduledTaskChannel(taskName, request, timeOfDay, dayOfMonth);
        return this.addTask(taskName, scheduledTaskChannel);
    }


    @Expose(
            arguments = {"TaskName"},
            requiredAccessLevel = 3,
            requiredArgumentCount = 1,
            description = {"Removes the argument scheduled task."},
            labels = {"@Expose", "AtExpose", "ScheduledTasks"}
    )
    public AtExpose removeTask(String taskName) {
        String dispatcherName = "ScheduledTask_" + taskName;
        this.closeDispatcher(dispatcherName);
        return this;
    }


    private AtExpose addTask(String taskName, ScheduledTaskChannel scheduledTask) {
        AbstractParser parser = new TextParser();
        parser.parseRequest(scheduledTask.getRequestAsString());
        String methodName = parser.getMethodName();
        Thrower.throwIfFalse(mAPI.methodExits(methodName), "No such method '" + methodName + "'");
        String dispatcherName = "ScheduledTask_" + taskName;
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
        return this;
    }


    // ---------------------------------
    // - Loggers  -
    // ---------------------------------
    public AtExpose addEventLogger(String dispatcherName, String logFormatter, String logWriter, String cryptoKey) {
        return this.addLogger(LoggerType.EVENT, dispatcherName, logFormatter, logWriter, cryptoKey);
    }


    public AtExpose addErrorLogger(String DispatcherName, String LogFormatter, String LogWriter, String cryptoKey) {
        return this.addLogger(LoggerType.ERROR, DispatcherName, LogFormatter, LogWriter, cryptoKey);
    }


    private AtExpose addLogger(LoggerType loggerType, String dispatcherName, String logFormatter, String logWriter, String cryptoKey) {
        ILogFormatter logFormatterObj = LogFormatterFactory.get(logFormatter).getInstance();
        ILogWriter logWriterObj = LogWriterFactory.create(logWriter).getInstance();
        ICrypto crypto = Checker.isEmpty(cryptoKey)
                ? new NoCrypto()
                : Crypto.getInstance(cryptoKey);
        Logger loggerBuilder = Logger.builder()
                .loggerType(loggerType)
                .logFormat(logFormatterObj)
                .logWriter(logWriterObj)
                .crypto(crypto)
                .build();
        Dispatcher dispatcher = this.getDispatchers().get(dispatcherName);
        return this.addLogger(dispatcher, loggerBuilder);
    }


    /**
     * Note that sending in loggerBuilder instead of logger is to make sure
     * that the same logger is not sent to more than one dispatcher.
     *
     * @param dispatcher The dispatcher that will get the logger build by argument loggerBuilder
     * @param logger     The logger.
     * @return Status of the operation.
     */
    public AtExpose addLogger(Dispatcher dispatcher, Logger logger) {
        dispatcher.addLogger(logger);
        return this;
    }


    public AtExpose removeAllLoggers(String dispatcherName) {
        if (this.getDispatchers().has(dispatcherName)) {
            Dispatcher dispatcher = this.getDispatchers().get(dispatcherName);
            dispatcher.removeLoggers();
            return this;
        } else {
            throw new RuntimeException("No such dispatcher '" + dispatcherName + "'");
        }
    }


    // ---------------------------------
    // - MISC  -
    // ---------------------------------
    public synchronized AtExpose shutdown() {
        //Shutdown all the dispatchers
        this.getDispatchers().forEach(Dispatcher::shutdown);
        //Empty the dispatcher collection.
        this.getDispatchers().clear();
        return this;
    }


    public String closeDispatcher(String dispatcherName) {
        Dispatcher dispatcher = this.getDispatchers().get(dispatcherName);
        dispatcher.shutdown();
        this.getDispatchers().remove(dispatcherName);
        return "Dispatcher " + dispatcherName + " has been closed";
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
    Dispatcher startDispatcher(Dispatcher dispatcher, boolean isSynchronized, boolean oneOffDispatcher) {
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
    // - STATUS  -
    // ---------------------------------


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
