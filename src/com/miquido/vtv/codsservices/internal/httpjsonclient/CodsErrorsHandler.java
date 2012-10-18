package com.miquido.vtv.codsservices.internal.httpjsonclient;

import com.google.inject.Singleton;
import com.miquido.vtv.codsservices.dataobjects.CodsLoginError;
import com.miquido.vtv.codsservices.dataobjects.CodsError;
import com.miquido.vtv.codsservices.exceptions.CodsInvalidSessionException;
import com.miquido.vtv.codsservices.exceptions.CodsResponseErrorException;
import com.miquido.vtv.codsservices.exceptions.CodsResponseFormatException;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created with IntelliJ IDEA.
 * User: ljazgar
 * Date: 28.07.12
 * Time: 21:24
 * To change this template use File | Settings | File Templates.
 */
@Singleton
public class CodsErrorsHandler {
    private static final Logger logger = LoggerFactory.getLogger(CodsErrorsHandler.class);


    /**
     *
     * @return If jsonObject contains CodsLoginException method returns it.
     *         Never null.
     * @exception com.miquido.vtv.codsservices.exceptions.CodsResponseFormatException if message has unrecognized structure.
     */
    public CodsLoginError handleCodsLoginError(JSONObject jsonObject) {

        CodsLoginError codsLoginError = getCodsLoginErrorFromJson(jsonObject);
        if (codsLoginError==null)
            throw new CodsResponseFormatException("Unrecognized structure of JSON response from CODS server.");
        logger.debug("CodsLoginError: {}", codsLoginError.toString());
        return codsLoginError;
    }

    /**
     *
     * @param jsonResponse
     * @return CodsError obtained from jsonResponse, if there is an unhandled error, which is not treated as error.
     *         null if there is not any error at all.
     * @exception CodsResponseErrorException if there is an error in response.
     */
    public CodsError handleCodsError(CodsHttpJsonClient.JsonResponse jsonResponse) {

        JSONObject jsonObject = jsonResponse.getJsonObject();
        CodsError codsError = (jsonObject!=null) ? getCodsErrorFromJson(jsonObject) : null;
        int statusCode = jsonResponse.getStatusCode();
        if (statusCode==404) {
            if (codsError==null)
                codsError = new CodsError();
            if (codsError.getNumber()!=404)
                codsError.setNumber(404);
            return codsError;
        } else if (statusCode==401) {
            throw new CodsInvalidSessionException((codsError!=null)?codsError.getError():"Access to resource requires authorization");

        } else if (statusCode>=300) {
            throw new CodsResponseErrorException(statusCode, (codsError!=null)?codsError.getError():null);
        } else {
            return null;
        }
    }

    private CodsLoginError getCodsLoginErrorFromJson(JSONObject jsonObject) {
        if (!jsonObject.has("name") || !jsonObject.has("number"))
            return null;

        try {
            CodsLoginError codsLoginError = new CodsLoginError();
            codsLoginError.setName(jsonObject.getString("name"));
            codsLoginError.setNumber(jsonObject.getLong("number"));
            codsLoginError.setDescription(jsonObject.optString("description", null));
            return codsLoginError;
        } catch(JSONException e) {
            throw new CodsResponseFormatException("Error while parsing JSON response from CODS server as CodsLoginError.", e);
        }
    }

    private CodsError getCodsErrorFromJson(JSONObject jsonObject) {
        if (!jsonObject.has("status") || !jsonObject.has("error") || !jsonObject.has("number"))
            return null;

        try {
            CodsError codsError = new CodsError();
            codsError.setStatus(jsonObject.getString("status"));
            codsError.setError(jsonObject.getString("error"));
            codsError.setNumber(jsonObject.getInt("number"));
            return codsError;
        } catch(JSONException e) {
            throw new CodsResponseFormatException("Error while parsing JSON response from CODS server as error.", e);
        }

    }

}
