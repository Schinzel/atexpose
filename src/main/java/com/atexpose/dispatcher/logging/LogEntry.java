package com.atexpose.dispatcher.logging;

import com.atexpose.dispatcher.parser.Request;
import com.atexpose.util.DateTimeStrings;
import com.google.common.collect.ImmutableMap;
import io.schinzel.basicutils.Checker;
import io.schinzel.basicutils.Thrower;
import io.schinzel.basicutils.crypto.cipher.ICipher;
import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;
import lombok.val;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * The purpose of this class is to hold the data for one log entry that will end upp in zero, one
 * or more logs.
 */

@Builder
public class LogEntry implements ILogEntry {
    final private Instant timeOfIncomingRequest;
    @Getter final private boolean isError;
    final private String requestString;
    final String response;
    final int threadNumber;
    final long requestReadTime;
    final long execTime;
    final long responseWriteTime;
    final Request request;
    final String senderInfo;


    /**
     * @return
     */
    public Map<String, String> getLogData(@NonNull ICipher crypto) {
        List<String> argValues = request.getArgumentValues();
        //Encrypt argument values
        if (!Checker.isEmpty(argValues)) {
            for (int i = 0; i < argValues.size(); i++) {
                argValues.set(i, crypto.encrypt(argValues.get(i)));
            }
        }
        String arguments = argumentsToString(request.getArgumentNames(), argValues);
        val logDataBuilder = ImmutableMap.<String, String>builder()
                .put("call_time_utc", DateTimeStrings.getDateTimeUTC(timeOfIncomingRequest))
                .put("response", response)
                .put("thread", String.valueOf(threadNumber))
                .put("read_time_in_ms", String.valueOf(requestReadTime))
                .put("exec_time_in_ms", String.valueOf(execTime))
                .put("write_time_in_ms", String.valueOf(responseWriteTime))
                .put("sender", senderInfo)
                .put("request_string", crypto.encrypt(requestString));
        //If request is a file request
        if (request.isFileRequest()) {
            logDataBuilder.put("filename", request.getFileName());
        } else {
            logDataBuilder.put("method_name", request.getMethodName()).put("arguments", arguments);
        }
        return logDataBuilder.build();
    }


    /**
     * The argument names and values as a string.
     * Example
     *
     * @param argNames  The names of the arguments
     * @param argValues The values of the arguments
     * @return The argument names and values formatted to a string
     */
    static String argumentsToString(@NonNull List<String> argNames, @NonNull List<String> argValues) {
        //If there are no argument values
        if (argNames.isEmpty() && argValues.isEmpty()) {
            return "-";
        }
        Thrower.throwIfTrue(!argNames.isEmpty() && argValues.isEmpty())
                .message("Illegal state. There cannot be argument names but no argument values.");
        Thrower.throwIfTrue(!argNames.isEmpty() && argNames.size() != argValues.size())
                .message("Illegal state. The number argument names and values must be the same.");
        return argNames.isEmpty() ?
                argValues.stream()
                        .map(value -> "'" + value + "'")
                        .collect(Collectors.joining(", "))
                :
                IntStream.range(0, argNames.size())
                        .mapToObj(i -> argNames.get(i) + "='" + argValues.get(i) + "'")
                        .collect(Collectors.joining(", "));
    }
}
