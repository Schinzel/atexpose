package com.atexpose;

import com.atexpose.api.API;
import com.atexpose.dispatcher.Dispatcher;
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
 * The central class that is the spider in the web.
 * <p>
 * The core purpose of an instance of this class is to start and stop dispatchers.
 *
 * @author Schinzel
 */
@SuppressWarnings({"unused", "WeakerAccess", "SameParameterValue", "UnusedReturnValue"})
@Accessors(prefix = "m")
public class AtExpose implements IStateNode {
    /** Instance creation time. For status and debug purposes. */
    private final String mInstanceStartTime = DateTimeStrings.getDateTimeUTC();
    /** Reference to the API. */
    @Getter private final API mAPI;
    /** Holds an email sender instance if such has been set up. */
    @Getter(AccessLevel.PROTECTED) private IEmailSender mMailSender;
    /** Holds the running dispatchers */
    @Getter(AccessLevel.PRIVATE) KeyValues<Dispatcher> mDispatchers = KeyValues.create("Dispatchers");
    //------------------------------------------------------------------------
    // CONSTRUCTION
    //------------------------------------------------------------------------


    /**
     * @return A freshly created instance.
     */
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


    /**
     * @param fileName The name of the script file to read.
     * @return This for chaining.
     */
    public AtExpose loadScriptFile(String fileName) {
        return this.loadScriptFile(fileName, EmptyObjects.EMPTY_STRING);
    }


