package com.atexpose.dispatcher.wrapper.webresponse;

import io.schinzel.basicutils.collections.keyvalues.IValueKey;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.Accessors;

/**
 * The purpose of this class to hold properties for a file type. For example a gif-file.
 * <p>
 * Created by Schinzel on 2017-03-06.
 */
@AllArgsConstructor
@Accessors(prefix = "m")
class FileType implements IValueKey {
    /** The extension for this file **/
    @Getter(AccessLevel.PACKAGE)
    private final String mExtension;
    /** The HTTP header content type. **/
    @Getter(AccessLevel.PACKAGE)
    private final String mHeaderContentType;
    /** The file type. E.g. text file. **/
    @Getter(AccessLevel.PACKAGE)
    private final boolean mTextFile;


    @Override
    public String getKey() {
        return this.getExtension();
    }
}
