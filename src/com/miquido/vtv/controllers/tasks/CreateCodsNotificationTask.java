package com.miquido.vtv.controllers.tasks;

import android.content.Context;
import com.google.inject.Inject;
import com.miquido.vtv.bo.WatchWithMeNotification;
import com.miquido.vtv.domainservices.NotificationsService;
import lombok.Setter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import roboguice.event.EventManager;

public class CreateCodsNotificationTask extends SimpleRoboAsyncTask {
  private static final Logger logger = LoggerFactory.getLogger(CreateCodsNotificationTask.class);

  @Inject
  private NotificationsService notificationsService;
  @Inject
  private EventManager eventManager;
  @Setter
  private WatchWithMeNotification notification;

  @Inject
  public CreateCodsNotificationTask(Context context) {
    super(context);
    logger.debug("UpdateCodsNotificationTask constructor. context:" + context);
  }

  @Override
  protected void onPreExecute() {
  }


  @Override
  public void doInBackground() throws Exception {
    logger.debug("doInBackground: Start updating notification in cods");
    notificationsService.createWatchWithMeNotification(notification);
    logger.debug("doInBackground: Notification updated in CODS");
  }

  @Override
  protected void onFinal() throws RuntimeException {
    super.onFinal();
  }


}
