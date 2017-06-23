package com.atexpose.dispatcher.wrapper;

import com.atexpose.MyProperties;
import com.atexpose.util.EncodingUtil;
import com.atexpose.util.FileRW;
import com.atexpose.util.httpresponse.*;
import io.schinzel.basicutils.Checker;
import io.schinzel.basicutils.Thrower;
import io.schinzel.basicutils.collections.Cache;
import io.schinzel.basicutils.state.State;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.experimental.Accessors;
import org.json.JSONObject;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * This wrapper handles two types of responses:
 * 1) JSON responses
 * 2) file responses (image, html, js and so on)
 * <p>
 * The file responses are divided into two groups:
 * 1) Text files. Examples: html and text.
 * 2) Static files. Examples: jpg and pdf.
 * <p>
 * Text files support two types of server side includes
 * 1) Files <!--#include file="header.html" -->
 * 2) Variables <!--#echo var="my_var" -->
 * The format is according to SSI: https://en.wikipedia.org/wiki/Server_Side_Includes
 * First are files in included and after that the variables are inserted in the resulting file.
 *
 * @author Schinzel
 */
@Accessors(prefix = "m")
public class WebWrapper implements IWrapper {
    //Pattern for server side variables. Example: <!--#echo var="my_var" -->
    private static final Pattern VARIABLE_PLACEHOLDER_PATTERN = Pattern.compile("<!--#echo var=\"([a-zA-Z1-9_]{3,25})\" -->");
    //Pattern for server side include files. Example: <!--#include file="header.html" -->
    private static final Pattern INCLUDE_FILE_PATTERN = Pattern.compile("<!--#include file=\"([\\w,/]+\\.[A-Za-z]{2,4})\" -->");
    /** The default to return if no page was specified. */
    private static final String DEFAULT_PAGE = "index.html";
    /** Where the files to server resides on the hard drive **/
    private final String mWebServerDir;
    /** Browser cache age instruction. **/
    private final int mBrowserCacheMaxAge;
    private final Map<String, String> mServerSideVariables;
    //If true, files read - e.g. HTML files - will be cached in RAM.
    private boolean mFilesCacheOn = true;
    @Getter(AccessLevel.PACKAGE)
    private Map<String, String> mCustomResponseHeaders = new HashMap<>();
    @Getter(AccessLevel.PACKAGE)
    private Cache<String, byte[]> mFilesCache;


    @Builder
    WebWrapper(String webServerDir, int browserCacheMaxAge, boolean cacheFilesInRam,
               Map<String, String> serverSideVariables, Map<String, String> responseHeaders) {
        //If the last char is not a file separator, then add it
        mWebServerDir = (!webServerDir.endsWith(MyProperties.FILE_SEPARATOR)) ?
                webServerDir + MyProperties.FILE_SEPARATOR : webServerDir;
        mBrowserCacheMaxAge = browserCacheMaxAge;
        mFilesCacheOn = cacheFilesInRam;
        mServerSideVariables = (serverSideVariables != null)
                ? serverSideVariables
                : Collections.emptyMap();
        Thrower.throwIfVarOutsideRange(browserCacheMaxAge, "browserCacheMaxAge", 0, 604800);
        mCustomResponseHeaders = (responseHeaders != null)
                ? responseHeaders
                : Collections.emptyMap();
        mFilesCache = new Cache<>();
    }


    @Override
    public String wrapResponse(String methodReturn) {
        return HttpResponseString.builder()
                .body(methodReturn)
                .customHeaders(this.getCustomResponseHeaders())
                .build()
                .getResponse();
    }


    @Override
    public String wrapError(String error) {
        return HttpResponse500.builder()
                .body(error)
                .customHeaders(this.getCustomResponseHeaders())
                .build()
                .getResponse();
    }


