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

    NativeSetup() {
    }

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
                        .name("file_name")
                        .dataType(DataTypeEnum.STRING.getDataType())
                        .description("The name of a file.")
                        .build())
                .addArgument(Argument.builder()
                        .name("port")
                        .dataType(DataTypeEnum.INT.getDataType())
                        .description("The port to use.")
                        .defaultValue("5555")
                        .build())
                .addArgument(Argument.builder()
                        .name("web_server_dir")
                        .dataType(DataTypeEnum.STRING.getDataType())
                        .description("The name and path to the directory to expose with this web server.")
                        .defaultValue("web")
                        .build())
                .addArgument(Argument.builder()
                        .name("dispatcher_name")
                        .dataType(DataTypeEnum.ALPHA_NUMERIC_STRING.getDataType())
                        .description("Name of a dispatcher")
                        .build())
                //@Expose
                .addArgument(Argument.builder()
                        .name("channel")
                        .dataType(DataTypeEnum.ALPHA_NUMERIC_STRING.getDataType())
                        .description("Reads incoming requests and sends the responses")
                        .build())
                .addArgument(Argument.builder()
                        .name("access_level")
                        .dataType(DataTypeEnum.INT.getDataType())
                        .description("Sets which level of commands this dispatcher has access to.")
                        .defaultValue("1").build())
                .addArgument(Argument.builder()
                        .name("no_of_threads")
                        .dataType(DataTypeEnum.INT.getDataType())
                        .description("Number of threads")
                        .defaultValue("10")
                        .build())
                .addArgument(Argument.builder()
                        .name("timeout")
                        .dataType(DataTypeEnum.INT.getDataType())
                        .description("Timeout in milliseconds")
                        .defaultValue("300")
                        .build())
                .addArgument(Argument.builder()
                        .name("cache_max_age")
                        .dataType(DataTypeEnum.INT.getDataType())
                        .description("The cache max age in seconds as sent to browsers. Cache age is always set to zero for method calls.")
                        .defaultValue("1200")
                        .build())
                .addArgument(Argument.builder()
                        .name("use_cached_files")
                        .dataType(DataTypeEnum.BOOLEAN.getDataType())
                        .description("If to cache files read from drive in RAM for better performance.")
                        .defaultValue("true")
                        .build())
                .addArgument(Argument.builder()
                        .name("default_Page")
                        .dataType(DataTypeEnum.STRING.getDataType())
                        .description("Default page to load if a call to web server does not specify a specific file.")
                        .defaultValue("index.html")
                        .build())
                .addArgument(Argument.builder()
                        .name("force_default_Page")
                        .dataType(DataTypeEnum.BOOLEAN.getDataType())
                        .description("If true, all calls are redirected to the default page.")
                        .defaultValue("false")
                        .build())
                //Logging
                .addArgument(Argument.builder()
                        .name("log_writer")
                        .dataType(DataTypeEnum.ALPHA_NUMERIC_STRING.getDataType())
                        .description("Where the log entries will be written")
                        .defaultValue("system_out")
                        .build())
                .addArgument(Argument.builder()
                        .name("log_formatter")
                        .dataType(DataTypeEnum.ALPHA_NUMERIC_STRING.getDataType())
                        .description("The format of the log entries")
                        .defaultValue("json")
                        .build())
                .addArgument(Argument.builder()
                        .name("crypto_key")
                        .dataType(DataTypeEnum.ALPHA_NUMERIC_STRING.getDataType())
                        .description("Key used to encrypt and decrypt data.")
                        //Tasks
                        .build())
                .addArgument(Argument.builder()
                        .name("task_name")
                        .dataType(DataTypeEnum.ALPHA_NUMERIC_STRING.getDataType())
                        .description("The name of the task.")
                        .build())
                .addArgument(Argument.builder()
                        .name("minutes")
                        .dataType(DataTypeEnum.INT.getDataType())
                        .description("A number of minutes.")
                        .defaultValue("1440").build())
                .addArgument(Argument.builder()
                        .name("time_of_day")
                        .dataType(DataTypeEnum.STRING.getDataType())
                        .description("The time to use. Format HH:mm, i.e. in 24h format, e.g. 22:55. The time is the time-zone of the machine running the instance.")
                        .build())
                .addArgument(Argument.builder()
                        .name("day_of_month")
                        .dataType(DataTypeEnum.INT.getDataType())
                        .description("A day of month")
                        .build())
                .addArgument(Argument.builder()
                        .name("request")
                        .dataType(DataTypeEnum.STRING.getDataType())
                        .description("A request in the text format. E.g. echo hello")
                        .build())
                //Report
                .addArgument(Argument.builder()
                        .name("from_name")
                        .dataType(DataTypeEnum.ALPHA_NUMERIC_STRING.getDataType())
                        .description("The personal name in the FROM field in an email.")
                        .defaultValue("@Expose")
                        .build())
                .addArgument(Argument.builder()
                        .name("recipient")
                        .dataType(DataTypeEnum.ALPHA_NUMERIC_STRING.getDataType())
                        .description("A mail address in the format recipient@domain.com.")
                        .build())
                .addArgument(Argument.builder()
                        .name("force_https")
                        .dataType(DataTypeEnum.BOOLEAN.getDataType())
                        .description("If true, http calls are redirected to https calls.")
                        .defaultValue("false")
                        .build())
                .addArgument(Argument.builder()
                        .name("username")
                        .dataType(DataTypeEnum.STRING.getDataType())
                        .description("A username.")
                        .build())
                .addArgument(Argument.builder()
                        .name("password")
                        .dataType(DataTypeEnum.STRING.getDataType())
                        .description("A password.")
                        .build())
                .addArgument(Argument.builder()
                        .name("queue_producer_name")
                        .dataType(DataTypeEnum.ALPHA_NUMERIC_STRING.getDataType())
                        .description("Name of a queue producer, which sends messages to a queue such as AWS SQS.")
                        .build())
                .addArgument(Argument.builder()
                        .name("message")
                        .dataType(DataTypeEnum.STRING.getDataType())
                        .description("A message.")
                        .build())
                .addArgument(Argument.builder()
                        .name("time_zone")
                        .dataType(DataTypeEnum.STRING.getDataType())
                        .defaultValue("UTC")
                        .description("A time zone. E.g. UTC, America/New_York, Europe/Stockholm.")
                        .build());
    }


    private static void setUpMethods(API api) {
        api.expose(new NativeUtilMethods());
    }
}
