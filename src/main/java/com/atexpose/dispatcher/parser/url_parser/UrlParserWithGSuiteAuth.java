package com.atexpose.dispatcher.parser.url_parser;

import com.atexpose.dispatcher.parser.IParser;
import com.atexpose.dispatcher.parser.Request;
import io.schinzel.basicutils.state.State;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

/**
 * The purpose of this class is a parse URL requests. User's has to be logged into constructor
 * argument domain to do command requests. Note, requests for static resources does not require the
 * user to be authenticated.
 * If user is not logged in, an error is thrown.
 * <p>
 * Created by Schinzel on 2017-03-03.
 */
@Accessors(prefix = "m")
public class UrlParserWithGSuiteAuth extends UrlParser {
    /** The name of the cookie that holds the authentication token. */
    @Getter(AccessLevel.PACKAGE)
    private final String mAuthCookieName;
    /** The GSuite domain that the user has to be logged into to make command requests. */
    @Getter(AccessLevel.PACKAGE)
    private final String mDomain;
    /** Authenticates against a GSuite domain. Setter for tests. */
    @Setter(AccessLevel.PACKAGE)
    GSuiteAuth mGSuiteAuth = new GSuiteAuth();


    @Builder
    UrlParserWithGSuiteAuth(String authCookieName, String domain) {
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
            if (!mGSuiteAuth.isUserLoggedIn(authCookieValue, this.getDomain())) {
                throw new RuntimeException("User was not logged in to the GSuite domain '" + this.getDomain() + "'.");
            }
        }
        return request;
    }


    @Override
    public State getState() {
        return State.getBuilder(super.getState())
                .add("AuthDomain", mDomain)
                .build();
    }


}
