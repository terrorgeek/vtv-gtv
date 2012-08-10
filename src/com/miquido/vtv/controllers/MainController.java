package com.miquido.vtv.controllers;

import android.content.Context;
import android.os.Handler;
import com.google.inject.Inject;
import com.miquido.vtv.controllers.tasks.LoginTask;
import com.miquido.vtv.domainservices.FriendsService;
import com.miquido.vtv.domainservices.PanelsStateService;
import com.miquido.vtv.domainservices.SessionService;
import com.miquido.vtv.events.commands.FriendsButtonToggledCommand;
import com.miquido.vtv.events.commands.InitCommand;
import com.miquido.vtv.events.commands.LoginButtonClickedCommand;
import com.miquido.vtv.events.modelchanges.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import roboguice.event.EventManager;
import roboguice.event.Observes;
import roboguice.inject.ContextSingleton;


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

    @Inject EventManager eventManager;
    @Inject SessionService sessionService;
    @Inject FriendsService friendsService;
    @Inject PanelsStateService panelsStateService;
    @Inject Context context;

    public MainController() {
        logger.debug("constructor");
    }

    public void onInit(@Observes InitCommand initCommand) {
        logger.debug("onInit: start");
        eventManager.fire(new SessionModelChanged(sessionService));
        eventManager.fire(new FriendsModelInitialized(friendsService));
        eventManager.fire(new PanelsStateChanged(panelsStateService));
        logger.debug("onInit: eventFired. Execute LoginTask");
        new LoginTask(context).execute();
        logger.debug("onInit: LoginTask executed");
    }

    public void onLoginButtonClicked(@Observes LoginButtonClickedCommand loginButtonClickedCommand) {
        if (sessionService.isLoggingIn())
            return; // TODO interruption of logging in

        if (sessionService.isLoggedIn()) {
            sessionService.logout();
            eventManager.fire(new SessionModelChanged(sessionService));
            eventManager.fire(new PanelsStateChanged(panelsStateService));
            eventManager.fire(new FriendsModelCleared(friendsService));
        } else {
            logger.debug("onLoginButtonClicked: Execute LoginTask");
            new LoginTask(context).execute();
            logger.debug("onLoginButtonClicked: LoginTask executed");
        }

    }

    public void onFriendsButtonToggled(@Observes FriendsButtonToggledCommand friendsButtonToggledCommand) {
        panelsStateService.setFriendsPanelOn(friendsButtonToggledCommand.isFriendsButtonChecked());
        eventManager.fire(new PanelsStateChanged(panelsStateService));
    }
}
