package com.atexpose.dispatcher.parser.urlparser;

import com.atexpose.dispatcher.parser.IParser;
import com.atexpose.dispatcher.parser.Request;
import io.schinzel.basicutils.Checker;
import io.schinzel.basicutils.state.State;
import io.schinzel.basicutils.str.Str;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.experimental.Accessors;
import org.json.JSONObject;
import org.jsoup.Connection;
import org.jsoup.Jsoup;

import java.io.IOException;

/**
 * The purpose of this class is a parse URL requests. User's has to be logged into constructor
 * argument domain to
 * do command requests. Note, requests for static resources does not require the user to be
 * authenticated.
 * If user is not logged in, an error is thrown.
 * <p>
 * Created by Schinzel on 2017-03-03.
 */
@Accessors(prefix = "m")
public class UrlParserWithGSuiteAuth extends UrlParser2 {
    /**
     * The name of the cookie that holds the authentication token.
     */
    @Getter(AccessLevel.PRIVATE)
    private final String mAuthCookieName;
    /**
     * The GSuite (my god that is a corny name, sounds like the name of a rap star's posse) domain
     * that the user
     * has to be logged into to make command requests.
     */
    @Getter(AccessLevel.PRIVATE)
    private final String mDomain;


    @Builder
    public UrlParserWithGSuiteAuth(String authCookieName, String domain) {
        mAuthCookieName = authCookieName;
        mDomain = domain;
    }


    @Override
    public IParser getClone() {
        return UrlParserWithGSuiteAuth.builder()
                .authCookieName(this.getAuthCookieName())
                .domain(this.getDomain())
                .build();
    }


    @Override
    public Request getRequest(String incomingRequest) {
        Request request = super.getRequest(incomingRequest);
        //If the call was a command call
        if (this.getCallType() == CallType.COMMAND) {
            //Get the auth cookie value
            String authCookieValue = this.getHttpRequest()
                    .getCookies()
                    .get(this.getAuthCookieName());
            if (!UrlParserWithGSuiteAuth.isUserLoggedIn(authCookieValue, this.getDomain())) {
                throw new RuntimeException("User was not logged in to the GSuite domain '" + this.getDomain() + "'.");
            }
        }
        return request;
    }


    /**
     * Checks is a user is logged into the argument domain or not.
     *
     * @param authCookieValue The auth cookie value from the user's request.
     * @param domain          The domain that the user should be logged into.
     * @return True if user is logged into argument domain else false.
     */
    private static boolean isUserLoggedIn(String authCookieValue, String domain) {
        if (Checker.isEmpty(authCookieValue)) {
            return false;
        }
        String userEmail = UrlParserWithGSuiteAuth.requestUsersEmail(authCookieValue);
        return (userEmail.endsWith(domain));
    }


    /**
     * Calls google with the argument token as argument value.
     *
     * @param token Token to send as query param to Google.
     * @return The email address of the user making a request.
     */
    private static String requestUsersEmail(String token) {
        String googleAuthUrl = "https://www.googleapis.com/oauth2/v1/userinfo?access_token=";
        //Create URL with argument token as query string parameter.
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


    @Override
    public State getState() {
        return State.getBuilder(super.getState())
                .add("AuthDomain", mDomain)
                .build();
    }


}
