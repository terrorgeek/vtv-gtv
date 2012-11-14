package com.miquido.vtv.codsservices.internal.impl;

import com.google.inject.Inject;
import com.miquido.vtv.bo.GuideEntry;
import com.miquido.vtv.bo.Program;
import com.miquido.vtv.codsservices.ProgramGuideCodsDao;
import com.miquido.vtv.codsservices.internal.httpjsonclient.CodsErrorsHandler;
import com.miquido.vtv.codsservices.internal.httpjsonclient.CodsHttpJsonClient;
import com.miquido.vtv.codsservices.internal.httpjsonclient.CodsUrlParams;
import com.miquido.vtv.codsservices.internal.jsontransformers.JsonReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ProgramGuideCodsDaoImpl implements ProgramGuideCodsDao {

  private static final Logger logger = LoggerFactory.getLogger(ProgramGuideCodsDaoImpl.class);

  @Override
  public GuideEntry getGuideById(String sessionId, String guideId) {
    CodsHttpJsonClient.JsonResponse jsonResponse = codsHttpJsonClient.get(String.format("/guide/%s", guideId),
        CodsUrlParams.createNew().addSession(sessionId));

    logger.info("Guide entry response: {}", jsonResponse.getJsonObject().toString());

    codsErrorsHandler.handleCodsError(jsonResponse);

    GuideEntry guideEntry = guideEntryJsonReader.createObjectFromJson(jsonResponse.getJsonObject());
    if (guideEntry != null) {
      logger.info("Guide entry found for id: {}", guideEntry.getId());
      CodsHttpJsonClient.JsonResponse programJsonResponse = codsHttpJsonClient.get(String.format("/programs/%s", guideEntry.getProgramId()),
          CodsUrlParams.createNew().addSession(sessionId));

      logger.info("Program details response: {}", programJsonResponse.getJsonObject().toString());

      codsErrorsHandler.handleCodsError(programJsonResponse);

      Program program = programJsonReader.createObjectFromJson(programJsonResponse.getJsonObject());
      if (program != null) {
        guideEntry.setProgram(program);
      } else {
        logger.info("No program with id: {} found!", guideEntry.getProgramId());
      }
      return guideEntry;
    }
    logger.info("No guide with id: {} found!", guideId);
    return null;
  }

  @Inject
  CodsHttpJsonClient codsHttpJsonClient;
  @Inject
  CodsErrorsHandler codsErrorsHandler;
  @Inject
  JsonReader<GuideEntry> guideEntryJsonReader;
  @Inject
  JsonReader<Program> programJsonReader;
}
