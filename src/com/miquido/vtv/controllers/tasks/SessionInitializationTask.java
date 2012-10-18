package com.miquido.vtv.controllers.tasks;

import android.content.Context;
import com.google.inject.Inject;
import com.miquido.vtv.domainservices.ChannelsService;
import com.miquido.vtv.domainservices.PanelsStateService;
import com.miquido.vtv.domainservices.SessionService;
import com.miquido.vtv.events.commands.InitCurrentChannelCommand;
import com.miquido.vtv.events.modelchanges.ChannelsModelChanged;
import com.miquido.vtv.events.modelchanges.PanelsStateChanged;
import com.miquido.vtv.events.modelchanges.SessionModelChanged;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import roboguice.RoboGuice;
import roboguice.event.EventManager;

/**
 * Created with IntelliJ IDEA.
 * User: ljazgar
 * Date: 03.08.12
 * Time: 14:47
 * To change this template use File | Settings | File Templates.
 */
public class SessionInitializationTask extends SimpleRoboAsyncTask {

  private static final Logger logger = LoggerFactory.getLogger(SessionInitializationTask.class);

  @Inject
  private SessionService sessionService;
  @Inject
  private PanelsStateService panelsStateService;
  @Inject
  ChannelsService channelsService;
  @Inject
  private EventManager eventManager;

  @Inject
  public SessionInitializationTask(Context context) {
    super(context);
    logger.debug("SessionInitializationTask constructor.");
  }


  @Override
  protected void onPreExecute() {
    try {
      super.onPreExecute();
      logger.debug("onPreExecute: sessionService: {}", sessionService);
      logger.debug("onPreExecute: Event Manager: " + eventManager);
      sessionService.setSessionInitializing();
      eventManager.fire(new SessionModelChanged(sessionService));
      logger.debug("onPreExecute: SessionModelChanged Event fired");
    } catch (Exception e) {
      logger.error("po ustawieniu stanu: wyjatek: " + e.getMessage());
    }
  }

  @Override
  public void doInBackground() throws Exception {
    logger.debug("call: Session initialization start");
    sessionService.initialize();
    logger.debug("call: Session initialized");
  }


  @Override
  protected void onFinal() throws RuntimeException {
    logger.debug("onFinally: fire event");
    panelsStateService.setApplicationVisible(false);
    eventManager.fire(new SessionModelChanged(sessionService));
    eventManager.fire(new PanelsStateChanged(panelsStateService));
    eventManager.fire(new ChannelsModelChanged(channelsService));
    eventManager.fire(new InitCurrentChannelCommand());
    logger.debug("onFinally: SessionModelChanged Event fired");
    LoadFriendsTask loadFriendsTask = RoboGuice.getInjector(context).getInstance(LoadFriendsTask.class);
    loadFriendsTask.execute();
    LoadNotificationsTask loadNotificationsTask = RoboGuice.getInjector(context).getInstance(LoadNotificationsTask.class);
    loadNotificationsTask.execute();
  }

}
