package com.miquido.vtv.controllers.tasks;

import android.content.Context;
import android.os.SystemClock;
import android.widget.Toast;
import com.google.inject.Inject;
import com.miquido.vtv.R;
import com.miquido.vtv.bo.Notification;
import com.miquido.vtv.domainservices.FriendsService;
import com.miquido.vtv.domainservices.NotificationsService;
import com.miquido.vtv.events.modelchanges.NotificationsModelChanged;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import roboguice.event.EventManager;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: ljazgar
 * Date: 14.09.12
 * Time: 16:18
 * To change this template use File | Settings | File Templates.
 */
public class NotificationsUpdatingTask extends SimpleRoboAsyncTask {
  private static final Logger logger = LoggerFactory.getLogger(NotificationsUpdatingTask.class);

  @Inject
  NotificationsService notificationsService;
  @Inject
  FriendsService friendsService;
  @Inject
  EventManager eventManager;
  private List<Notification> notifications;

  @Inject
  public NotificationsUpdatingTask(Context context) {
    super(context);
    logger.debug("NotificationsUpdatingTask constructor. context:" + context);
  }

  @Override
  protected void onPreExecute() {
    super.onPreExecute();
    logger.debug("onPreExecute");
  }

  @Override
  public void doInBackground() throws Exception {
    logger.debug("doInBackground: Start background task updating notifications");
    SystemClock.sleep(10000);
    while (!isCancelled()) {
      notifications = notificationsService.updateNotifications();
      publishProgress();
      logger.debug("doInBackground: Notifications loaded and updated");
      SystemClock.sleep(10000);
    }
    logger.debug("doInBackground: Finished");
  }

  @Override
  protected void onProgressUpdate(Void... values) {
    super.onProgressUpdate(values);
    logger.debug("onProgressUpdate: Notifications loaded and updated");
    if (notifications != null) {
      for (Notification notification : notifications) {
        Toast.makeText(context, context.getString(R.string.new_notification, friendsService.getFriendNameByProfileId(notification.getFromProfileId())), Toast.LENGTH_LONG).show();
      }
    }
    eventManager.fire(new NotificationsModelChanged(notificationsService));
  }

  @Override
  protected void onCancelled() {
    super.onCancelled();
    logger.debug("onCancelled: Notifications updating task cancelled!!!");
  }
}
