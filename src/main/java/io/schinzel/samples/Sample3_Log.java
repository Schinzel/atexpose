package io.schinzel.samples;

import com.atexpose.AtExpose;
import com.atexpose.dispatcher.IDispatcher;
import com.atexpose.dispatcher.logging.Logger;
import com.atexpose.dispatcher.logging.LoggerType;
import com.atexpose.dispatcher.logging.format.LogFormatterFactory;
import com.atexpose.dispatcher.logging.writer.LogWriterFactory;
import com.atexpose.dispatcher_factories.WebServerBuilder;
import io.schinzel.samples.auxiliary.MyClass;

/**
 * <p>
 * The purpose of this sample is to show how to add a logger
 * </p>
 * <p>
 * Request the below URL in a browser and observe the log in the terminal
 * </p>
 * <pre>
 * http://127.0.0.1:5555/api/doubleIt?Int=55
 * </pre>
 *
 * @author schinzel
 */
public class _Sample3_Log {
    public static void main(String[] args) {
        AtExpose.create()
                //Expose methods in a class
                .expose(MyClass.class)
                //Start a web server
                .start(getWebServer());
    }


    /**
     *
     * @return A web server
     */
    private static IDispatcher getWebServer() {
        return WebServerBuilder.create()
                .build()
                //Add a logger to web server
                .addLogger(getLogger());
    }


    /**
     *
     * @return A logger
     */
    private static Logger getLogger() {
        return Logger
                .builder()
                .loggerType(LoggerType.EVENT)
                .logFormatter(LogFormatterFactory.JSON.create())
                .logWriter(LogWriterFactory.SYSTEM_OUT.create())
                .build();

    }
}
