package com.atexpose.dispatcher.parser.urlparser;

import io.schinzel.basicutils.Checker;
import io.schinzel.basicutils.str.Str;
import org.json.JSONObject;
import org.jsoup.Connection;
import org.jsoup.Jsoup;

import java.io.IOException;

/**
 * The purpose of this class is to retrieve an GSuite email address from Google.
 * *
 * Created by schinzel on 2017-04-27.
 */
class GSuiteAuth {


    /**
     * Checks is a user is logged into the argument domain or not.
     *
     * @param authCookieValue The auth cookie value from the user's request.
     * @param domain          The domain that the user should be logged into.
     * @return True if user is logged into argument domain else false.
     */
    boolean isUserLoggedIn(String authCookieValue, String domain) {
        if (Checker.isEmpty(authCookieValue)) {
            return false;
        }
        return this.requestUsersEmail(authCookieValue).endsWith(domain);
    }


    /**
     * Calls google with the argument token as argument value.
     *
     * @param token Token to send as query param to Google.
     * @return The email address of the user making a request.
     */
    private String requestUsersEmail(String token) {
        //Create URL with argument token as query string parameter.
        String googleAuthUrl = "https://www.googleapis.com/oauth2/v1/userinfo?access_token=";
        String url = Str.create().a(googleAuthUrl).a(token).toString();
        try {
            //Make request to google.
            Connection.Response response = Jsoup
                    .connect(url)
                    .ignoreContentType(true)
                    .timeout(10000)
                    .method(Connection.Method.GET)
                    .execute();
            //Return the email address that was sent as a response from Google
            return new JSONObject(response.body()).getString("email");
        } catch (IOException ex) {
            throw new RuntimeException("Exception trying to authenticate user using Google's API. " + ex.toString());
        }
    }

}
