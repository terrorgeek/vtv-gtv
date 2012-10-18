package com.miquido.vtv.codsservices.internal.impl;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.miquido.vtv.bo.Id;
import com.miquido.vtv.bo.Notification;
import com.miquido.vtv.codsservices.NotificationsCodsDao;
import com.miquido.vtv.codsservices.internal.httpjsonclient.CodsErrorsHandler;
import com.miquido.vtv.codsservices.internal.httpjsonclient.CodsHttpJsonClient;
import com.miquido.vtv.codsservices.internal.httpjsonclient.CodsUrlParams;
import com.miquido.vtv.codsservices.internal.jsontransformers.NotificationJsonReader;
import com.miquido.vtv.codsservices.internal.jsontransformers.NotificationJsonWriter;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Singleton
public class NotificationsCodsDaoImpl implements NotificationsCodsDao {

  private static final Logger logger = LoggerFactory.getLogger(NotificationsCodsDaoImpl.class);


  @Override
  public Notification update(String sessionId, Id notificationId, Notification notificationData, Notification dataToUpdateIndicator) {
    logger.debug(String.format("NotificationsCodsDao.update(%s, %s, %s, %s)", sessionId, notificationId, notificationData, dataToUpdateIndicator));

    JSONObject notificationJson = notificationJsonWriter.createJsonFromObject(notificationData, dataToUpdateIndicator);
    CodsHttpJsonClient.JsonResponse jsonResponse = codsHttpJsonClient.put(
        String.format("/notifications/%s", notificationId),
        CodsUrlParams.createNew().addSession(sessionId),
        notificationJson);

    codsErrorsHandler.handleCodsError(jsonResponse);

    return notificationJsonReader.createObjectFromJson(jsonResponse.getJsonObject());
  }

  @Override
  public Notification update(String sessionId, Id notificationId, Notification notificationData) {
    return update(sessionId, notificationId, notificationData, notificationData);
  }

  @Override
  public Notification create(String sessionId, Notification notificationData) {
    logger.debug(String.format("NotificationsCodsDao.create(%s, %s)", sessionId, notificationData));

    JSONObject notificationJson = notificationJsonWriter.createJsonFromObject(notificationData);
    CodsHttpJsonClient.JsonResponse jsonResponse = codsHttpJsonClient.post(
        "/notifications",
        CodsUrlParams.createNew().addSession(sessionId),
        notificationJson);
    codsErrorsHandler.handleCodsError(jsonResponse);
    return notificationJsonReader.createObjectFromJson(jsonResponse.getJsonObject());
  }

  // ***************************** DEPENDENCIES ************************************************************
  @Inject
  CodsHttpJsonClient codsHttpJsonClient;
  @Inject
  CodsErrorsHandler codsErrorsHandler;
  @Inject
  NotificationJsonWriter notificationJsonWriter;
  @Inject
  NotificationJsonReader notificationJsonReader;
}
