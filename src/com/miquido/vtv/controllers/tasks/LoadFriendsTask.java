package com.miquido.vtv.controllers.tasks;

import android.content.Context;
import com.google.inject.Inject;
import com.miquido.vtv.domainservices.FriendsService;
import com.miquido.vtv.events.commands.FriendsLoadedCommand;
import com.miquido.vtv.events.modelchanges.FriendsModelChanged;
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
public class LoadFriendsTask extends SimpleRoboAsyncTask {
    private static final Logger logger = LoggerFactory.getLogger(LoadFriendsTask.class);

    @Inject
    private FriendsService friendsService;

    @Inject
    private EventManager eventManager;

    @Inject
    public LoadFriendsTask(Context context) {
        super(context);
        logger.debug("LoadFriendsTask constructor. context:"+context);
    }

    @Override
    protected void onPreExecute() {
        try {
            super.onPreExecute();
            logger.debug("onPreExecute: friendService: {}", friendsService);
            logger.debug("onPreExecute: Event Manager: " + eventManager);
            friendsService.setFriendsLoading(true);
            // TODO
            eventManager.fire(new FriendsModelChanged(friendsService));
            logger.debug("onPreExecute: FriendsModelChanged Event fired");
        } catch (Exception e) {
            logger.error("po ustawieniu stanu: wyjatek: "+e.getMessage());
        }
    }


    @Override
    public void doInBackground() throws Exception {
        logger.debug("doInBackground: Start loading friends");
        friendsService.loadFriends();
        logger.debug("doInBackground: friends loaded or not");
    }

    @Override
    protected void onFinal() throws RuntimeException {
        super.onFinal();
        logger.debug("onFinally: fire event");
        eventManager.fire(new FriendsModelChanged(friendsService));
        logger.debug("onFinally: FriendsModelChanged Event fired");

        eventManager.fire(new FriendsLoadedCommand());
    }


}
