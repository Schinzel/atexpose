package com.atexpose.dispatcher.wrapper;

import com.google.common.base.Joiner;
import io.schinzel.basicutils.state.State;
import org.json.JSONObject;

import java.util.Map;

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
    public String wrapError(Map<String, String> properties) {
        return "Error:\n" + Joiner.on("\n").withKeyValueSeparator(": ").join(properties);
    }


    @Override
    public byte[] wrapFile(String fileName) {
        throw new UnsupportedOperationException("Not supported yet.");
    }


    @Override
    public String wrapJSON(String response) {
        return new JSONObject(response).toString(3);
    }


    @Override
    public State getState() {
        return State.getBuilder()
                .add("Class", this.getClass().getSimpleName())
                .add("ColumnDelimiter", COL_DELIMITER)
                .build();
    }


}
