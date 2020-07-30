package com.atexpose;

import com.atexpose.api.API;
import com.atexpose.api.Argument;
import com.atexpose.api.datatypes.DataTypeEnum;

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
        NativeSetup.setUpArguments(api);
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


    private static void setUpArguments(API api) {
        api
                .addArgument(Argument.builder()
                        .name("FileName")
                        .dataType(DataTypeEnum.STRING)
                        .description("The name of a file.")
                        .build())
                .addArgument(Argument.builder()
                        .name("Port")
                        .dataType(DataTypeEnum.INT)
                        .description("The port to use.")
                        .defaultValue("5555")
                        .build())
                .addArgument(Argument.builder()
                        .name("WebServerDir")
                        .dataType(DataTypeEnum.STRING)
                        .description("The name and path to the directory to expose with this web server.")
                        .defaultValue("web")
                        .build())
                .addArgument(Argument.builder()
                        .name("DispatcherName")
                        .dataType(DataTypeEnum.ALPHA_NUMERIC_STRING)
                        .description("Name of a dispatcher")
                        .build())
                //@Expose
                .addArgument(Argument.builder()
                        .name("Channel")
                        .dataType(DataTypeEnum.ALPHA_NUMERIC_STRING)
                        .description("Reads incoming requests and sends the responses")
                        .build())
                .addArgument(Argument.builder()
                        .name("AccessLevel")
                        .dataType(DataTypeEnum.INT)
                        .description("Sets which level of commands this dispatcher has access to.")
                        .defaultValue("1").build())
                .addArgument(Argument.builder()
                        .name("NoOfThreads")
                        .dataType(DataTypeEnum.INT)
                        .description("Number of threads")
                        .defaultValue("10")
                        .build())
                .addArgument(Argument.builder()
                        .name("Timeout")
                        .dataType(DataTypeEnum.INT)
                        .description("Timeout in milliseconds")
                        .defaultValue("300")
                        .build())
                .addArgument(Argument.builder()
                        .name("CacheMaxAge")
                        .dataType(DataTypeEnum.INT)
                        .description("The cache max age in seconds as sent to browsers. Cache age is always set to zero for method calls.")
                        .defaultValue("1200")
                        .build())
                .addArgument(Argument.builder()
                        .name("UseCachedFiles")
                        .dataType(DataTypeEnum.BOOLEAN)
                        .description("If to cache files read from drive in RAM for better performance.")
                        .defaultValue("true")
                        .build())
                .addArgument(Argument.builder()
                        .name("DefaultPage")
                        .dataType(DataTypeEnum.STRING)
                        .description("Default page to load if a call to web server does not specify a specific file.")
                        .defaultValue("index.html")
                        .build())
                .addArgument(Argument.builder()
                        .name("ForceDefaultPage")
                        .dataType(DataTypeEnum.BOOLEAN)
                        .description("If true, all calls are redirected to the default page.")
                        .defaultValue("false")
                        .build())
                //Logging
                .addArgument(Argument.builder()
                        .name("LogWriter")
                        .dataType(DataTypeEnum.ALPHA_NUMERIC_STRING)
                        .description("Where the log entries will be written")
                        .defaultValue("system_out")
                        .build())
                .addArgument(Argument.builder()
                        .name("LogFormatter")
                        .dataType(DataTypeEnum.ALPHA_NUMERIC_STRING)
                        .description("The format of the log entries")
                        .defaultValue("json")
                        .build())
                .addArgument(Argument.builder()
                        .name("CryptoKey")
                        .dataType(DataTypeEnum.ALPHA_NUMERIC_STRING)
                        .description("Key used to encrypt and decrypt data.")
                        //Tasks
                        .build())
                .addArgument(Argument.builder()
                        .name("TaskName")
                        .dataType(DataTypeEnum.ALPHA_NUMERIC_STRING)
                        .description("The name of the task.")
                        .build())
                .addArgument(Argument.builder()
                        .name("Minutes")
                        .dataType(DataTypeEnum.INT)
                        .description("A number of minutes.")
                        .defaultValue("1440").build())
                .addArgument(Argument.builder()
                        .name("TimeOfDay")
                        .dataType(DataTypeEnum.STRING)
                        .description("The time to use. Format HH:mm, i.e. in 24h format, e.g. 22:55. The time is the time-zone of the machine running the instance.")
                        .build())
                .addArgument(Argument.builder()
                        .name("DayOfMonth")
                        .dataType(DataTypeEnum.INT)
                        .description("A day of month")
                        .build())
                .addArgument(Argument.builder()
                        .name("Request")
                        .dataType(DataTypeEnum.STRING)
                        .description("A request in the text format. E.g. echo hello")
                        .build())
                //Report
                .addArgument(Argument.builder()
                        .name("FromName")
                        .dataType(DataTypeEnum.ALPHA_NUMERIC_STRING)
                        .description("The personal name in the FROM field in an email.")
                        .defaultValue("@Expose")
                        .build())
                .addArgument(Argument.builder()
                        .name("Recipient")
                        .dataType(DataTypeEnum.ALPHA_NUMERIC_STRING)
                        .description("A mail address in the format recipient@domain.com.")
                        .build())
                .addArgument(Argument.builder()
                        .name("ForceHttps")
                        .dataType(DataTypeEnum.BOOLEAN)
                        .description("If true, http calls are redirected to https calls.")
                        .defaultValue("false")
                        .build())
                .addArgument(Argument.builder()
                        .name("Username")
                        .dataType(DataTypeEnum.STRING)
                        .description("A username.")
                        .build())
                .addArgument(Argument.builder()
                        .name("Password")
                        .dataType(DataTypeEnum.STRING)
                        .description("A password.")
                        .build())
                .addArgument(Argument.builder()
                        .name("QueueProducerName")
                        .dataType(DataTypeEnum.ALPHA_NUMERIC_STRING)
                        .description("Name of a queue producer, which sends messages to a queue such as AWS SQS.")
                        .build())
                .addArgument(Argument.builder()
                        .name("Message")
                        .dataType(DataTypeEnum.STRING)
                        .description("A message.")
                        .build())
                .addArgument(Argument.builder()
                        .name("TimeZone")
                        .dataType(DataTypeEnum.STRING)
                        .defaultValue("UTC")
                        .description("A time zone. E.g. UTC, America/New_York, Europe/Stockholm.")
                        .build());
    }


    private static void setUpMethods(API api) {
        api.expose(new NativeUtilMethods());
    }
}
