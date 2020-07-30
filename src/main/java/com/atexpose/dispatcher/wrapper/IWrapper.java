package com.atexpose.dispatcher.wrapper;

import io.schinzel.basicutils.state.IStateNode;
import org.json.JSONObject;

import java.util.Map;

/**
 * @author Schinzel
 */
public interface IWrapper extends IStateNode {

    String wrapResponse(String response);


    byte[] wrapFile(String fileName);


    String wrapError(Map<String, String> properties);


    String wrapJSON(String response);

}
