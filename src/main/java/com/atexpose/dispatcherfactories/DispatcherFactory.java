package com.atexpose.dispatcherfactories;

import com.amazonaws.regions.Regions;
import com.atexpose.dispatcher.Dispatcher;
import com.atexpose.dispatcher.IDispatcher;
import com.atexpose.dispatcher.channels.CommandLineChannel;
import com.atexpose.dispatcher.channels.ScriptFileChannel;
import com.atexpose.dispatcher.channels.SqsChannel;
import com.atexpose.dispatcher.parser.JsonRpcParser;
import com.atexpose.dispatcher.parser.TextParser;
import com.atexpose.dispatcher.wrapper.CsvWrapper;
import com.atexpose.util.sqs.SqsConsumer;
import io.schinzel.basicutils.Checker;
import lombok.Builder;

public class DispatcherFactory {


    @Builder(builderMethodName = "cliBuilder", builderClassName = "CliBuilder")
    static IDispatcher newCli(String name) {
        return Dispatcher.builder()
                .name("CommandLine")
                .accessLevel(3)
                .isSynchronized(false)
                .channel(new CommandLineChannel())
                .parser(new TextParser())
                .wrapper(new CsvWrapper())
                .noOfThreads(1)
                .build();
    }


    @Builder(builderMethodName = "sqsConsumerBuilder", builderClassName = "SqsConsumerBuilder")
    static IDispatcher newSqsConsumer(String name, String awsAccessKey, String awsSecretKey, Regions region,
                                      String queueUrl, int accessLevel, int noOfThreads) {
        if (Checker.isEmpty(name)) {
            name = "SqsConsumer";
        }
        SqsConsumer sqsReceiver = SqsConsumer.builder()
                .awsAccessKey(awsAccessKey)
                .awsSecretKey(awsSecretKey)
                .region(region)
                .queueUrl(queueUrl)
                .build();
        return Dispatcher.builder()
                .name(name)
                .channel(new SqsChannel(sqsReceiver))
                .parser(new JsonRpcParser())
                .wrapper(new CsvWrapper())
                .accessLevel(accessLevel)
                .noOfThreads(noOfThreads)
                .build();
    }


    @Builder(builderMethodName = "scriptFileReader", builderClassName = "ScriptFileReaderBuilder")
    static IDispatcher newScriptFileReader(String fileName) {
        return Dispatcher.builder()
                .name("ScriptFile")
                .accessLevel(3)
                .isSynchronized(true)
                .channel(new ScriptFileChannel(fileName))
                .parser(new TextParser())
                .wrapper(new CsvWrapper())
                .noOfThreads(1)
                .build();
    }


}

