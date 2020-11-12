package com.atexpose.dispatcher.wrapper;

import com.atexpose.ProjectProperties;
import com.atexpose.dispatcher.channels.webchannel.WebCookieStorage;
import com.atexpose.util.FileRW;
import com.atexpose.util.httpresponse.HttpResponse404;
import com.atexpose.util.httpresponse.HttpResponse500;
import com.atexpose.util.httpresponse.HttpResponseFile;
import com.atexpose.util.httpresponse.HttpResponseString;
import io.schinzel.basicutils.Checker;
import io.schinzel.basicutils.UTF8;
import io.schinzel.basicutils.collections.Cache;
import io.schinzel.basicutils.state.State;
import io.schinzel.basicutils.thrower.Thrower;
import lombok.Builder;
import lombok.experimental.Accessors;
import org.json.JSONObject;

import java.nio.charset.StandardCharsets;
import java.util.Collections;
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
 * Text files support two types of server side includes:
 * 1) Files <!--#include file="header.html" -->
 * 2) Variables <!--#echo var="my_var" -->
 * Include files can contain other include files which in turn can contain other include files and
 * so on.
 * The format is according to SSI: https://en.wikipedia.org/wiki/Server_Side_Includes
 * First are files in included and after that the variables are inserted in the resulting file.
 *
 * @author Schinzel
 */
@Accessors(prefix = "m")
public class WebWrapper implements IWrapper {
    /** The default 404 page */
    private static final byte[] DEFAULT_404_PAGE = "<html><body><center>File not found</center><body></html>"
            .getBytes(StandardCharsets.UTF_8);
    /** Pattern for server side variables. Example: <!--#echo var="my_var" --> */
    private static final Pattern VARIABLE_PLACEHOLDER_PATTERN = Pattern
            .compile("<!--#echo var=\"([a-zA-Z1-9_]{3,25})\" -->");
    /** Pattern for server side include files. Example: <!--#include file="header.html" --> */
    private static final Pattern INCLUDE_FILE_PATTERN = Pattern
            .compile("<!--#include file=\"([\\w,/]+\\.[A-Za-z]{2,4})\" -->");
    /** The default to return if no page was specified */
    private static final String DEFAULT_PAGE = "index.html";
    /** Where the files to server resides on the hard drive **/
    private final String mWebServerDir;
    /** Browser cache age instruction. **/
    private final int mBrowserCacheMaxAge;
    /** Variables to inject to the page */
    private final Map<String, String> mServerSideVariables;
    /** If true, files read will be cached in RAM */
    private final boolean mFileCacheOn;
    /** Custom response headers to add to response header */
    private final Map<String, String> mCustomResponseHeaders;
    /** Files read from drive stored in RAM */
    final Cache<String, byte[]> mFileCache;
    /** 404 page to return of a requested file does not exist */
    private final byte[] m404Page;


    @Builder
    WebWrapper(String webServerDir, int browserCacheMaxAge, boolean cacheFilesInRam,
               Map<String, String> serverSideVariables, Map<String, String> responseHeaders,
               String fileName404Page) {
        //If the last char is not a file separator, then add it
        mWebServerDir = !webServerDir.endsWith(ProjectProperties.FILE_SEPARATOR)
                ? webServerDir + ProjectProperties.FILE_SEPARATOR
                : webServerDir;
        mBrowserCacheMaxAge = browserCacheMaxAge;
        mServerSideVariables = Checker.isEmpty(serverSideVariables)
                ? Collections.emptyMap()
                : serverSideVariables;
        Thrower.throwIfVarOutsideRange(browserCacheMaxAge, "browserCacheMaxAge", 0, 604800);
        mCustomResponseHeaders = Checker.isEmpty(responseHeaders)
                ? Collections.emptyMap()
                : responseHeaders;
        mFileCacheOn = cacheFilesInRam;
        mFileCache = new Cache<>();
        m404Page = Checker.isEmpty(fileName404Page)
                ? DEFAULT_404_PAGE
                : FileRW.readFileAsByteArray(this.getFullFileName(fileName404Page));
    }


