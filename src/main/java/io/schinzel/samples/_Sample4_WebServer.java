package io.schinzel.samples;

import com.atexpose.AtExpose;
import com.atexpose.api.Argument;
import com.atexpose.api.data_types.DataTypeEnum;
import com.atexpose.dispatcher.IDispatcher;
import com.atexpose.dispatcher.logging.Logger;
import com.atexpose.dispatcher.logging.LoggerType;
import com.atexpose.dispatcher.logging.format.LogFormatterFactory;
import com.atexpose.dispatcher.logging.writer.LogWriterFactory;
import com.atexpose.dispatcher_factories.WebServerBuilder;
import io.schinzel.samples.auxiliary.ClassWithCustomArgument;
import io.schinzel.samples.auxiliary.MyClass;

/**
 * The purpose of this sample is to show a more advanced set up.
 * <p>
 * In the command line interface try:
 * getPrice
 * setPrice 55
 * getPrice
 * <p>
 * Load the below url in a browser:
 * http://127.0.0.1:5555/mypage.html
 *
 * @author schinzel
 */
public class _Sample3_WebServer {


    public static void main(String[] args) {
        AtExpose.create()
                // Add a custom argument "Price"
                .addArgument(Argument.builder()
                        .name("Price")
                        .dataType(DataTypeEnum.INT.getDataType())
                        .description("A price")
                        .build())
                //Expose static methods in a class
                .expose(MyClass.class)
                //Expose an instance
                .expose(new ClassWithCustomArgument())
                //Start web server
                .start(getWebServer())
                //Start a command line interface
                .startCLI();
    }


    private static IDispatcher getWebServer() {
        return WebServerBuilder.create()
                //Set the dir from which requested file will be read
                .webServerDir("web/sample3")
                //Disable RAM cache so that changes to files made kicks through without restarting the web server
                .cacheFilesInRAM(false)
                //Set custom 404 page
                .fileName404Page("404page.html")
                //Build web server
                .build()
                //Add a logger
                .addLogger(getEventLogger());
    }


    private static Logger getEventLogger() {
        return Logger.builder()
                .loggerType(LoggerType.EVENT)
                .logFormatter(LogFormatterFactory.JSON.create())
                .logWriter(LogWriterFactory.SYSTEM_OUT.create())
                .build();
    }
}
