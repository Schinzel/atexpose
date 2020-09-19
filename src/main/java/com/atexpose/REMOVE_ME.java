package com.atexpose;

import com.atexpose.api.Argument;
import com.atexpose.api.datatypes.ClassDT;
import com.atexpose.api.datatypes.DataTypeEnum;
import com.atexpose.dispatcher.logging.Logger;
import com.atexpose.dispatcher.logging.LoggerType;
import com.atexpose.dispatcher.logging.format.LogFormatterFactory;
import com.atexpose.dispatcher.logging.writer.LogWriterFactory;
import com.atexpose.dispatcherfactories.WebServerBuilder;

public class REMOVE_ME {
    public static void main(String[] args) {
        AtExpose.create()
                .addDataType(RemoveMeVar.class)
                .addDataType(RemoveMeEnum.class)
                .addArgument(Argument.builder()
                        .name("test_arg")
                        .dataType(DataTypeEnum.STRING.getDataType())
                        .allowedCharsRegEx("[a-z0-9_@.-]*")
                        .description("")
                        .build())
                .addArgument(Argument.builder()
                        .name("test_var")
                        .dataType(new ClassDT<>(RemoveMeVar.class))
                        .description("")
                        .build())
                .addArgument(Argument.builder()
                        .name("test_enum")
                        .dataType(new ClassDT<>(RemoveMeEnum.class))
                        .defaultValue('"' + RemoveMeEnum.FIRST.name() + '"')
                        .description("")
                        .build())
                .expose(RemoveMeClass.class)
                .start(WebServerBuilder.create()
                        .webServerDir("remove_me")
                        .cacheFilesInRAM(false)
                        .build()
                        .addLogger(getEventLogger()))
                .startCLI()
                .generateJavaScriptClient("src/main/resources/remove_me/ServerCaller.js");
        System.out.println("System started!");

    }

    private static Logger getEventLogger() {
        return Logger.builder()
                .loggerType(LoggerType.EVENT)
                .logFormatter(LogFormatterFactory.JSON.create())
                .logWriter(LogWriterFactory.SYSTEM_OUT.create())
                .build();
    }

}
