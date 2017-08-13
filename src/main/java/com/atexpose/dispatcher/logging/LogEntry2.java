package com.atexpose.dispatcher.logging;

import com.atexpose.dispatcher.parser.Request;
import com.atexpose.util.DateTimeStrings;
import com.google.common.collect.ImmutableMap;
import io.schinzel.basicutils.Checker;
import io.schinzel.basicutils.crypto.cipher.ICipher;
import lombok.Builder;
import lombok.Getter;
import lombok.val;

import java.time.Instant;
import java.util.List;
import java.util.Map;

@Builder
public class LogEntry2 {
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
    Map<String, String> getLogData(ICipher crypto) {
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
     * @param argumentNames  The names of the arguments
     * @param argumentValues The values of the arguments
     * @return The argument names and values formatted to a string
     */
    private static String argumentsToString(List<String> argumentNames, List<String> argumentValues) {
        //If there are no arguments
        if (Checker.isEmpty(argumentNames) && Checker.isEmpty(argumentValues)) {
            return "-";
        }
        StringBuilder sb = new StringBuilder();
        //Go through all arguments values
        for (int i = 0; i < argumentValues.size(); i++) {
            //If not is first argument
            if (i != 0) {
                sb.append(", ");
            }
            //If there are argument names
            if (!Checker.isEmpty(argumentNames)) {
                sb.append(argumentNames.get(i)).append("=");
            }
            String argumentValue = argumentValues.get(i);
            sb.append("\'").append(argumentValue).append("\'");
        }
        return sb.toString();
    }
}
