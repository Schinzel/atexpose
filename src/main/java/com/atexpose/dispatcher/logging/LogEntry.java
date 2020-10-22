package com.atexpose.dispatcher.logging;

import com.atexpose.util.DateTimeStrings;
import com.google.common.collect.ImmutableMap;
import io.schinzel.basicutils.thrower.Thrower;
import io.schinzel.crypto.cipher.ICipher;
import lombok.*;

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
@ToString
public class LogEntry implements ILogEntry {
    public static final String KEY_CALL_TIME = "call_time_utc";
    public static final String KEY_RESPONSE = "response";
    public static final String KEY_THREAD = "thread";
    public static final String KEY_READ_TIME = "read_time_in_ms";
    public static final String KEY_EXEC_TIME = "exec_time_in_ms";
    public static final String KEY_WRITE_TIME = "write_time_in_ms";
    public static final String KEY_SENDER = "sender";
    public static final String KEY_REQUEST_STRING = "request_string";
    public static final String KEY_FILENAME = "filename";
    public static final String KEY_METHOD_NAME = "method_name";
    public static final String KEY_ARGUMENTS = "arguments";

    final @NonNull private Instant timeOfIncomingRequest;
    @Getter final private boolean isError;
    final @NonNull private String requestString;
    final @NonNull String response;
    final int threadNumber;
    final long requestReadTime;
    final long execTime;
    final long responseWriteTime;
    final @NonNull String senderInfo;
    final @NonNull List<String> argNames;
    final @NonNull List<String> argValues;
    final boolean isFileRequest;
    final @NonNull String fileName;
    final @NonNull String methodName;


    public Map<String, String> getLogData(@NonNull ICipher crypto) {
        List<String> encryptedArguments = argValues.stream()
                .map(crypto::encrypt)
                .collect(Collectors.toList());
        String arguments = argumentsToString(argNames, encryptedArguments);
        val logDataBuilder = ImmutableMap.<String, String>builder()
                .put(LogEntry.KEY_CALL_TIME, DateTimeStrings.getDateTimeUTC(timeOfIncomingRequest))
                .put(LogEntry.KEY_THREAD, String.valueOf(threadNumber))
                .put(LogEntry.KEY_READ_TIME, String.valueOf(requestReadTime))
                .put(LogEntry.KEY_EXEC_TIME, String.valueOf(execTime))
                .put(LogEntry.KEY_WRITE_TIME, String.valueOf(responseWriteTime))
                .put(LogEntry.KEY_SENDER, senderInfo)
                .put(LogEntry.KEY_REQUEST_STRING, crypto.encrypt(requestString))
                .put(LogEntry.KEY_RESPONSE, response);
        //If request is a file request
        if (isFileRequest) {
            logDataBuilder.put(LogEntry.KEY_FILENAME, fileName);
        } else {
            logDataBuilder
                    .put(LogEntry.KEY_METHOD_NAME, methodName)
                    .put(KEY_ARGUMENTS, arguments);

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
