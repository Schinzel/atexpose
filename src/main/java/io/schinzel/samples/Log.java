package io.schinzel.samples;

import com.atexpose.AtExpose;
import com.atexpose.dispatcher.logging.Logger;
import com.atexpose.dispatcher.logging.LoggerType;
import com.atexpose.dispatcher.logging.format.LogFormatterFactory;
import com.atexpose.dispatcher.logging.writer.LogWriterFactory;
import io.schinzel.samples.auxiliary.MyClass;
import io.schinzel.samples.auxiliary.MyObject;

/**
 * This sample adds logs to dispatchers
 * <p>
 * Request the below URL in a browser and observe the log in the terminal
 * http://127.0.0.1:5555/call/doubleIt?Int=55
 *
 * Add a log
 */
public class Log {
    public static void main(String[] args) {
        AtExpose atExpose = AtExpose.create();
        atExpose.getAPI()
                //Expose static methods in a class
                .expose(MyClass.class)
                //Expose an instance
                .expose(new MyObject());
        atExpose.startCLI()
                //Start a web server
                .getWebServerBuilder()
                .startWebServer()
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
