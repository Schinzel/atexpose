package com.atexpose.dispatcher.wrapper;

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

}