    /**
     * @param fileName               The name of the script file to read.
     * @param emailErrorLogRecipient Recipient of an error log if there are any errors.
     * @return This for chaining.
     */
    public AtExpose loadScriptFile(String fileName, String emailErrorLogRecipient) {
        Dispatcher scriptFileDispatcher = Dispatcher.builder()
                .name("ScriptFile")
                .accessLevel(3)
                .channel(new ScriptFileChannel(fileName))
                .parser(new TextParser())
                .wrapper(new CsvWrapper())
                .noOfThreads(1)
                .api(mAPI)
                .build();
        //If there was a recipient argument
        if (!Checker.isEmpty(emailErrorLogRecipient)) {
            //Set up an error email logger
            Logger eMailLogger = Logger.builder()
                    .loggerType(LoggerType.ERROR)
                    .logFormat(new MultiLineFormatter())
                    .logWriter(new MailLogWriter(emailErrorLogRecipient, mMailSender))
                    .build();
            scriptFileDispatcher.addLogger(eMailLogger);
        }
        this.startDispatcher(scriptFileDispatcher, true, true);
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
     * Sets the Gmail SMTP server used for sending scheduled reports.
     *
     * @param username The Gmail username
     * @param password The Gmail password
     * @return This for chaining.
     */
    public AtExpose setSMTPServerGmail(String username, String password) {
        mMailSender = new GmailEmailSender(username, password);
        return this;
    }


    /**
     * Set a mock smtp server. For debugging and testing purposes.
     *
     * @return This for chaining.
     */
    public AtExpose setMockSMTPServer() {
        mMailSender = new MockMailSender();
        return this;
    }


    /**
     * Sets up a scheduled report. This is a scheduled task where the result of the operation is sent to the argument
     * recipient.
     *
     * @param taskName  The name of the report.
     * @param request   The request to execute. Example: "echo hi"
     * @param timeOfDay The time of day to run the report. Examples: "13:05" "07:55"
     * @param recipient The recipient email address.
     * @param fromName  The name in the from field in the email
     * @return This for chaining.
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


    /**
     * Sets up a task to run once a day at the argument time of the day.
     *
     * @param taskName  The name of the task.
     * @param request   The request to execute. Example: "time", "echo 123"
     * @param timeOfDay The time for the day. UTC. Examples "23:55" "07:05"
     * @return This for chaining.
     */
    public AtExpose addDailyTask(String taskName, String request, String timeOfDay) {
        ScheduledTaskChannel scheduledTaskChannel = new ScheduledTaskChannel(taskName, request, timeOfDay);
        this.addTask(taskName, scheduledTaskChannel);
        return this;
    }


    /**
     * Sets up a task to run every X minutes. Runs the first time after the argument number of minutes.
     *
     * @param taskName The name of the task.
     * @param request  The request to execute. Example: "time", "echo 123"
     * @param minutes  The interval at which to execute the task.
     * @return This for chaining.
     */
    public AtExpose addTask(String taskName, String request, int minutes) {
        ScheduledTaskChannel scheduledTaskChannel = new ScheduledTaskChannel(taskName, request, minutes);
        return this.addTask(taskName, scheduledTaskChannel);
    }


    /**
     * @param taskName   The name of the task.
     * @param request    The request to execute. Example: "time", "echo 123"
     * @param timeOfDay  The time for the day. UTC. Examples "23:55" "07:05"
     * @param dayOfMonth The day of the month the task can execute.
     * @return This for chaining.
     */
    public AtExpose addMonthlyTask(String taskName, String request, String timeOfDay, int dayOfMonth) {
        ScheduledTaskChannel scheduledTaskChannel = new ScheduledTaskChannel(taskName, request, timeOfDay, dayOfMonth);
        return this.addTask(taskName, scheduledTaskChannel);
    }


    /**
     * Removes a running scheduled task. Is shut down immediately.
     *
     * @param taskName The name of the task to remove.
     * @return This for chaining.
     */
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


    /**
     * Adds an event logger to a running dispatcher.
     *
     * @param dispatcherName The name of the dispatcher that will have the even logger added.
     * @param logFormatter   The name of the log formatter. Examples: "json", "multi_line", "single_line"
     * @param logWriter      The name of the log writer. Examples: "mail", "system_out"
     * @param cryptoKey      If empty the logs are not encrypted. If non empty it should be a 16 byte crypto key. If
     *                       non
     *                       empty, some parts of the logs will be encrypted with this key.
     * @return This for chaining.
     */
    public AtExpose addEventLogger(String dispatcherName, String logFormatter, String logWriter, String cryptoKey) {
        return this.addLogger(LoggerType.EVENT, dispatcherName, logFormatter, logWriter, cryptoKey);
    }


    /**
     * Adds an error logger to a running dispatcher.
     *
     * @param dispatcherName The name of the dispatcher that will have the even logger added.
     * @param logFormatter   The name of the log formatter. Examples: "json", "multi_line", "single_line"
     * @param logWriter      The name of the log writer. Examples: "mail", "system_out"
     * @param cryptoKey      If empty the logs are not encrypted. If non empty it should be a 16 byte crypto key. If
     *                       non
     *                       empty, some parts of the logs will be encrypted with this key.
     * @return This for chaining.
     */
    public AtExpose addErrorLogger(String dispatcherName, String logFormatter, String logWriter, String cryptoKey) {
        return this.addLogger(LoggerType.ERROR, dispatcherName, logFormatter, logWriter, cryptoKey);
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
     * Adds the argument logger to the argument dispatcher.
     *
     * @param dispatcher The dispatcher that will get the logger build by argument
     * @param logger     The logger.
     * @return This for chaining.
     */
    public AtExpose addLogger(Dispatcher dispatcher, Logger logger) {
        dispatcher.addLogger(logger);
        return this;
    }


    /**
     * Removes all loggers from a dispatcher.
     *
     * @param dispatcherName The name of the dispatchers from which all loggers - event and error - will be removed.
     * @return This for chaining.
     */
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


    /**
     * Shuts down all dispatchers of this instance.
     *
     * @return This for chaining.
     */
    public synchronized AtExpose shutdown() {
        //Shutdown all the dispatchers
        this.getDispatchers().forEach(Dispatcher::shutdown);
        //Empty the dispatcher collection.
        this.getDispatchers().clear();
        return this;
    }


    /**
     *
     * @param dispatcherName The dispatcher to shutdown.
     * @return This for chaining.
     */
    public AtExpose closeDispatcher(String dispatcherName) {
        Dispatcher dispatcher = this.getDispatchers().get(dispatcherName);
        dispatcher.shutdown();
        this.getDispatchers().remove(dispatcherName);
        return this;
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
                .add("StartTime", mInstanceStartTime)
                .add("EmailSender", mMailSender)
                .add("Dispatchers", this.getDispatchers())
                .build();
    }


}
