/**
 * Created by raho on 10/9/12.
 *
 * Copyright 2012 MiQUiDO <http://www.miquido.com/>. All rights reserved.
 */
package com.miquido.vtv.codsservices.internal.impl;

import com.google.inject.Inject;
import com.miquido.vtv.bo.Actor;
import com.miquido.vtv.bo.Program;
import com.miquido.vtv.codsservices.ProgramsCodsDao;
import com.miquido.vtv.codsservices.dataobjects.PageParams;
import com.miquido.vtv.codsservices.dataobjects.Subcollection;
import com.miquido.vtv.codsservices.internal.httpjsonclient.CodsErrorsHandler;
import com.miquido.vtv.codsservices.internal.httpjsonclient.CodsHttpJsonClient;
import com.miquido.vtv.codsservices.internal.httpjsonclient.CodsUrlParams;
import com.miquido.vtv.codsservices.internal.jsontransformers.SubcollectionJsonReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class ProgramsCodsDaoImpl implements ProgramsCodsDao {

  private static final Logger logger = LoggerFactory.getLogger(ProgramsCodsDaoImpl.class);

  public Program getCurrentProgramForChannel(String sessionId, String channelId) {
    CodsHttpJsonClient.JsonResponse jsonResponse = codsHttpJsonClient.get(String.format("/channels/%s/guide", channelId),
        CodsUrlParams.createNew().addSession(sessionId));

    logger.info("Guide response: {}", jsonResponse.getJsonObject().toString());

    codsErrorsHandler.handleCodsError(jsonResponse);

    Subcollection<Program> programs = programJsonReader.createObjectFromJson(jsonResponse.getJsonObject());
    List<Program> entries = programs.getEntries();
    if (!entries.isEmpty()) {
      Program result = entries.get(0);
      logger.info("Current program: {}", result.getName());
      CodsHttpJsonClient.JsonResponse actorsResponse = codsHttpJsonClient.get(String.format("/programs/%s/actors", result.getId().toString()), CodsUrlParams.createNew().addSession(sessionId));
      codsErrorsHandler.handleCodsError(jsonResponse);
      Subcollection<Actor> actors = actorJsonReader.createObjectFromJson(actorsResponse.getJsonObject());
      List<Actor> actorList = actors.getEntries();
      result.setActors(actorList.size() > 9 ? actorList.subList(0, 8) : actorList);
      return result;
    }
    logger.info("No current program found!");
    return null;
  }

  @Inject
  CodsHttpJsonClient codsHttpJsonClient;
  @Inject
  private CodsErrorsHandler codsErrorsHandler;
  @Inject
  SubcollectionJsonReader<Program> programJsonReader;
  @Inject
  SubcollectionJsonReader<Actor> actorJsonReader;
}
