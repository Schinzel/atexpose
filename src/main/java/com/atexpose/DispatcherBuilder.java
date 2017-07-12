package com.atexpose;

import com.amazonaws.regions.Regions;
import com.atexpose.api.API;
import com.atexpose.dispatcher.Dispatcher;
import com.atexpose.dispatcher.channels.SqsChannel;
import com.atexpose.dispatcher.parser.JsonRpcParser;
import com.atexpose.dispatcher.wrapper.CsvWrapper;
import com.atexpose.util.sqs.SqsConsumer;
import io.schinzel.basicutils.Checker;
import io.schinzel.basicutils.collections.namedvalues.NamedValues;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.experimental.Accessors;

/**
 * The purpoes of this class is to build a class that consumes message from a SQS queue.
 * <p>
 * Created by schinzel on 2017-07-05.
 */
@Accessors(prefix = "m")
@AllArgsConstructor
class DispatcherBuilder {
    private final API mAPI;
    private final NamedValues<Dispatcher> mDispatchers;


    @Builder(builderClassName = "SqsConsumerBuilder", builderMethodName = "sqsConsumerBuilder", buildMethodName = "start")
    Dispatcher newSqsConsumer(String name, String awsAccessKey, String awsSecretKey, Regions region,
                              String queueUrl, int accessLevel, int noOfThreads) {
        if(Checker.isEmpty(name)){
            name = "SqsConsumer";
        }
        SqsConsumer sqsReceiver = SqsConsumer.builder()
                .awsAccessKey(awsAccessKey)
                .awsSecretKey(awsSecretKey)
                .region(region)
                .queueUrl(queueUrl)
                .build();
        Dispatcher dispatcher = Dispatcher.builder()
                .name(name)
                .channel(new SqsChannel(sqsReceiver))
                .parser(new JsonRpcParser())
                .wrapper(new CsvWrapper())
                .accessLevel(accessLevel)
                .noOfThreads(noOfThreads)
                .api(mAPI)
                .build();
        mDispatchers.add(dispatcher);
        return dispatcher.commenceMessaging(false);
    }


}