    @Override
    public byte[] wrapFile(String requestedFile) {
        String filename = this.getActualFilename(requestedFile);
        return FileUtil.isTextFile(filename)
                ? this.getTextFileHeaderAndContent(filename)
                : this.getStaticFileHeaderAndContent(filename);
    }


    @Override
    public String wrapJSON(JSONObject response) {
        return HttpResponseJson.builder()
                .body(response)
                .customHeaders(this.getCustomResponseHeaders())
                .build()
                .getResponse();
    }


    /**
     * @param filename The name of the file
     * @return The content of argument file including HTTP headers.
     */
    byte[] getTextFileHeaderAndContent(String filename) {
        //Get the text file
        byte[] abFileContent = this.getTextFileContent(filename);
        //If there was no such file
        if (abFileContent == null) {
            return HttpResponse404.builder()
                    .customHeaders(this.getCustomResponseHeaders())
                    .filenameMissingFile(filename)
                    .build()
                    .getResponse();
        } else {
            //Add server side variables
            abFileContent = WebWrapper.setServerSideVariables(abFileContent, mServerSideVariables);
            return HttpResponseFile.builder()
                    .body(abFileContent)
                    .customHeaders(this.getCustomResponseHeaders())
                    .filename(filename)
                    .build()
                    .getResponse();
        }
    }


    /**
     * @param filename The name of the file to return
     * @return The content of the argument file. Null if there was no such file.
     */
    byte[] getTextFileContent(String filename) {
        //If is to use cache AND there is the argument file is cached
        if (mFilesCacheOn && mFilesCache.has(filename)) {
            //Return cached file
            return mFilesCache.get(filename);
        }
        //If file does not exist
        if (!FileRW.fileExists(filename)) {
            return null;
        }
        //Read file
        byte[] abFileContent = FileRW.readFileAsByteArray(filename);
        //Add server side include files
        abFileContent = WebWrapper.setServerIncludeFiles(abFileContent, mWebServerDir);
        //If is to use file cache
        if (mFilesCacheOn) {
            //Add file content to cache
            mFilesCache.put(filename, abFileContent);
        }
        return abFileContent;
    }


    /**
     * @param filename The name of the file
     * @return The content of argument file including HTTP headers.
     */
    byte[] getStaticFileHeaderAndContent(String filename) {
        byte[] abFileContent;
        //If is to use cache AND the argument file is cached
        if (mFilesCacheOn && mFilesCache.has(filename)) {
            //Return cached file
            return mFilesCache.get(filename);
        }
        //If file doesn't exists
        if (!FileRW.fileExists(filename)) {
            return HttpResponse404.builder()
                    .customHeaders(this.getCustomResponseHeaders())
                    .filenameMissingFile(filename)
                    .build()
                    .getResponse();
        } else {
            abFileContent = FileRW.readFileAsByteArray(filename);
            byte[] abFileHeaderAndContent = HttpResponseFile.builder()
                    .body(abFileContent)
                    .customHeaders(this.getCustomResponseHeaders())
                    .filename(filename)
                    .build()
                    .getResponse();
            //If is to use file cache
            if (mFilesCacheOn) {
                //Add file header and content to cache
                this.mFilesCache.put(filename, abFileHeaderAndContent);
            }
            return abFileHeaderAndContent;
        }

    }


    /**
     * Derives the actual file name for the requested file.
     * <p>
     * If the request is a directory, the default file including the
     * argument directory is returned.
     * <p>
     * If forced default page is enabled, this page in the in the web root is
     * always returned.
     * <p>
     * The directory on the hard drive is added as a prefix to argument file
     * name.
     *
     * @param requestedFile The requested file
     * @return The name of the actual file to return
     */
    String getActualFilename(String requestedFile) {
        // if filename is empty
        if (Checker.isEmpty(requestedFile)) {
            requestedFile = DEFAULT_PAGE;
        } // if the request if a folder path, we return the default file in this folder
        else if (FileUtil.isDirPath(requestedFile)) {
            // suffix with / if not there
            if (!requestedFile.endsWith("/")) {
                requestedFile += "/";
            }
            // suffix with default page
            requestedFile += DEFAULT_PAGE;
        }
        //Prefix with path to directory where files resides
        requestedFile = mWebServerDir + requestedFile;
        return requestedFile;
    }
    // ------------------------------------
    // - SERVER SIDE INCLUDES
    // ------------------------------------


