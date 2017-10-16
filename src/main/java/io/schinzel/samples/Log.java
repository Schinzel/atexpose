package io.schinzel.samples;

import com.atexpose.AtExpose;
import com.atexpose.dispatcher.IDispatcher;
import com.atexpose.dispatcher.logging.Logger;
import com.atexpose.dispatcher.logging.LoggerType;
import com.atexpose.dispatcher.logging.format.LogFormatterFactory;
import com.atexpose.dispatcher.logging.writer.LogWriterFactory;
import com.atexpose.dispatcherfactories.CliFactory;
import com.atexpose.dispatcherfactories.WebServerBuilder;
import io.schinzel.samples.auxiliary.MyClass;
import io.schinzel.samples.auxiliary.MyObject;

/**
 * This sample adds logs to dispatchers
 * <p>
 * Request the below URL in a browser and observe the log in the terminal
 * http://127.0.0.1:5555/call/doubleIt?Int=55
 * <p>
 * Add a log
 */
public class Log {
    public static void main(String[] args) {
        AtExpose.create()
                //Expose static methods in a class
                .expose(MyClass.class)
                //Expose an instance
                .expose(new MyObject())
                //Start a web server
                .start(getWebServer())
                //Start command line interface
                .start(CliFactory.create());
    }


    private static IDispatcher getWebServer() {
        return WebServerBuilder.create()
                .build()
                //Add a logger to web server
                .addLogger(getWebServerLogger());
    }


    private static Logger getWebServerLogger() {
        return Logger
                .builder()
                .loggerType(LoggerType.EVENT)
                .logFormatter(LogFormatterFactory.JSON.create())
                .logWriter(LogWriterFactory.SYSTEM_OUT.create())
                .build();

    }
}
