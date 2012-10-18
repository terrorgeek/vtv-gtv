package com.miquido.vtv.controllers.tasks;

import android.content.Context;
import android.os.SystemClock;
import com.google.inject.Inject;
import com.miquido.vtv.domainservices.FriendsService;
import com.miquido.vtv.events.modelchanges.FriendsModelChanged;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import roboguice.event.EventManager;

/**
 * Created with IntelliJ IDEA.
 * User: ljazgar
 * Date: 14.09.12
 * Time: 16:18
 * To change this template use File | Settings | File Templates.
 */
public class FriendsUpdatingTask extends SimpleRoboAsyncTask {
  private static final Logger logger = LoggerFactory.getLogger(FriendsUpdatingTask.class);

  @Inject
  FriendsService friendsService;
  @Inject
  EventManager eventManager;

  @Inject
  public FriendsUpdatingTask(Context context) {
    super(context);
    logger.debug("FriendsUpdatingTask constructor. context:" + context);
  }

  @Override
  protected void onPreExecute() {
    super.onPreExecute();
    logger.debug("onPreExecute");
  }

  @Override
  public void doInBackground() throws Exception {
    logger.debug("doInBackground: Start background task updating friends");
    SystemClock.sleep(10000);
    while (!isCancelled()) {
      friendsService.updateFriends();
      publishProgress();
      logger.debug("doInBackground: Friends loaded and updated");
      SystemClock.sleep(10000);
    }
    logger.debug("doInBackground: Finished");
  }

  @Override
  protected void onProgressUpdate(Void... values) {
    super.onProgressUpdate(values);
    logger.debug("onProgressUpdate: Friends loaded and updated");
    eventManager.fire(new FriendsModelChanged(friendsService));
  }

  @Override
  protected void onCancelled() {
    super.onCancelled();    //To change body of overridden methods use File | Settings | File Templates.
    logger.debug("onCancelled: Friends updating task cancelled!!!");
  }
}
