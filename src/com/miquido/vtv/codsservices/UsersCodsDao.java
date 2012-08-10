package com.miquido.vtv.codsservices;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.miquido.vtv.bo.Profile;
import com.miquido.vtv.bo.Session;
import com.miquido.vtv.codsservices.dataobjects.CodsBusinessError;
import com.miquido.vtv.codsservices.dataobjects.LoginInput;
import com.miquido.vtv.codsservices.exceptions.CodsResponseFormatException;
import com.miquido.vtv.codsservices.internal.CodsErrorsHandler;
import com.miquido.vtv.codsservices.internal.CodsHttpJsonClient;
import com.miquido.vtv.codsservices.internal.jsontransformers.LoginInputJsonWriter;
import com.miquido.vtv.codsservices.internal.jsontransformers.ProfileJsonReader;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created with IntelliJ IDEA.
 * User: ljazgar
 * Date: 27.07.12
 * Time: 16:55
 * To change this template use File | Settings | File Templates.
 */
@Singleton
public class UsersCodsDao {

    private static final Logger logger = LoggerFactory.getLogger(UsersCodsDao.class);

    /**
     * TODO comment
     * @param userName
     * @param hashedPassword
     * @return null if invalid login (incorrect username or password)
     * @exception com.miquido.vtv.codsservices.exceptions.CodsConnectionException
     * @exception com.miquido.vtv.codsservices.exceptions.CodsResponseFormatException
     */
    public Session login(String userName, String hashedPassword) {
        logger.debug("UsersCodsDao.login");

        // GET version - it works but is deprecated
        String servicePath = String.format("/login/%s/%s", userName, hashedPassword);
        JSONObject responseObject = codsHttpJsonClient.get(servicePath);
        // POST version - it doesn't work now
//        LoginInput loginInput = new LoginInput(userName, hashedPassword);
//        JSONObject loginInputJson = loginInputJsonWriter.createJsonFromObject(loginInput);
//        JSONObject responseObject = codsHttpJsonClient.post("/login", loginInputJson);

        try {
            if (responseObject.has("id")) {
                Session session = new Session();
                session.setId( responseObject.getString("id") );
                JSONObject nameValueList = responseObject.getJSONObject("name_value_list");
                session.setUserId(nameValueList.getJSONObject("user_id").getString("value"));
                session.setUserName(nameValueList.getJSONObject("user_name").getString("value"));

                logger.debug("Session: " + session);
                return session;

            } else {
                logger.debug("Cods response hasn't id attribute. Trying parsing as error structure.");
                CodsBusinessError codsBusinessError = codsErrorsHandler.handleStandardCodsError(responseObject);
                if (codsBusinessError.getNumber()==10) // Invalid Login
                    return null;
                else
                    throw new CodsResponseFormatException(String.format("Unknown error number in jsontransformers response from CODS. CodsBusinessError: %s.", codsBusinessError.toString()));

            }

        } catch (JSONException e) {
            throw new CodsResponseFormatException("JSON response from CODS has incorrect structure.", e);
        }

    }

    /**
     *
     * @param sessionId
     * return Never null
     * @throws com.miquido.vtv.codsservices.exceptions.CodsSystemException
     */
    public Profile getCurrentUserProfile(String sessionId) {
        logger.debug("UsersCodsDao.getUserProfile({})", sessionId);

        JSONObject responseObject = codsHttpJsonClient.getWithSession(sessionId, "/profile");

        if (codsErrorsHandler.checkIsNullResponse(responseObject)) {
            logger.debug("User has not profile");
            return null;
        }

        if (profileJsonReader.isCorrectType(responseObject)) {
            Profile profile = profileJsonReader.createObjectFromJson(responseObject);
            logger.debug("Current user profile: " + profile);
            return profile;
        } else {
            logger.debug("Cods response is not Profile. Trying parsing as error structure.");
            CodsBusinessError codsBusinessError = codsErrorsHandler.handleStandardCodsError(responseObject);
            throw new CodsResponseFormatException(String.format("Unknown error number in jsontransformers response from CODS. CodsBusinessError: %s.", codsBusinessError.toString()));
        }
    }


    // ***************************** DEPENDENCIES ************************************************************

    @Inject
    private CodsHttpJsonClient codsHttpJsonClient;
    @Inject
    private ProfileJsonReader profileJsonReader;
    @Inject
    private CodsErrorsHandler codsErrorsHandler;

    @Inject
    private LoginInputJsonWriter loginInputJsonWriter;
}
