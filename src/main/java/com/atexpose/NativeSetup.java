package com.atexpose;

import com.atexpose.api.API;
import com.atexpose.api.Argument;
import com.atexpose.api.datatypes.DataType;

/**
 * The purpose of this class is to native se up for @Expose.
 * I.e. set up
 * - labels
 * - arguments
 * <p>
 * Created by Schinzel on 2017-03-07.
 */
class NativeSetup {

    static void setUp(API api) {
        NativeSetup.setUpLabels(api);
        NativeSetup.setArguments(api);
        NativeSetup.setUpMethods(api);
    }


    private static void setUpLabels(API api) {
        api.addLabel("@Expose", "Methods native to @Expose.")
                .addLabel("Util", "Misc util methods.")
                .addLabel("AtExpose", "Administrating @Expose.")
                .addLabel("ScheduledTasks", "Scheduled tasks")
                .addLabel("Misc", "Misc methods")
                .addLabel("Logs", "Logs");
    }


    private static void setArguments(API api) {
        api
                .addArgument(Argument.builder()
                        .name("FileName")
                        .dataType(DataType.STRING)
                        .description("The name of a file.")
                        .build())
                .addArgument(Argument.builder()
                        .name("Port")
                        .dataType(DataType.INT)
                        .description("The port to use.")
                        .defaultValue("5555")
                        .build())
                .addArgument(Argument.builder()
                        .name("WebServerDir")
                        .dataType(DataType.STRING)
                        .description("The name and path to the directory to expose with this web server.")
                        .defaultValue("web")
                        .build())
                .addArgument(Argument.builder()
                        .name("DispatcherName")
                        .dataType(DataType.ALPHNUMSTRING)
                        .description("Name of a dispatcher")
                        .build())
                //@Expose
                .addArgument(Argument.builder()
                        .name("Channel")
                        .dataType(DataType.ALPHNUMSTRING)
                        .description("Reads incoming requests and sends the responses")
                        .build())
                .addArgument(Argument.builder()
                        .name("AccessLevel")
                        .dataType(DataType.INT)
                        .description("Sets which level of commands this dispatcher has access to.")
                        .defaultValue("1").build())
                .addArgument(Argument.builder()
                        .name("NoOfThreads")
                        .dataType(DataType.INT)
                        .description("Number of threads")
                        .defaultValue("10")
                        .build())
                .addArgument(Argument.builder()
                        .name("Timeout")
                        .dataType(DataType.INT)
                        .description("Timeout in milliseconds")
                        .defaultValue("300")
                        .build())
                .addArgument(Argument.builder()
                        .name("CacheMaxAge")
                        .dataType(DataType.INT)
                        .description("The cache max age in seconds as sent to browsers. Cache age is always set to zero for method calls.")
                        .defaultValue("1200")
                        .build())
                .addArgument(Argument.builder()
                        .name("UseCachedFiles")
                        .dataType(DataType.BOOLEAN)
                        .description("If to cache files read from drive in RAM for better performance.")
                        .defaultValue("true")
                        .build())
                .addArgument(Argument.builder()
                        .name("DefaultPage")
                        .dataType(DataType.STRING)
                        .description("Default page to load if a call to web server does not specify a specific file.")
                        .defaultValue("index.html")
                        .build())
                .addArgument(Argument.builder()
                        .name("ForceDefaultPage")
                        .dataType(DataType.BOOLEAN)
                        .description("If true, all calls are redirected to the default page.")
                        .defaultValue("false")
                        .build())
                //Logging
                .addArgument(Argument.builder()
                        .name("LogWriter")
                        .dataType(DataType.ALPHNUMSTRING)
                        .description("Where the log entries will be written")
                        .defaultValue("File")
                        .build())
                .addArgument(Argument.builder()
                        .name("LogFormatter")
                        .dataType(DataType.ALPHNUMSTRING)
                        .description("The format of the log entries")
                        .defaultValue("MultiLineFormatter")
                        .build())
                .addArgument(Argument.builder()
                        .name("CryptoKey")
                        .dataType(DataType.ALPHNUMSTRING)
                        .description("Key used to encrypt and decrypt data.")
                        //Tasks
                        .build())
                .addArgument(Argument.builder()
                        .name("TaskName")
                        .dataType(DataType.ALPHNUMSTRING)
                        .description("The name of the task.")
                        .build())
                .addArgument(Argument.builder()
                        .name("Minutes")
                        .dataType(DataType.INT)
                        .description("A number of minutes.")
                        .defaultValue("1440").build())
                .addArgument(Argument.builder()
                        .name("TimeOfDay")
                        .dataType(DataType.STRING)
                        .description("The time to use. Format HH:mm, i.e. in 24h format, e.g. 22:55. The time is the time-zone of the machine running the instance.")
                        .build())
                .addArgument(Argument.builder()
                        .name("DayOfMonth")
                        .dataType(DataType.INT)
                        .description("A day of month")
                        .build())
                .addArgument(Argument.builder()
                        .name("Request")
                        .dataType(DataType.STRING)
                        .description("A request in the text format. E.g. echo hello")
                        .build())
                //Report
                .addArgument(Argument.builder()
                        .name("FromName")
                        .dataType(DataType.ALPHNUMSTRING)
                        .description("The personal name in the FROM field in an email.")
                        .defaultValue("@Expose")
                        .build())
                .addArgument(Argument.builder()
                        .name("Recipient")
                        .dataType(DataType.ALPHNUMSTRING)
                        .description("A mail address in the format recipient@domain.com.")
                        .build())
                .addArgument(Argument.builder()
                        .name("ForceHttps")
                        .dataType(DataType.BOOLEAN)
                        .description("If true, http calls are redirected to https calls.")
                        .defaultValue("false")
                        .build())
                .addArgument(Argument.builder()
                        .name("Username")
                        .dataType(DataType.STRING)
                        .description("A username.")
                        .build())
                .addArgument(Argument.builder()
                        .name("Password")
                        .dataType(DataType.STRING)
                        .description("A password.")
                        .build());
    }


    private static void setUpMethods(API api) {
        api.expose(new Misc());
    }
}
