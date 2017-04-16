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
     * @param TaskName  The name of the report.
     * @param Request   The request to execute.
     * @param TimeOfDay The time of day to run the report.
     * @param recipient The recipient email address.
     * @param fromName  The name in the from field in the mail
     * @return Status of the operation message.
     */
    public AtExpose addScheduledReport(String TaskName, String Request, String TimeOfDay, String recipient, String fromName) {
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
        return this;
    }


    //------------------------------------------------------------------------
    // SCHEDULED TASKS
    //------------------------------------------------------------------------
    public AtExpose addDailyTask(String TaskName, String Request, String TimeOfDay) {
        ScheduledTaskChannel scheduledTaskChannel = new ScheduledTaskChannel(TaskName, Request, TimeOfDay);
        this.addTask(TaskName, scheduledTaskChannel);
        return this;
    }


    public AtExpose addTask(String TaskName, String Request, int Minutes) {
        ScheduledTaskChannel scheduledTaskChannel = new ScheduledTaskChannel(TaskName, Request, Minutes);
        return this.addTask(TaskName, scheduledTaskChannel);
    }


    public AtExpose addMonthlyTask(String TaskName, String Request, String TimeOfDay, int DayOfMonth) {
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
    public AtExpose removeTask(String TaskName) {
        String dispatcherName = "ScheduledTask_" + TaskName;
        this.closeDispatcher(dispatcherName);
        return this;
    }


    private AtExpose addTask(String TaskName, ScheduledTaskChannel scheduledTask) {
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
        return this;
    }


    // ---------------------------------
    // - Loggers  -
    // ---------------------------------
    public AtExpose addEventLogger(String DispatcherName, String LogFormatter, String LogWriter, String cryptoKey) {
        return this.addLogger(LoggerType.EVENT, DispatcherName, LogFormatter, LogWriter, cryptoKey);
    }


    public AtExpose addErrorLogger(String DispatcherName, String LogFormatter, String LogWriter, String cryptoKey) {
        return this.addLogger(LoggerType.ERROR, DispatcherName, LogFormatter, LogWriter, cryptoKey);
    }


    private AtExpose addLogger(LoggerType loggerType, String DispatcherName, String LogFormatter, String LogWriter, String cryptoKey) {
        ILogFormatter logFormatter = LogFormatterFactory.get(LogFormatter).getInstance();
        ILogWriter logWriter = LogWriterFactory.create(LogWriter).getInstance();
        ICrypto crypto = Checker.isEmpty(cryptoKey)
                ? new NoCrypto()
                : Crypto.getInstance(cryptoKey);
        Logger loggerBuilder = Logger.builder()
                .loggerType(loggerType)
                .logFormat(logFormatter)
                .logWriter(logWriter)
                .crypto(crypto)
                .build();
        Dispatcher dispatcher = this.getDispatchers().get(DispatcherName);
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
    // - API  -
    // ---------------------------------


    public API getAPI() {
        return mAPI;
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
