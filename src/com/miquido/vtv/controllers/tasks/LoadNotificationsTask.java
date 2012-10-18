package com.miquido.vtv.controllers.tasks;

import android.content.Context;
import com.google.inject.Inject;
import com.miquido.vtv.domainservices.NotificationsService;
import com.miquido.vtv.events.commands.NotificationsLoadedCommand;
import com.miquido.vtv.events.modelchanges.NotificationsModelChanged;
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
public class LoadNotificationsTask extends SimpleRoboAsyncTask {
  private static final Logger logger = LoggerFactory.getLogger(LoadNotificationsTask.class);

  @Inject
  private NotificationsService notificationsService;

  @Inject
  private EventManager eventManager;

  @Inject
  public LoadNotificationsTask(Context context) {
    super(context);
    logger.debug("LoadNotificationsTask constructor. context:" + context);
  }

  @Override
  protected void onPreExecute() {
    try {
      super.onPreExecute();
      logger.debug("onPreExecute: notificatiosService: {}", notificationsService);
      logger.debug("onPreExecute: Event Manager: " + eventManager);
      notificationsService.setNotificationsLoading(true);
      // TODO
      eventManager.fire(new NotificationsModelChanged(notificationsService));
      logger.debug("onPreExecute: NotificationsModelChanged Event fired");
    } catch (Exception e) {
      logger.error("po ustawieniu stanu: wyjatek: " + e.getMessage());
    }
  }


  @Override
  public void doInBackground() throws Exception {
    logger.debug("doInBackground: Start loading notifications");
    notificationsService.loadNotifications();
    logger.debug("doInBackground: notifications loaded or not");
  }

  @Override
  protected void onFinal() throws RuntimeException {
    super.onFinal();
    logger.debug("onFinally: fire event");
    eventManager.fire(new NotificationsModelChanged(notificationsService));
    logger.debug("onFinally: NotificationsModelChanged Event fired");

    eventManager.fire(new NotificationsLoadedCommand());
  }


}
