package com.atexpose.dispatcher_factories;

import com.amazonaws.regions.Regions;
import com.atexpose.dispatcher.Dispatcher;
import com.atexpose.dispatcher.IDispatcher;
import com.atexpose.dispatcher.channels.SqsChannel;
import com.atexpose.dispatcher.parser.JsonRpcParser;
import com.atexpose.dispatcher.wrapper.CsvWrapper;
import com.atexpose.util.sqs.SqsConsumer;
import io.schinzel.basicutils.Checker;
import lombok.Builder;

public class SqsConsumerFactory {
    SqsConsumerFactory(){}

    @Builder
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
}
