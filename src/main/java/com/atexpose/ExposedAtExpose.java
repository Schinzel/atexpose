package com.atexpose;

import org.json.JSONObject;

/**
 * The purpose of this class is to expose @Expose and allow string returns that contain status of the operation
 * messages.
 * <p>
 * Created by schinzel on 2017-04-16.
 */
class ExposedAtExpose {
    private final AtExpose mAtExpose;


    private ExposedAtExpose(AtExpose atExpose) {
        mAtExpose = atExpose;
    }


    static ExposedAtExpose create(AtExpose atExpose) {
        return new ExposedAtExpose(atExpose);
    }


    @Expose(
            arguments = {"FileName", "Recipient"},
            requiredAccessLevel = 3,
            requiredArgumentCount = 1,
            description = {"Reads and executes the argument script file.", "Useful for setting up settings, scheduled tasks and so on."},
            labels = {"@Expose", "AtExpose"}
    )
    public String loadScriptFile(String fileName, String recipient) {
        mAtExpose.loadScriptFile(fileName, recipient);
        return "Script file '" + fileName + "' loaded.";
    }


    @Expose(
            arguments = {"Port", "WebServerDir"},
            requiredAccessLevel = 3,
            description = {"Starts a web server."},
            labels = {"@Expose", "AtExpose"}
    )
    public String startWebServer(int port, String dir) {
        mAtExpose.getWebServerBuilder()
                .port(port)
                .webServerDir(dir)
                .startWebServer();
        return "Web server started on port " + port;
    }


    @Expose(
            arguments = {"Username", "Password"},
            requiredAccessLevel = 3,
            requiredArgumentCount = 2,
            description = {"Sets the SMTP server to user for outgoing mails."},
            labels = {"@Expose", "AtExpose"}
    )
    public String setSMTPServerGmail(String username, String password) {
        mAtExpose.setSMTPServerGmail(username, password);
        return "SMTP server settings set";
    }


    @Expose(
            requiredAccessLevel = 3,
            description = {"Set usage of Mock SMTP server, i.e. do not send any real e-mail."},
            labels = {"@Expose", "AtExpose"}
    )
    public String setMockSMTPServer() {
        mAtExpose.setMockSMTPServer();
        return "Mock SMTP server settings is set";
    }


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
        mAtExpose.addScheduledReport(TaskName, Request, TimeOfDay, recipient, fromName);
        return "Scheduled report '" + TaskName + "' has been set up";
    }


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
        mAtExpose.addDailyTask(TaskName, Request, TimeOfDay);
        return "Task '" + TaskName + "' set up";
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
        mAtExpose.addTask(TaskName, Request, Minutes);
        return "Task '" + TaskName + "' set up";
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
        mAtExpose.addMonthlyTask(TaskName, Request, TimeOfDay, DayOfMonth);
        return "Task '" + TaskName + "' set up";
    }


    @Expose(
            arguments = {"TaskName"},
            requiredAccessLevel = 3,
            requiredArgumentCount = 1,
            description = {"Removes the argument scheduled task."},
            labels = {"@Expose", "AtExpose", "ScheduledTasks"}
    )
    public String removeTask(String TaskName) {
        mAtExpose.removeTask(TaskName);
        return "Task '" + TaskName + "' was removed";
    }


    @Expose(
            arguments = {"DispatcherName", "LogWriter", "LogFormatter", "CryptoKey"},
            requiredAccessLevel = 3,
            description = {"Adds an event logger to a dispatcher."},
            labels = {"@Expose", "AtExpose", "Logs"},
            requiredArgumentCount = 1
    )
    public String addEventLogger(String DispatcherName, String LogFormatter, String LogWriter, String cryptoKey) {
        mAtExpose.addEventLogger(DispatcherName, LogFormatter, LogWriter, cryptoKey);
        return "Dispatcher " + DispatcherName + " got an event logger";
    }


    @Expose(
            arguments = {"DispatcherName", "LogWriter", "LogFormatter", "CryptoKey"},
            requiredAccessLevel = 3,
            description = {"Adds an error logger to a dispatcher."},
            labels = {"@Expose", "AtExpose", "Logs"},
            requiredArgumentCount = 1
    )
    public String addErrorLogger(String DispatcherName, String LogFormatter, String LogWriter, String cryptoKey) {
        mAtExpose.addErrorLogger(DispatcherName, LogFormatter, LogWriter, cryptoKey);
        return "Dispatcher " + DispatcherName + " got an error logger";
    }


    @Expose(
            arguments = {"DispatcherName"},
            requiredAccessLevel = 3,
            description = {"Removes all loggers from a dispatcher."},
            labels = {"@Expose", "AtExpose", "Logs"},
            requiredArgumentCount = 1
    )
    public String removeAllLoggers(String dispatcherName) {
        mAtExpose.removeAllLoggers(dispatcherName);
        return "Removed all loggers from dispatcher '" + dispatcherName + "'";
    }


    @Expose(
            description = {"Shuts down the system"},
            requiredAccessLevel = 3,
            aliases = {"close", "bye", "exit"},
            labels = {"@Expose"}
    )
    public synchronized String shutdown() {
        mAtExpose.shutdown();
        return "Shutting down...";
    }


    @Expose(
            arguments = {"DispatcherName"},
            requiredAccessLevel = 3,
            description = {"Closes the argument dispatcher."},
            labels = {"@Expose", "AtExpose"}
    )
    public String closeDispatcher(String name) {
        mAtExpose.closeDispatcher(name);
        return "Dispatcher " + name + " has been closed";
    }


    @Expose(
            description = {"Returns the current state of this instance as json."},
            requiredAccessLevel = 2,
            labels = {"@Expose"}
    )
    public JSONObject apiAsJson() {
        return mAtExpose.getAPI().getState().getJson();
    }


    @Expose(
            description = {"Returns the current state of this instance."},
            requiredAccessLevel = 2,
            labels = {"@Expose"}
    )
    public String api() {
        return mAtExpose.getAPI().getState().getString();
    }


}
