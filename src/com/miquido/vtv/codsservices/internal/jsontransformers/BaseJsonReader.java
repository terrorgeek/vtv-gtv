package com.miquido.vtv.codsservices.internal.jsontransformers;

import com.miquido.vtv.codsservices.exceptions.CodsResponseFormatException;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created with IntelliJ IDEA.
 * User: ljazgar
 * Date: 31.08.12
 * Time: 11:46
 * To change this template use File | Settings | File Templates.
 */
public abstract class BaseJsonReader<Type> implements JsonReader<Type> {

    private final static DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    protected abstract Class<Type> getType();

    protected Date getDate(JSONObject jsonObject, String attributeName) throws JSONException {
        return parseDate(jsonObject.getString(attributeName), attributeName);
    }

    protected Date optDate(JSONObject jsonObject, String attributeName) {
        return parseDate(jsonObject.optString(attributeName), attributeName);
    }

    private Date parseDate(String dateString, String attributeName) {
        if (dateString==null || dateString.isEmpty())
            return null;
        try {
            return dateFormat.parse(dateString);
        } catch (ParseException e) {
            throw new CodsResponseFormatException(String.format("Incorrect format of data in attribute %s of %s.", attributeName, getType().getName()), e);
        }
    }

}