    @Override
    public String wrapResponse(String methodReturn) {
        return HttpResponseString.builder()
                .body(methodReturn)
                .customHeaders(mCustomResponseHeaders)
                .cookieList(WebCookieStorage.getCookiesToSendToClient())
                .build()
                .getResponse();
    }


    @Override
    public String wrapError(Map<String, String> properties) {
        return HttpResponse500.builder()
                .body(new JSONObject(properties))
                .customHeaders(mCustomResponseHeaders)
                .build()
                .getResponse();
    }


    @Override
    public byte[] wrapFile(String requestedFile) {
        String fileName = this.getFullFileName(requestedFile);
        //If is to use cache AND the argument file is cached
        if (mFileCacheOn && mFileCache.has(fileName)) {
            //Return cached file
            return mFileCache.get(fileName);
        }
        byte[] abFileHeaderAndContent = FileRW.fileExists(fileName)
                ? getFileHeaderAndContent(fileName)
                : get404headerAndContent();
        return mFileCacheOn
                ? mFileCache.putAndGet(fileName, abFileHeaderAndContent)
                : abFileHeaderAndContent;
    }


    private byte[] get404headerAndContent() {
        return HttpResponse404.builder()
                .body(m404Page)
                .customHeaders(mCustomResponseHeaders)
                .build()
                .getResponse();
    }


    byte[] getFileHeaderAndContent(String fileName) {
        byte[] abFileContent = FileRW.readFileAsByteArray(fileName);
        if (FileUtil.isTextFile(fileName)) {
            //Add server side include files
            abFileContent = WebWrapper.setServerIncludeFiles(abFileContent, mWebServerDir);
            //Add server side variables
            abFileContent = WebWrapper.setServerSideVariables(abFileContent, mServerSideVariables);
        }
        return HttpResponseFile.builder()
                .body(abFileContent)
                .customHeaders(mCustomResponseHeaders)
                .filename(fileName)
                .build()
                .getResponse();
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
    private String getFullFileName(String requestedFile) {
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
        String mainFileContent = UTF8.getString(fileContent);
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
        return UTF8.getBytes(fileContentReturn.toString());
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
        String mainFileContent = UTF8.getString(fileContent);
        //While there are include files
        while (INCLUDE_FILE_PATTERN.matcher(mainFileContent).find()) {
            //Create holder of return string
            StringBuffer fileContentReturn = new StringBuffer();
            ///Create a matcher for the include file tags on the file content
            Matcher includeFileMatcher = INCLUDE_FILE_PATTERN.matcher(mainFileContent);
            //Go through all include file tags in the file
            while (includeFileMatcher.find()) {
                //Get the name of the include file and add directory to file name
                String includeFileName = directory + includeFileMatcher.group(1);
                String includeFileContent = FileRW.fileExists(includeFileName)
                        ? FileRW.readFileAsString(includeFileName)
                        : "Include file '" + includeFileName + "' not found";
                //Replace all $ with \$. This as dollar sign has a special meaning in Matcher.appendReplacement
                includeFileContent = backslashDollarSigns(includeFileContent);
                //Add content up until include file and replace include file reference with include file content
                includeFileMatcher.appendReplacement(fileContentReturn, includeFileContent);
            }
            //Add the end of the file to return string
            includeFileMatcher.appendTail(fileContentReturn);
            mainFileContent = fileContentReturn.toString();
        }
        //Create byte array and return
        return UTF8.getBytes(mainFileContent);
    }


    /**
     * @return The argument string with all all $ replaced with \$
     */
    static String backslashDollarSigns(String str) {
        if (str == null) {
            throw new RuntimeException("String cannot be null");
        }
        return str.replace("$", "\\$");
    }


    @Override
    public State getState() {
        return State.getBuilder()
                .add("Directory", mWebServerDir)
                .add("BrowserCacheMaxAge", mBrowserCacheMaxAge)
                .add("FilesInRamCache", mFileCacheOn)
                .build();
    }

}
