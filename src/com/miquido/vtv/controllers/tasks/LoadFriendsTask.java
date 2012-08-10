package com.miquido.vtv.controllers.tasks;

import android.content.Context;
import android.os.AsyncTask;
import com.google.inject.Inject;
import com.miquido.vtv.domainservices.FriendsService;
import com.miquido.vtv.events.modelchanges.FriendsLoadingFinished;
import com.miquido.vtv.events.modelchanges.FriendsLoadingStarted;
import com.miquido.vtv.events.modelchanges.SessionModelChanged;
import com.miquido.vtv.view.fragment.FriendsFragment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import roboguice.event.EventManager;
import roboguice.util.RoboAsyncTask;

/**
 * Created with IntelliJ IDEA.
 * User: ljazgar
 * Date: 03.08.12
 * Time: 16:11
 * To change this template use File | Settings | File Templates.
 */
public class LoadFriendsTask extends RoboAsyncTask<Void> {
    private static final Logger logger = LoggerFactory.getLogger(LoadFriendsTask.class);

    @Inject
    private FriendsService friendsService;

    @Inject
    private EventManager eventManager;

    public LoadFriendsTask(Context context) {
        super(context);
        logger.debug("LoadFriendsTask constructor.");
    }

    @Override
    protected void onPreExecute() throws Exception {
        try {
            super.onPreExecute();
            logger.debug("onPreExecute: friendService: {}", friendsService);
            logger.debug("onPreExecute: Event Manager: " + eventManager);
            friendsService.setFriendsLoading(true);
            // TODO
            eventManager.fire(new FriendsLoadingStarted(friendsService));
            logger.debug("onPreExecute: SessionModelChanged Event fired");
        } catch (Exception e) {
            logger.error("po ustawieniu stanu: wyjatek: "+e.getMessage());
            throw e;
        }
    }


    @Override
    public Void call() throws Exception {
        logger.debug("call: Start loading friends");
        friendsService.loadFriends();
        logger.debug("call: friends loaded or not");
        return null;
    }

    @Override
    protected void onFinally() throws RuntimeException {
        super.onFinally();    //To change body of overridden methods use File | Settings | File Templates.
        logger.debug("onFinally: fire event");
        eventManager.fire(new FriendsLoadingFinished(friendsService));
        logger.debug("onFinally: FriendsLoadingFinished Event fired");
    }


}
