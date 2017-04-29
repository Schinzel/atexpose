package com.atexpose.dispatcher.parser;

import io.schinzel.basicutils.EmptyObjects;
import lombok.Builder;
import lombok.Getter;
import lombok.experimental.Accessors;

/**
 * An instance of this object is an incoming request.
 * <p>
 * Created by schinzel on 2017-04-25.
 */
@Builder
@Accessors(prefix = "m")
public class Request {
    @Getter @Builder.Default private String mMethodName = "";
    @Getter @Builder.Default private String[] mArgumentNames = EmptyObjects.EMPTY_STRING_ARRAY;
    @Getter @Builder.Default private String[] mArgumentValues = EmptyObjects.EMPTY_STRING_ARRAY;
    @Getter @Builder.Default private String mFileName = "";
    @Getter @Builder.Default private boolean mFileRequest = false;


}
