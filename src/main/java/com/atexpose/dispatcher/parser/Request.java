package com.atexpose.dispatcher.parser;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;
import lombok.experimental.Accessors;

import java.util.Collections;
import java.util.List;

/**
 * An instance of this object is an incoming request.
 * <p>
 * Created by schinzel on 2017-04-25.
 */
@Builder
@ToString
@Accessors(prefix = "m")
public class Request {
    public static final Request EMPTY = Request.builder().build();

    @Getter
    @Builder.Default
    private final String mMethodName = "";
    @Getter
    @Builder.Default
    private final List<String> mArgumentNames = Collections.emptyList();
    @Getter
    @Builder.Default
    private final List<String> mArgumentValues = Collections.emptyList();
    @Getter
    @Builder.Default
    private final String mFileName = "";
    @Getter
    @Builder.Default
    private final boolean mFileRequest = false;

}
