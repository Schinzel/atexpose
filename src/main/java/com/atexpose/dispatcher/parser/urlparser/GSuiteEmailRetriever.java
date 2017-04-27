package com.atexpose.dispatcher.parser.urlparser;

import io.schinzel.basicutils.str.Str;
import org.json.JSONObject;
import org.jsoup.Connection;
import org.jsoup.Jsoup;

import java.io.IOException;

/**
 * The purpose of this class is to retrieve an GSuite email address from Google.
 **
 * Created by schinzel on 2017-04-27.
 */
class GSuiteEmailRetriever {
    private static String GOOGLE_AUTH_URL = "https://www.googleapis.com/oauth2/v1/userinfo?access_token=";


    /**
     * Calls google with the argument token as argument value.
     *
     * @param token Token to send as query param to Google.
     * @return The email address of the user making a request.
     */
    String requestUsersEmail(String token) {
        //Create URL with argument token as query string parameter.
        String url = Str.create().a(GOOGLE_AUTH_URL).a(token).toString();
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
