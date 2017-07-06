package io.schinzel.samples.sample_sqs_consumer;

import com.atexpose.Expose;

/**
 * This is a sample class that does a heavy background job.
 *
 * Created by schinzel on 2017-07-06.
 */
public class JobClass {

    @Expose(requiredAccessLevel = 1)
    String doHeavyBackgroundJob() {
        return "Phew, all done with heavy job";
    }
}
