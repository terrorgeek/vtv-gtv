package com.miquido.vtv.controllers;

import android.content.Context;
import android.widget.Toast;
import com.google.inject.Inject;
import com.miquido.DemoChannels;
import com.miquido.vtv.bo.Channel;
import com.miquido.vtv.bo.Id;
import com.miquido.vtv.bo.Notification;
import com.miquido.vtv.bo.WatchWithMeNotification;
import com.miquido.vtv.controllers.tasks.*;
import com.miquido.vtv.domainservices.*;
import com.miquido.vtv.events.commands.*;
import com.miquido.vtv.events.modelchanges.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import roboguice.event.EventManager;
import roboguice.event.Observes;
import roboguice.inject.ContextSingleton;

import java.text.ParseException;


/**
 * Created with IntelliJ IDEA.
 * User: ljazgar
 * Date: 09.08.12
 * Time: 02:08
 * To change this template use File | Settings | File Templates.
 */
@ContextSingleton
public class MainController {
  private static final Logger logger = LoggerFactory.getLogger(MainController.class);

  @Inject
  EventManager eventManager;
  @Inject
  SessionService sessionService;
  @Inject
  FriendsService friendsService;
  @Inject
  NotificationsService notificationsService;
  @Inject
  PanelsStateService panelsStateService;
  @Inject
  CurrentChannelService currentChannelService;
  @Inject
  Context context;
  @Inject
  TaskProvider<LoginTask> loginTaskProvider;
  @Inject
  TaskProvider<UpdateCodsChannelTask> updateCodsChannelTaskProvider;
  @Inject
  TaskProvider<FriendsUpdatingTask> friendsUpdatingTaskProvider;
  @Inject
  TaskProvider<NotificationsUpdatingTask> notificationsUpdatingTaskTaskProvider;
  @Inject
  TaskProvider<UpdateCodsNotificationTask> updateCodsNotificationTaskProvider;
  @Inject
  TaskProvider<CreateCodsNotificationTask> createCodsNotificationTaskProvider;
  @Inject
  TaskProvider<RequestedChannelWatchingTask> requestedChannelWatchingTaskProvider;

  FriendsUpdatingTask friendsUpdatingTask = null;
  RequestedChannelWatchingTask requestedChannelWatchingTask = null;
  NotificationsUpdatingTask notificationsUpdatingTask = null;
  UpdateCodsNotificationTask updateCodsNotificationTask = null;
  CreateCodsNotificationTask createCodsNotificationTask = null;

  public MainController() {
    logger.debug("constructor");
  }

  public void onInit(@Observes InitCommand initCommand) {
    logger.debug("onInit: start");
    eventManager.fire(new SessionModelChanged(sessionService));
    eventManager.fire(new FriendsModelChanged(friendsService));
    eventManager.fire(new NotificationsModelChanged(notificationsService));
    eventManager.fire(new PanelsStateChanged(panelsStateService));
    eventManager.fire(new CurrentChannelChanged(currentChannelService));
    logger.debug("onInit: eventFired. Execute LoginTask");
    logger.debug("onInit: context:" + context);
    logger.debug("onInit: eventManager:" + eventManager);
    loginTaskProvider.get().execute();
    logger.debug("onInit: LoginTask executed");
  }

  public void onLoginButtonClicked(@Observes LoginButtonClickedCommand loginButtonClickedCommand) {
    logger.debug("onLoginButtonClicked:");
    if (!sessionService.isLoggedIn() || !sessionService.isSessionInitialized())
      return; // No action when application is not logged in and initialized

    logger.debug("changing visibility");
    panelsStateService.toggleApplicationVisibility();
    logger.debug(String.format("Application visibility changed to:%b", panelsStateService.isApplicationVisible()));

    eventManager.fire(new PanelsStateChanged(panelsStateService));
  }

  public void onInitCurrentChannel(@Observes InitCurrentChannelCommand initCurrentChannelCommand) {
    boolean codsNeedsUpdate = currentChannelService.initiateCurrentlyWatchedChannel();

    Channel channel = currentChannelService.getCurrentlyWatchedChannel();
    logger.debug(String.format("onInitCurrentChannel: set current channel to: %s  Changed? %b",
        ((channel != null) ? channel.getCallSign() : null), codsNeedsUpdate));

    if (codsNeedsUpdate) {
      logger.debug("onInitCurrentChannel: Cods currentProfile needs change");
      updateCodsChannelTaskProvider.get().execute();
    }

    /*requestedChannelWatchingTask = requestedChannelWatchingTaskProvider.get();
    requestedChannelWatchingTask.execute();*/

    eventManager.fire(new CurrentChannelChanged(currentChannelService));
  }

  public void onChannelSelected(@Observes ClickedChannelCommand clickedChannelCommand) {
    logger.debug("onChannelSelected: " + clickedChannelCommand.getClickedChannelId());
    currentChannelService.changeChannel(clickedChannelCommand.getClickedChannelId());
    panelsStateService.setChannelsPanelOn(false);
    if (currentChannelService.codsChannelNeedsChange()) {
      logger.debug("onChannelSelected: Cods currentProfile needs change");
      updateCodsChannelTaskProvider.get().execute();
    }
    eventManager.fire(new CurrentChannelChanged(currentChannelService));
    eventManager.fire(new PanelsStateChanged(panelsStateService));
  }

