package com.miquido.vtv.codsservices.internal.impl;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.miquido.vtv.bo.Id;
import com.miquido.vtv.bo.Notification;
import com.miquido.vtv.bo.Profile;
import com.miquido.vtv.codsservices.ProfilesCodsDao;
import com.miquido.vtv.codsservices.dataobjects.RelatedDataCollection;
import com.miquido.vtv.codsservices.dataobjects.PageParams;
import com.miquido.vtv.codsservices.dataobjects.Subcollection;
import com.miquido.vtv.codsservices.internal.httpjsonclient.CodsErrorsHandler;
import com.miquido.vtv.codsservices.internal.httpjsonclient.CodsHttpJsonClient;
import com.miquido.vtv.codsservices.internal.httpjsonclient.CodsUrlParams;
import com.miquido.vtv.codsservices.internal.jsontransformers.ProfileJsonReader;
import com.miquido.vtv.codsservices.internal.jsontransformers.ProfileJsonWriter;
import com.miquido.vtv.codsservices.internal.jsontransformers.RelatedDataCollectionJsonReader;
import com.miquido.vtv.codsservices.internal.jsontransformers.SubcollectionJsonReader;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created with IntelliJ IDEA.
 * User: ljazgar
 * Date: 27.07.12
 * Time: 16:51
 * To change this template use File | Settings | File Templates.
 */
@Singleton
public class ProfilesCodsDaoImpl implements ProfilesCodsDao {

    private static final Logger logger = LoggerFactory.getLogger(ProfilesCodsDao.class);


    @Override public RelatedDataCollection<Profile> getFriends(String sessionId, Id profileId) {
        return getFriends(sessionId, profileId, null);
    }

    @Override public RelatedDataCollection<Profile> getFriends(String sessionId, Id profileId, PageParams pageParams) {
        logger.debug(String.format("ProfilesCodsDao.getFriends(%s, %s, %s)", sessionId, profileId, (pageParams!=null)?pageParams.toString():"null"));

        CodsHttpJsonClient.JsonResponse jsonResponse = codsHttpJsonClient.get(
                String.format("/profiles/%s/friends", profileId),
                CodsUrlParams.createNew()
                        .addSession(sessionId)
                        .addPageParams(pageParams));

        codsErrorsHandler.handleCodsError(jsonResponse);

        return profileRelatedDataCollectionJsonReader.createObjectFromJson(jsonResponse.getJsonObject());
    }

    public Profile update(String sessionId, Id profileId, Profile profileData) {
        return update(sessionId, profileId, profileData, profileData);
    }
        @Override
    public Profile update(String sessionId, Id profileId, Profile profileData, Profile dataToUpdateIndicator) {
        logger.debug(String.format("ProfilesCodsDao.update(%s, %s, %s, %s)", sessionId, profileId, profileData, dataToUpdateIndicator));

        JSONObject profileJson = profileJsonWriter.createJsonFromObject(profileData, dataToUpdateIndicator);
        CodsHttpJsonClient.JsonResponse jsonResponse = codsHttpJsonClient.put(
                String.format("/profiles/%s", profileId),
                CodsUrlParams.createNew().addSession(sessionId),
                profileJson );

        codsErrorsHandler.handleCodsError(jsonResponse);

        return profileJsonReader.createObjectFromJson(jsonResponse.getJsonObject());
    }

    @Override
    public Subcollection<Notification> getProfileNotifications(String sessionId, Id profileId) {
        return getProfileNotifications(sessionId, profileId, null);
    }
        @Override
    public Subcollection<Notification> getProfileNotifications(String sessionId, Id profileId, PageParams pageParams) {
        logger.debug(String.format("ProfilesCodsDao.getProfileNotifications(%s, %s, %s)", sessionId, profileId, (pageParams!=null)?pageParams.toString():"null"));

        CodsHttpJsonClient.JsonResponse jsonResponse = codsHttpJsonClient.get(
                String.format("/profiles/%s/notifications", profileId),
                CodsUrlParams.createNew()
                        .addSession(sessionId)
                        .addPageParams(pageParams));

        codsErrorsHandler.handleCodsError(jsonResponse);

        return notificationSubcollectionJsonReader.createObjectFromJson(jsonResponse.getJsonObject());
    }

    // ***************************** Private helper methods ***************************************************


    // ***************************** DEPENDENCIES ************************************************************
    @Inject
    CodsHttpJsonClient codsHttpJsonClient;
    @Inject
    private CodsErrorsHandler codsErrorsHandler;
    @Inject
    RelatedDataCollectionJsonReader<Profile> profileRelatedDataCollectionJsonReader;
    @Inject
    SubcollectionJsonReader<Notification> notificationSubcollectionJsonReader;
    @Inject
    ProfileJsonWriter profileJsonWriter;
    @Inject
    ProfileJsonReader profileJsonReader;
}
