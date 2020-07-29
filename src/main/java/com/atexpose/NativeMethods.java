package com.atexpose;

import com.atexpose.dispatcher.IDispatcher;
import com.atexpose.dispatcher.logging.Logger;
import com.atexpose.dispatcher.logging.LoggerType;
import com.atexpose.dispatcher.logging.format.LogFormatterFactory;
import com.atexpose.dispatcher.logging.writer.LogWriterFactory;
import com.atexpose.dispatcherfactories.ScriptFileReaderFactory;
import io.schinzel.basicutils.Checker;
import io.schinzel.crypto.cipher.Aes256Gcm;
import io.schinzel.crypto.cipher.ICipher;
import io.schinzel.crypto.cipher.NoCipher;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.Accessors;

/**
 * The purpose of this class is to expose @Expose and allow string returns that contain status of
 * the operation
 * messages.
 * <p>
 * Created by schinzel on 2017-04-16.
 */
@SuppressWarnings("unused")
@Accessors(prefix = "m")
@AllArgsConstructor(access = AccessLevel.PRIVATE)
class NativeMethods {
    @Getter(AccessLevel.PACKAGE)
    private final AtExpose mAtExpose;


    static NativeMethods create(AtExpose atExpose) {
        return new NativeMethods(atExpose);
    }


    @Expose(
            arguments = {"FileName"},
            requiredAccessLevel = 3,
            requiredArgumentCount = 1,
            description = "Reads and executes the argument script file. Useful for setting up settings, scheduled tasks and so on.",
            labels = {"@Expose", "AtExpose"}
    )
    public String loadScriptFile(String fileName) {
        IDispatcher scriptFileReader = ScriptFileReaderFactory.create(fileName);
        this.getAtExpose().start(scriptFileReader, true);
        return "Script file '" + fileName + "' loaded.";
    }


    @Expose(
            arguments = {"DispatcherName", "LogFormatter", "LogWriter", "CryptoKey"},
            requiredAccessLevel = 3,
            description = "Adds an event logger to a dispatcher.",
            labels = {"@Expose", "AtExpose", "Logs"},
            requiredArgumentCount = 1
    )
    public String addEventLogger(String dispatcherName, String logFormatter, String logWriter, String cryptoKey) {
        return this.addLogger(dispatcherName, logFormatter, logWriter, cryptoKey, LoggerType.EVENT);
    }


    @Expose(
            arguments = {"DispatcherName", "LogFormatter", "LogWriter", "CryptoKey"},
            requiredAccessLevel = 3,
            description = "Adds an error logger to a dispatcher.",
            labels = {"@Expose", "AtExpose", "Logs"},
            requiredArgumentCount = 1
    )
    public String addErrorLogger(String dispatcherName, String logFormatter, String logWriter, String cryptoKey) {
        return this.addLogger(dispatcherName, logFormatter, logWriter, cryptoKey, LoggerType.ERROR);
    }


    private String addLogger(String dispatcherName, String logFormatter, String logWriter, String cryptoKey, LoggerType loggerType) {
        ICipher crypto = Checker.isEmpty(cryptoKey)
                ? new NoCipher()
                : new Aes256Gcm(cryptoKey);
        Logger logger = Logger.builder()
                .loggerType(LoggerType.EVENT)
                .logFormatter(LogFormatterFactory.get(logFormatter).create())
                .logWriter(LogWriterFactory.get(logWriter).create())
                .cipher(crypto)
                .build();
        this.getAtExpose().getDispatchers().get(dispatcherName).addLogger(logger);
        return "Dispatcher " + dispatcherName + " got an " + loggerType.name().toLowerCase() + " logger";
    }


    @Expose(
            description = "Shuts down the system",
            requiredAccessLevel = 3,
            aliases = {"close", "bye", "exit"},
            labels = {"@Expose"}
    )
    public synchronized String shutdown() {
        this.getAtExpose().shutdown();
        return "Shutting down...";
    }


    @Expose(
            arguments = {"DispatcherName"},
            requiredAccessLevel = 3,
            description = "Closes the argument dispatcher.",
            labels = {"@Expose"}
    )
    public String closeDispatcher(String name) {
        this.getAtExpose().closeDispatcher(name);
        return "Dispatcher " + name + " has been closed";
    }


    @Expose(
            description = "Returns the API.",
            requiredAccessLevel = 2,
            labels = {"@Expose"}
    )
    public String api() {
        return this.getAtExpose().getAPI().getState().getJson().toString();
    }


    @Expose(
            description = "Returns the current @Expose state.",
            requiredAccessLevel = 2,
            labels = {"@Expose"}
    )
    public String status() {
        return this.getAtExpose().getState().toString();
    }


}
