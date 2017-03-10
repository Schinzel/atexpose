package com.atexpose.dispatcher.parser.urlparser;

/**
 * Types of Redirect and their respective HTTPStatusCode
 */
public enum RedirectHttpStatus {
    /**
     * Permanently removed, use this when there is not change that this file or
     * host domain will ever re-occur
     */
    PERMANENT(301),
    /**
     * Temporary moved file of host domain. Use this when
     */
    TEMPORARY(302);

    final int mRedirectHttpCode;


    RedirectHttpStatus(int redirectCode) {
        mRedirectHttpCode = redirectCode;
    }


    public int getRedirectCode() {
        return mRedirectHttpCode;
    }
}
