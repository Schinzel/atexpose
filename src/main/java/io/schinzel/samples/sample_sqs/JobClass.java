package io.schinzel.samples.sample_sqs;

import com.atexpose.Expose;
import io.schinzel.basicutils.Sandman;

/**
 * This is a sample class that does simulates doing a heavy background job.
 * <p>
 * Created by schinzel on 2017-07-06.
 */
public class JobClass {

    @Expose(
            requiredAccessLevel = 1,
            arguments = {"Int"}
    )
    String doHeavyBackgroundJob(int count) {
        Sandman.snoozeSeconds(1);
        return "Phew, all done with heavy job " + count;
    }
}
