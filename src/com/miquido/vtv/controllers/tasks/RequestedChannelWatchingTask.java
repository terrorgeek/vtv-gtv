package com.miquido.vtv.controllers.tasks;

import android.content.Context;
import android.os.SystemClock;
import com.google.inject.Inject;
import com.miquido.DemoChannels;
import com.miquido.vtv.bo.Id;
import com.miquido.vtv.domainservices.CurrentChannelService;
import com.miquido.vtv.events.modelchanges.CurrentChannelChanged;
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
public class RequestedChannelWatchingTask extends SimpleRoboAsyncTask {
  private static final Logger logger = LoggerFactory.getLogger(RequestedChannelWatchingTask.class);

  @Inject
  CurrentChannelService currentChannelService;
  @Inject
  EventManager eventManager;
  @Inject
  TaskProvider<UpdateCodsChannelTask> updateCodsChannelTaskProvider;

  @Inject
  public RequestedChannelWatchingTask(Context context) {
    super(context);
    logger.debug("RequestedChannelWatchingTask constructor. context:" + context);
  }

  @Override
  protected void onPreExecute() {
    super.onPreExecute();
    logger.debug("onPreExecute");
  }

  @Override
  public void doInBackground() throws Exception {
    logger.debug("doInBackground: Start background task watching requested channel id");
    SystemClock.sleep(5000);
    while (!isCancelled()) {
      currentChannelService.updateRequestedChannelId();
      publishProgress();
      logger.debug("doInBackground: Requested channel id updated from cods");
      SystemClock.sleep(5000);
    }
    logger.debug("doInBackground: Finished");
  }

  @Override
  protected void onProgressUpdate(Void... values) {
    super.onProgressUpdate(values);
    logger.debug("onProgressUpdate: Requested channel updated");
    Id requestedChannelId = currentChannelService.getRemotelyRequestedChannelChange();
    if (requestedChannelId!=null) {
      currentChannelService.changeChannel(requestedChannelId);
//      if (currentChannelService.codsChannelNeedsChange()) {
        logger.debug("onChannelSelected: Cods currentProfile needs change");
        updateCodsChannelTaskProvider.get().execute();
//      }
      eventManager.fire(new CurrentChannelChanged(currentChannelService));
    }
  }

  @Override
  protected void onCancelled() {
    super.onCancelled();    //To change body of overridden methods use File | Settings | File Templates.
    logger.debug("onCancelled: Friends updating task cancelled!!!");
  }
}
