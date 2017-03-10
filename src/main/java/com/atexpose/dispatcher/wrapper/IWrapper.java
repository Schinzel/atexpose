package com.atexpose.dispatcher.wrapper;

import com.atexpose.dispatcher.parser.urlparser.RedirectHttpStatus;
import io.schinzel.basicutils.state.IStateNode;
import org.json.JSONObject;

/**
 * @author Schinzel
 */
public interface IWrapper extends IStateNode {

    String wrapResponse(String response);


    byte[] wrapFile(String fileName);


    String wrapError(String error);


    String wrapJSON(JSONObject response);


    /**
     * Only works for web responses.
     *
     * @param url
     * @param redirectStatusCode
     * @return
     */
    String wrapRedirect(String url, RedirectHttpStatus redirectStatusCode);

}