  public void onFriendsButtonToggled(@Observes FriendsButtonToggledCommand friendsButtonToggledCommand) {
    logger.debug("onFriendsButtonToggled: " + friendsButtonToggledCommand.isFriendsButtonChecked());
    panelsStateService.setFriendsPanelOn(friendsButtonToggledCommand.isFriendsButtonChecked());
    eventManager.fire(new PanelsStateChanged(panelsStateService));
  }

  public void onChannelsButtonToggled(@Observes ChannelsButtonToggledCommand channelsButtonToggledCommand) {
    logger.debug("onChannelsButtonToggled: " + channelsButtonToggledCommand.isChannelsButtonChecked());
    panelsStateService.setChannelsPanelOn(channelsButtonToggledCommand.isChannelsButtonChecked());
    eventManager.fire(new PanelsStateChanged(panelsStateService));
  }

  public void onScheduleButtonToggled(@Observes ScheduleProgramButtonToggledCommand scheduleProgramButtonToggledCommand) {
    logger.debug("onScheduleButtonToggled: " + scheduleProgramButtonToggledCommand.isScheduleProgramButtonChecked());
    panelsStateService.setSchedulePanelOn(scheduleProgramButtonToggledCommand.isScheduleProgramButtonChecked());
    eventManager.fire(new PanelsStateChanged(panelsStateService));
  }

  public void onNotificationsButtonToggled(@Observes NotificationsButtonToggledCommand notificationsButtonToggledCommand) {
    logger.debug("onNotificationsButtonToggled: " + notificationsButtonToggledCommand.isNotificationsButtonChecked());
    panelsStateService.setNotificationsPanelOn(notificationsButtonToggledCommand.isNotificationsButtonChecked());
    eventManager.fire(new PanelsStateChanged(panelsStateService));
  }

  public void onProgramInfoButtonToggled(@Observes ProgramInfoButtonToggledCommand programInfoButtonToggledCommand) {
    logger.debug("onProgramInfoButtonToggled: " + programInfoButtonToggledCommand.isProgramInfoButtonChecked());
    panelsStateService.setProgramInfoPanelOn(programInfoButtonToggledCommand.isProgramInfoButtonChecked());
    eventManager.fire(new PanelsStateChanged(panelsStateService));
  }

  public void onDashboardButtonToggled(@Observes DashboardButtonToggledCommand dashboardButtonToggledCommand) {
    logger.debug("onDashboardButtonToggled: " + dashboardButtonToggledCommand.isDashboardButtonChecked());
    panelsStateService.setDashboardPanelOn(dashboardButtonToggledCommand.isDashboardButtonChecked());
    eventManager.fire(new PanelsStateChanged(panelsStateService));
  }

  public void onInviteToWatchTogetherButtonClicked(@Observes InviteToWatchTogetherButtonClicked command) {
    logger.debug("onInviteToWatchTogetherButtonClicked: " + command.getFriendship());
    Channel channel = currentChannelService.getCurrentlyWatchedChannel();
    WatchWithMeNotification notification = new WatchWithMeNotification();
    notification.setStatus(WatchWithMeNotification.Status.New);
    notification.setFromProfileId(command.getFriendship().getProfileId());
    notification.setToProfileId(command.getFriendship().getFriendProfileId());
    notification.setSubjectId(channel.getId());
    Toast.makeText(context, "You've just invited " + command.getFriendship().getFriend().getName() + " to watch with you.", Toast.LENGTH_SHORT).show();
    createCodsNotificationTask = createCodsNotificationTaskProvider.get();
    createCodsNotificationTask.setNotification(notification);
    createCodsNotificationTask.execute();
  }

  public void onNotificationAcceptButtonClicked(@Observes NotificationAcceptButtonClicked command) {
    logger.debug("onNotificationAcceptButtonClicked: " + command.getNotification());
    Notification notification = command.getNotification();
    if (notification instanceof WatchWithMeNotification) {
      ((WatchWithMeNotification) notification).setStatus(WatchWithMeNotification.Status.Accepted);
      updateCodsNotificationTask = updateCodsNotificationTaskProvider.get();
      updateCodsNotificationTask.setNotification(notification);
      updateCodsNotificationTask.execute();
      eventManager.fire(new ClickedChannelCommand(notification.getSubjectId()));
    } else {
      logger.debug("Unknown type of notification, ignoring");
    }
  }

  public void onFriendsLoadedCommand(@Observes FriendsLoadedCommand command) {
    logger.debug("onFriendsLoadedCommand");
    friendsUpdatingTask = friendsUpdatingTaskProvider.get();
    friendsUpdatingTask.execute();
  }

  public void onNotificationsLoadedCommand(@Observes NotificationsLoadedCommand command) {
    logger.debug("onNotificationsLoadedCommand");
    notificationsUpdatingTask = notificationsUpdatingTaskTaskProvider.get();
    notificationsUpdatingTask.execute();
  }
}