    /**
     * Replaces all the server side variable placeholders <!--#echo var="MY_VAR" --> with
     * the server side variable value.
     *
     * @param fileContent         The file content into which the argument variables are to be
     *                            injected
     * @param serverSideVariables The variables to inject.
     * @return The argument with the placeholders replaced with server side
     * variables.
     */
    static byte[] setServerSideVariables(byte[] fileContent, Map<String, String> serverSideVariables) {
        //Create a string from the file content
        String mainFileContent = EncodingUtil.convertToString(fileContent);
        //Create holder of return string
        StringBuffer fileContentReturn = new StringBuffer();
        //Create a matcher for the placeholders for variables on the file content
        Matcher placeHolderMatcher = VARIABLE_PLACEHOLDER_PATTERN.matcher(mainFileContent);
        //Go through all server side variable tags in the file
        while (placeHolderMatcher.find()) {
            //Get the name of the variable
            String currentPlaceholder = placeHolderMatcher.group(1);
            String variableValue = serverSideVariables.get(currentPlaceholder);
            //If there was a value for the found place holder
            if (variableValue != null) {
                variableValue = Matcher.quoteReplacement(variableValue);
                //Replace the placeholder with the value
                placeHolderMatcher.appendReplacement(fileContentReturn, variableValue);
            }
        }
        //Add the end of the file to return string
        placeHolderMatcher.appendTail(fileContentReturn);
        //Create byte array and return
        return EncodingUtil.convertToByteArray(fileContentReturn.toString());
    }


    /**
     * Replaces all include file placeholders <!--#include file="header.html" --> with the
     * content of the include file.
     *
     * @param fileContent The content of the file in which include files are to be injected
     * @param directory   The directory to prepend to the name of the include files.
     * @return The argument file content with the placeholders replaced with the content of the
     * include files.
     */
    static byte[] setServerIncludeFiles(byte[] fileContent, String directory) {
        //Create a string from the file content
        String mainFileContent = EncodingUtil.convertToString(fileContent);
        //Create holder of return string
        StringBuffer fileContentReturn = new StringBuffer();
        ///Create a matcher for the include file tags on the file content
        Matcher includeFileMatcher = INCLUDE_FILE_PATTERN.matcher(mainFileContent);
        //Go through all include file tags in the file
        while (includeFileMatcher.find()) {
            //Get the name of the include file
            String includeFilename = includeFileMatcher.group(1);
            //Add directory to file name
            includeFilename = directory + includeFilename;
            String includeFileContent;
            if (FileRW.fileExists(includeFilename)) {
                //Get include file content
                includeFileContent = FileRW.readFileAsString(includeFilename);
            } else {
                includeFileContent = "Include file '" + includeFilename + "' not found";
            }
            //Replace all $ with \$. This as dollar sign has a special meaning in Matcher.appendReplacement
            includeFileContent = includeFileContent.replaceAll("\\$", "\\\\\\$");
            //Add content up until include file and replace include file reference with include file content
            includeFileMatcher.appendReplacement(fileContentReturn, includeFileContent);
        }
        //Add the end of the file to return string
        includeFileMatcher.appendTail(fileContentReturn);
        //Create byte array and return
        return EncodingUtil.convertToByteArray(fileContentReturn.toString());
    }


    @Override
    public State getState() {
        return State.getBuilder()
                .add("Directory", mWebServerDir)
                .add("BrowserCacheMaxAge", mBrowserCacheMaxAge)
                .add("FilesInRamCache", mFilesCacheOn)
                .build();
    }

}
