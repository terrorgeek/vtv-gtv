/**
 * Created by raho on 10/9/12.
 *
 * Copyright 2012 MiQUiDO <http://www.miquido.com/>. All rights reserved.
 */
package com.miquido.vtv.domainservices;

import com.google.inject.Inject;
import com.miquido.MockData;
import com.miquido.vtv.bo.Id;
import com.miquido.vtv.bo.Program;
import com.miquido.vtv.codsservices.ProgramsCodsDao;
import com.miquido.vtv.events.modelchanges.CurrentlyWatchedProgramChanged;
import com.miquido.vtv.repositories.ProgramsRepository;
import com.miquido.vtv.repositories.SessionRepository;
import com.miquido.vtv.viewmodel.ProgramsViewModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import roboguice.event.EventManager;

public class ProgramsService implements ProgramsViewModel {

  private static final Logger logger = LoggerFactory.getLogger(ProgramsService.class);

  public void updateCurrentlyWatchedProgram() {
    logger.info("Updating currently watched program...");
    Id channelId = currentChannelService.getCurrentlyWatchedChannel().getId();
    Program currentProgram = getCurrentProgramForChannel(channelId);
    programsRepository.setCurrentlyWatchedProgram(currentProgram);
    eventManager.fire(new CurrentlyWatchedProgramChanged(this));
  }

  private Program getCurrentProgramForChannel(Id channelId) {
    return MockData.getProgramForChannel(channelId);
    //return programsCodsDao.getCurrentProgramForChannel(sessionRepository.getSession().getId(), channelId.toString());
  }

  public Program getCurrentlyWatchedProgram() {
    return programsRepository.getCurrentlyWatchedProgram();
  }

  @Inject
  ProgramsCodsDao programsCodsDao;
  @Inject
  SessionRepository sessionRepository;
  @Inject
  ProgramsRepository programsRepository;
  @Inject
  EventManager eventManager;
  @Inject
  CurrentChannelService currentChannelService;

}