package com.atexpose.dispatcher.wrapper.webresponse;

import io.schinzel.basicutils.collections.keyvalues.KeyValues;


/**
 * The purpose of this class to hold a collection of FileTypes.
 * <p>
 * Created by Schinzel on 2017-03-06.
 */
class FileTypes {
    /** A collection that maps file-extensions to property objects **/
    private KeyValues<FileType> mFileProps = KeyValues.create();

    private static class Holder {
        public static FileTypes INSTANCE = new FileTypes();
    }


    /**
     *
     * @return A singleton instance.
     */
    static FileTypes getInstance() {
        return Holder.INSTANCE;
    }


    private FileTypes() {
        mFileProps
                //Images
                .add(new FileType("ico", "image/ico", false))
                .add(new FileType("png", "image/png", false))
                .add(new FileType("jpg", "image/jpg", false))
                .add(new FileType("jpeg", "image/jpeg", false))
                .add(new FileType("gif", "image/gif", false))
                .add(new FileType("svg", "image/svg+xml", false))
                //TextParser files
                .add(new FileType("html", "text/html", true))
                .addAlias("html", "htm")
                .add(new FileType("css", "text/css", true))
                .add(new FileType("js", "text/javascript", true))
                .add(new FileType("txt", "text/plain", true))
                .add(new FileType("json", "application/json", true))
                //Other
                .add(new FileType("map", "text/plain", false))
                .add(new FileType("pdf", "application/pdf", false))
                .add(new FileType("woff", "application/font-woff", false))
                .add(new FileType("woff2", "application/font-woff2", false));
    }


    /**
     * @param fileExtension The extension of the file. E.g. "txt"
     * @return The FileType for the argument extension.
     */
    FileType getProps(String fileExtension) {
        return mFileProps.get(fileExtension);
    }
}
