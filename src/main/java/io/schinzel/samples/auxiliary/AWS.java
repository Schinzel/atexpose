package io.schinzel.samples.auxiliary;

import io.schinzel.basicutils.configvar.ConfigVar;

/**
 * Holds AWS credentials and other data.
 * <p>
 * Created by schinzel on 2017-07-07.
 */
public class AWS {
    public static final String ACCESS_KEY = ConfigVar.create(".env").getValue("AWS_SQS_ACCESS_KEY");
    public static final String SECRET_KEY = ConfigVar.create(".env").getValue("AWS_SQS_SECRET_KEY");
    public static final String QUEUE_NAME = "my_first_queue";

}
