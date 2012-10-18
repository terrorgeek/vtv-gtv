package com.miquido.vtv.codsservices.internal.impl;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.miquido.vtv.bo.Id;
import com.miquido.vtv.bo.Profile;
import com.miquido.vtv.bo.Session;
import com.miquido.vtv.codsservices.UsersCodsDao;
import com.miquido.vtv.codsservices.dataobjects.CodsError;
import com.miquido.vtv.codsservices.dataobjects.CodsLoginError;
import com.miquido.vtv.codsservices.exceptions.CodsResponseErrorException;
import com.miquido.vtv.codsservices.exceptions.CodsResponseFormatException;
import com.miquido.vtv.codsservices.internal.httpjsonclient.CodsErrorsHandler;
import com.miquido.vtv.codsservices.internal.httpjsonclient.CodsHttpJsonClient;
import com.miquido.vtv.codsservices.internal.httpjsonclient.CodsUrlParams;
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
public class UsersCodsDaoImpl implements UsersCodsDao {

    private static final Logger logger = LoggerFactory.getLogger(UsersCodsDao.class);

    /**
     * TODO comment
     * @param userName
     * @param hashedPassword
     * @return null if invalid login (incorrect username or password)
     * @exception com.miquido.vtv.codsservices.exceptions.CodsConnectionException
     * @exception com.miquido.vtv.codsservices.exceptions.CodsResponseFormatException
     */
    @Override public Session login(String userName, String hashedPassword) {
        logger.debug("UsersCodsDao.login");

        // GET version - it works but is deprecated
        CodsHttpJsonClient.JsonResponse jsonResponse =
                codsHttpJsonClient.get("/login",
                                        CodsUrlParams.createNew()
                                                .add("login", userName)
                                                .add("password", hashedPassword));

        CodsError codsError = codsErrorsHandler.handleCodsError(jsonResponse);
        if (codsError!=null) {
            throw new CodsResponseErrorException(jsonResponse.getStatusCode(), codsError.getError());
        }

        JSONObject responseObject = jsonResponse.getJsonObject();

        try {
            if (responseObject!=null && responseObject.has("id")) {
                Session session = new Session();
                session.setId( responseObject.getString("id") );
                //session.setUserId(Id.valueOf(responseObject.getString("user_id")));
                session.setUserName(responseObject.getString("user_name"));

                logger.debug("Session: " + session);
                return session;

            } else {
                logger.debug("Cods response hasn't id attribute. Trying parsing as error structure.");
                CodsLoginError codsLoginError = codsErrorsHandler.handleCodsLoginError(responseObject);
                if (codsLoginError.getNumber()==10) // Invalid Login
                    return null;
                else
                    throw new CodsResponseFormatException(String.format("Unknown error number in response from CODS. CodsLoginError: %s.", codsLoginError.toString()));

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
    @Override public Profile getCurrentUserProfile(String sessionId) {
        logger.debug("UsersCodsDao.getUserProfile({})", sessionId);

        CodsHttpJsonClient.JsonResponse jsonResponse =
                codsHttpJsonClient.get("/profile", CodsUrlParams.createNew().addSession(sessionId));

        CodsError codsError = codsErrorsHandler.handleCodsError(jsonResponse);
        if (codsError!=null && codsError.isNotFound()) {
            logger.debug("User has not profile");
            return null;
        }

        JSONObject responseObject = jsonResponse.getJsonObject();
        if (!profileJsonReader.isCorrectType(responseObject)) {
            throw new CodsResponseFormatException(String.format("Cods response is not Profile."));
        }

        Profile profile = profileJsonReader.createObjectFromJson(responseObject);
        logger.debug("Current user profile: " + profile);
        return profile;
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
