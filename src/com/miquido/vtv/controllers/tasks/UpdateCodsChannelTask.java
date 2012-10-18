package com.miquido.vtv.controllers.tasks;

import android.content.Context;
import com.google.inject.Inject;
import com.miquido.DemoChannels;
import com.miquido.vtv.domainservices.CurrentChannelService;
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
public class UpdateCodsChannelTask extends SimpleRoboAsyncTask {
    private static final Logger logger = LoggerFactory.getLogger(UpdateCodsChannelTask.class);

    @Inject
    private CurrentChannelService currentChannelService;
    @Inject
    private EventManager eventManager;

    @Inject
    public UpdateCodsChannelTask(Context context) {
        super(context);
        logger.debug("UpdateCodsChannelTask constructor. context:"+context);
    }

    @Override
    protected void onPreExecute() {
    }


    @Override
    public void doInBackground() throws Exception {
        logger.debug("doInBackground: Start updating current channel in cods");
        currentChannelService.updateCodsChannel();
        logger.debug("doInBackground: Current channel updated in CODS");
    }

    @Override
    protected void onFinal() throws RuntimeException {
        super.onFinal();
    }


}
