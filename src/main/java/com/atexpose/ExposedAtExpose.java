package com.atexpose;

import com.atexpose.dispatcher.logging.Logger;
import com.atexpose.dispatcher.logging.LoggerType;
import com.atexpose.dispatcher.logging.format.LogFormatterFactory;
import com.atexpose.dispatcher.logging.writer.LogWriterFactory;
import io.schinzel.basicutils.Checker;
import io.schinzel.basicutils.crypto.cipher.Aes256Gcm;
import io.schinzel.basicutils.crypto.cipher.ICipher;
import io.schinzel.basicutils.crypto.cipher.NoCipher;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.Accessors;
import org.apache.commons.lang3.StringUtils;
import org.json.JSONObject;

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
class ExposedAtExpose {
    @Getter(AccessLevel.PACKAGE)
    private final AtExpose mAtExpose;


    static ExposedAtExpose create(AtExpose atExpose) {
        return new ExposedAtExpose(atExpose);
    }


    @Expose(
            arguments = {"FileName"},
            requiredAccessLevel = 3,
            requiredArgumentCount = 1,
            description = "Reads and executes the argument script file. Useful for setting up settings, scheduled tasks and so on.",
            labels = {"@Expose", "AtExpose"}
    )
    public String loadScriptFile(String fileName) {
        this.getAtExpose().loadScriptFile(fileName);
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


    String addLogger(String dispatcherName, String logFormatter, String logWriter, String cryptoKey, LoggerType loggerType) {
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
            arguments = {"DispatcherName"},
            requiredAccessLevel = 3,
            description = "Removes all loggers from a dispatcher.",
            labels = {"@Expose", "AtExpose", "Logs"},
            requiredArgumentCount = 1
    )
    public String removeAllLoggers(String dispatcherName) {
        this.getAtExpose().removeAllLoggers(dispatcherName);
        return "Removed all loggers from dispatcher '" + dispatcherName + "'";
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
    public JSONObject api() {
        return this.getAtExpose().getAPI().getState().getJson();
    }


    @Expose(
            description = "Returns the current @Expose state.",
            requiredAccessLevel = 2,
            labels = {"@Expose"}
    )
    public JSONObject status() {
        return this.getAtExpose().getState().getJson();
    }


    @Expose(
            arguments = {"QueueProducerName", "Message"},
            requiredAccessLevel = 3,
            description = "Sends the argument message to a queue, e.g. AWS SQS. using the argument queue producer. "
                    + "The producer has to have been added to @Expose using method addQueueProducer.",
            labels = {"@Expose"}
    )
    public String sendToQueue(String queueProducerName, String message) {
        this.getAtExpose().sendToQueue(queueProducerName, message);
        return "Message sent. SqsSender: '" + queueProducerName + ". Message: '"
                + StringUtils.abbreviate(message, 50) + "'";
    }


}
