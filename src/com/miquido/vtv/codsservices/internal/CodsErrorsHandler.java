package com.miquido.vtv.codsservices.internal;

import com.google.inject.Singleton;
import com.miquido.vtv.codsservices.dataobjects.CodsBusinessError;
import com.miquido.vtv.codsservices.dataobjects.CodsError;
import com.miquido.vtv.codsservices.exceptions.CodsInvalidSessionException;
import com.miquido.vtv.codsservices.exceptions.CodsResponseFormatException;
import com.miquido.vtv.codsservices.exceptions.CodsSystemException;
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

//    {"id":null,"warning":"Access to this object is denied since it has been deleted or does not exist"}
    public boolean checkIsNullResponse(JSONObject jsonObject) {
        if (jsonObject.has("id") && jsonObject.isNull("id") && jsonObject.has("warning")) {
            logger.debug("Response is null. Reason: {}", jsonObject.optString("warning"));
            return true;
        } else {
            return false;
        }
    }

    /**
     *
     * @return If jsonObject contains CodsBusinessException which is not handled by this method, method returns it.
     *         Never null.
     * @exception com.miquido.vtv.codsservices.exceptions.CodsInvalidSessionException if message about invalid session was recognized
     * @exception com.miquido.vtv.codsservices.exceptions.CodsResponseFormatException if message has unrecognized structure.
     * @exceptoin CodsSystemException If message from CODS server has error status (eg No endpoint found)
     */
    public CodsBusinessError handleStandardCodsError(JSONObject jsonObject) {

        CodsBusinessError codsBusinessError = getCodsBussinessErrorFromJson(jsonObject);

        if (codsBusinessError !=null) {
            logger.debug("CodsBusinessError: {}", codsBusinessError.toString());
            if (codsBusinessError.getNumber()==11) //Invalid Session ID
                throw new CodsInvalidSessionException(codsBusinessError.getDescription());
            else
                return codsBusinessError;
        }

        CodsError codsError = getCodsErrorFromJson(jsonObject);
        if (codsError!=null) {
            logger.debug("CodsError: {}", codsError.toString());
            throw new CodsSystemException(codsError.getError());
        }

        throw new CodsResponseFormatException("Unrecognized structure of JSON response from CODS server.");
    }

    private CodsBusinessError getCodsBussinessErrorFromJson(JSONObject jsonObject) {
        if (!jsonObject.has("name") || !jsonObject.has("number"))
            return null;

        try {
            CodsBusinessError codsBusinessError = new CodsBusinessError();
            codsBusinessError.setName(jsonObject.getString("name"));
            codsBusinessError.setNumber(jsonObject.getLong("number"));
            codsBusinessError.setDescription(jsonObject.optString("description", null));
            return codsBusinessError;
        } catch(JSONException e) {
            throw new CodsResponseFormatException("Error while parsing JSON response from CODS server as bussiness error.", e);
        }
    }

    private CodsError getCodsErrorFromJson(JSONObject jsonObject) {
        if (!jsonObject.has("status") || !jsonObject.has("error"))
            return null;

        try {
            CodsError codsError = new CodsError();
            codsError.setStatus(jsonObject.getString("status"));
            codsError.setError(jsonObject.getString("error"));
            return codsError;
        } catch(JSONException e) {
            throw new CodsResponseFormatException("Error while parsing JSON response from CODS server as error.", e);
        }

    }

}
