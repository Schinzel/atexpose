package com.atexpose.dispatcher.parser;

import lombok.Builder;
import lombok.Getter;
import lombok.experimental.Accessors;

import java.util.Collections;
import java.util.List;

/**
 * An instance of this object is an incoming request.
 * <p>
 * Created by schinzel on 2017-04-25.
 */
@Builder
@Accessors(prefix = "m")
public class Request {
    @Getter @Builder.Default private String mMethodName = "";
    @Getter @Builder.Default private List<String> mArgumentNames = Collections.emptyList();
    @Getter @Builder.Default private List<String> mArgumentValues = Collections.emptyList();
    @Getter @Builder.Default private String mFileName = "";
    @Getter @Builder.Default private boolean mFileRequest = false;


}
