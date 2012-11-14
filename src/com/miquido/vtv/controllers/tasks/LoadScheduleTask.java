package com.miquido.vtv.controllers.tasks;

import android.content.Context;
import com.google.inject.Inject;
import com.miquido.vtv.domainservices.ScheduleService;
import com.miquido.vtv.events.modelchanges.ScheduleLoadedCommand;
import com.miquido.vtv.events.modelchanges.ScheduleModelChanged;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import roboguice.event.EventManager;

public class LoadScheduleTask extends SimpleRoboAsyncTask {
  private static final Logger logger = LoggerFactory.getLogger(LoadScheduleTask.class);

  @Inject
  private ScheduleService scheduleService;

  @Inject
  private EventManager eventManager;

  @Inject
  public LoadScheduleTask(Context context) {
    super(context);
    logger.debug("LoadScheduleTask constructor. context:" + context);
  }

  @Override
  protected void onPreExecute() {
    try {
      super.onPreExecute();
      eventManager.fire(new ScheduleModelChanged(scheduleService));
      logger.debug("onPreExecute: ScheduleModelChanged Event fired");
    } catch (Exception e) {
      logger.error("po ustawieniu stanu: wyjatek: " + e.getMessage());
    }
  }


  @Override
  public void doInBackground() throws Exception {
    logger.debug("doInBackground: Start loading schedule");
    scheduleService.loadSchedule();
    logger.debug("doInBackground: schedule loaded or not");
  }

  @Override
  protected void onFinal() throws RuntimeException {
    super.onFinal();
    eventManager.fire(new ScheduleModelChanged(scheduleService));
    logger.debug("onFinally: ScheduleModelChanged Event fired");

    eventManager.fire(new ScheduleLoadedCommand());
  }


}
