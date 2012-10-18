package com.miquido.vtv.controllers.tasks;

import android.content.Context;
import com.google.inject.Inject;
import com.miquido.vtv.bo.Notification;
import com.miquido.vtv.domainservices.NotificationsService;
import lombok.Setter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import roboguice.event.EventManager;

/**
 * Created with IntelliJ IDEA.
 * User: ljazgar
 * Date: 03.08.12
 * Time: 16:11
 * To change this template use File | Settings | File Templates.
 */
public class UpdateCodsNotificationTask extends SimpleRoboAsyncTask {
  private static final Logger logger = LoggerFactory.getLogger(UpdateCodsNotificationTask.class);

  @Inject
  private NotificationsService notificationsService;
  @Inject
  private EventManager eventManager;
  @Setter
  private Notification notification;

  @Inject
  public UpdateCodsNotificationTask(Context context) {
    super(context);
    logger.debug("UpdateCodsNotificationTask constructor. context:" + context);
  }

  @Override
  protected void onPreExecute() {
  }


  @Override
  public void doInBackground() throws Exception {
    logger.debug("doInBackground: Start updating notification in cods");
    notificationsService.updateCodsNotificationStatus(notification);
    logger.debug("doInBackground: Notification updated in CODS");
  }

  @Override
  protected void onFinal() throws RuntimeException {
    super.onFinal();
  }


}
