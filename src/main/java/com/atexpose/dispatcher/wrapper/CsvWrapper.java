package com.atexpose.dispatcher.wrapper;

import io.schinzel.basicutils.state.State;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * @author Schinzel
 */
public class CsvWrapper implements IWrapper {
    private static final String COL_DELIMITER = ", ";


    @Override
    public String wrapResponse(String methodReturn) {
        return methodReturn;
    }


    @Override
    public String wrapError(String sError) {
        return "Error: " + sError;
    }


    @Override
    public byte[] wrapFile(String FileName) {
        throw new UnsupportedOperationException("Not supported yet.");
    }


    @Override
    public String wrapJSON(JSONObject response) {
        try {
            return response.toString(3);
        } catch (JSONException ex) {
            throw new RuntimeException(ex.getMessage());
        }
    }


    @Override
    public State getState() {
        return State.getBuilder()
                .add("ColumnDelimiter", COL_DELIMITER)
                .build();
    }


}
