/**
 * Created by raho on 10/9/12.
 *
 * Copyright 2012 MiQUiDO <http://www.miquido.com/>. All rights reserved.
 */
package com.miquido.vtv.controllers.tasks;

import android.content.Context;
import com.google.inject.Inject;
import com.miquido.vtv.domainservices.ProgramsService;
import com.miquido.vtv.domainservices.SessionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LoadCurrentProgramTask extends SimpleRoboAsyncTask {

  private static final Logger logger = LoggerFactory.getLogger(LoadCurrentProgramTask.class);

  @Inject
  private SessionService sessionService;

  @Inject
  ProgramsService programsService;

  @Inject
  public LoadCurrentProgramTask(Context context) {
    super(context);
    logger.debug("LoadCurrentProgramTask constructor. context:" + context);
  }

  @Override
  protected void onPreExecute() {
    super.onPreExecute();
  }

  @Override
  protected void doInBackground() throws Exception {
    programsService.updateCurrentlyWatchedProgram();
  }

  @Override
  protected void onFinal() {
    super.onFinal();
  }
}
