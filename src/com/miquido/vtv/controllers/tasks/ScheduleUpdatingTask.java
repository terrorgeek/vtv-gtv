package com.miquido.vtv.controllers.tasks;

import android.content.Context;
import android.os.SystemClock;
import com.google.inject.Inject;
import com.miquido.vtv.domainservices.ScheduleService;
import com.miquido.vtv.events.modelchanges.ScheduleModelChanged;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import roboguice.event.EventManager;

public class ScheduleUpdatingTask extends SimpleRoboAsyncTask {
  private static final Logger logger = LoggerFactory.getLogger(ScheduleUpdatingTask.class);

  @Inject
  ScheduleService scheduleService;
  @Inject
  EventManager eventManager;

  @Inject
  public ScheduleUpdatingTask(Context context) {
    super(context);
  }

  @Override
  public void doInBackground() throws Exception {
    logger.debug("doInBackground: Start background task updating schedule");
    SystemClock.sleep(10000);
    while (!isCancelled()) {
      scheduleService.updateSchedule();
      publishProgress();
      logger.debug("doInBackground: Schedule loaded and updated");
      SystemClock.sleep(10000);
    }
    logger.debug("doInBackground: Finished");
  }

  @Override
  protected void onProgressUpdate(Void... values) {
    super.onProgressUpdate(values);
    logger.debug("onProgressUpdate: Schedule loaded and updated");
    eventManager.fire(new ScheduleModelChanged(scheduleService));
  }

  @Override
  protected void onCancelled() {
    super.onCancelled();
    logger.debug("onCancelled: Notifications updating task cancelled!!!");
  }
}
