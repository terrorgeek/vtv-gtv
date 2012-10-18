package com.miquido.vtv.codsservices.internal.jsontransformers;

import com.miquido.vtv.bo.Id;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created with IntelliJ IDEA.
 * User: ljazgar
 * Date: 12.09.12
 * Time: 12:04
 * To change this template use File | Settings | File Templates.
 */
public class BaseJsonWriter {

    protected boolean hasValue(Id id) {
        return (id!=null);
    }
    protected boolean hasValue(String stringValue) {
        return (stringValue!=null);
    }
    protected boolean hasValue(boolean booleanValue) {
        return booleanValue;
    }
    protected boolean hasValue(Boolean booleanValue) {
        return (booleanValue!=null);
    }
    protected boolean hasValue(Object objectValue) {
        return (objectValue!=null);
    }

    protected void put(JSONObject jsonObject, String jsonAttributeName, String value) throws JSONException {
        jsonObject.put(jsonAttributeName, (value!=null)? value : JSONObject.NULL);
    }
    protected void put(JSONObject jsonObject, String jsonAttributeName, Id id) throws JSONException {
        jsonObject.put(jsonAttributeName, (id!=null)? id.toString() : JSONObject.NULL);
    }

}
