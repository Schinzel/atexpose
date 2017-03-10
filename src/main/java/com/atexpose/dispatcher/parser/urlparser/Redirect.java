package com.atexpose.dispatcher.parser.urlparser;

import org.apache.commons.lang3.tuple.Pair;
import io.schinzel.basicutils.SubStringer;

/**
 * Purpose of this file is to hold a Redirect directive
 *
 * @author Jorgen
 */
public class Redirect {

    /**
     * Type of available redirects
     */
    public enum RedirectType {
        /**
         * Redirect from one host address to another
         */
        HOST,
        /**
         * Redirect from one file within the server to another file
         */
        FILE
    }

    /**
     * The address of host or file we do not want the user to land on
     */
    private final String mFromAddress;
    /**
     * The address of host or file we want the user to see instead
     */
    private final String mToAddress;
    /**
     * Type of redirect, is this from a host to another or from a file to another
     */
    private final RedirectType mRedirectType;
    /**
     * Http redirect statusAsJson, 301 for permanent or 302 for temporary redirect
     */
    private final RedirectHttpStatus mStatusCode;


    /**
     * Constructor to create a redirect object
     *
     * @param from         - from what should we redirect
     * @param to           - to what should we redirect
     * @param redirectType - type of redirect,host url (address) or file path
     * @param statusCode   - http statusAsJson redirect code, permanent redirect or temporary redirect
     */
    public Redirect(String from, String to, RedirectType redirectType, RedirectHttpStatus statusCode) {
        mFromAddress = from;
        mToAddress = to;
        mRedirectType = redirectType;
        mStatusCode = statusCode;
    }


    /**
     * @param host host address to match with this redirect
     * @return True this a Host redirect directive and does the parameter host address match this redirect address.
     */
    public boolean isRedirectHost(String host) {
        return mRedirectType == RedirectType.HOST && host != null && host.equalsIgnoreCase(mFromAddress);
    }


    /**
     * Is this a File redirect directive and does the parameter file name match this redirect file name
     *
     * @param requestedFileName file name to match with this file name
     * @return
     */
    public boolean isRedirectFile(String requestedFileName) {
        // Remove query parameter from the requested file
        String filePath = getFilePath(requestedFileName);
        // See if it matches this redirect 
        return mRedirectType == RedirectType.FILE && filePath.equalsIgnoreCase(mFromAddress);
    }


    /**
     * If this is a redirect, get address of host or file we wish to redirect the user to
     *
     * @param useHttps          - should we redirect to https or http
     * @param requestedFileName - name of file user is requesting, with query parameters
     * @return - pair of path to where we should redirect the user request and type of redirect
     */
    public Pair<String, RedirectHttpStatus> getRedirectInfo(boolean useHttps, String requestedFileName) {
        // If this is a redirect from a host url to another
        if (mRedirectType == RedirectType.HOST) {
            String protocol = useHttps ? "https://" : "http://";
            return Pair.of(protocol + mToAddress + "/" + requestedFileName, mStatusCode);
        }
        // If this is a redirect from a file to another
        if (mRedirectType == RedirectType.FILE) {
            // Extract the query paramters from the requested file name
            String fileQuery = getFileQuery(requestedFileName);
            // Redirect user to the new url and keep the query parameters
            return Pair.of(getFileRedirect(mToAddress, fileQuery), mStatusCode);
        }
        throw new RuntimeException("Not a valid redirect type.");
    }


    /**
     * Method to remove the query parameters from the requested file name
     *
     * @param requestedFileName
     * @return
     */
    private String getFilePath(String requestedFileName) {
        if (requestedFileName.contains("?")) {
            return SubStringer.create(requestedFileName).end("?").toString();
        }
        return requestedFileName;
    }


    /**
     * Method to extract the query parameters from a requested file name
     *
     * @param requestedFileName
     * @return
     */
    private String getFileQuery(String requestedFileName) {
        if (requestedFileName.contains("?")) {
            return SubStringer.create(requestedFileName).start("?").toString();
        }
        return "";
    }


    /**
     * Method to build the file name to redirect the request to
     *
     * @param filePath
     * @param fileQuery
     * @return
     */
    private String getFileRedirect(String filePath, String fileQuery) {
        String fileName = filePath;
        if (fileQuery != null && fileQuery.length() > 0) {
            fileName += ("?" + fileQuery);
        }
        return fileName;
    }

}
