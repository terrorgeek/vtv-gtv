package com.miquido.vtv.view.activity;

import android.app.ActivityManager;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import com.google.inject.Inject;
import com.miquido.vtv.R;
import com.miquido.vtv.controllers.MainController;
import com.miquido.vtv.events.commands.InitCommand;
import com.miquido.vtv.view.fragment.ButtonsFragment;
import com.miquido.vtv.view.fragment.FriendsFragment;
import com.miquido.vtv.domainservices.SessionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import roboguice.activity.RoboFragmentActivity;
import roboguice.inject.InjectFragment;

import java.util.List;


/**
 * Main activity of vTV application.
 *
 * @author Lukasz Jazgar - Miquido
 */
public class VTVMainActivity extends RoboFragmentActivity {

    private static final Logger logger = LoggerFactory.getLogger(VTVMainActivity.class);


    @InjectFragment(R.id.friendsFragment)
    FriendsFragment friendsFragment;
    @InjectFragment(R.id.buttonsFragment)
    ButtonsFragment buttonsFragment;

    @Inject
    MainController mainController;

    /**
     * Uri of current channel. In the beginning it is passthrough of video signal. It changes as a result of channel changing.
     * It's temporary. It should be stored somewhere else (eg. service or persistent storage)
     */
    String currentChannelUri = "tv://passthrough";


    // *****************************************************************************************************
    // ************************** LifeCycle CALLBACKS ******************************************************
    // *****************************************************************************************************

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        logger.debug("vTV: Starting application");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        logger.debug("onCreate: fire event initcommand: ");
        eventManager.fire(new InitCommand());
        logger.debug("onCreate: event initcommand fired");



    }


    @Override
    protected void onResume() {
        super.onResume();
        logger.debug("vTV: onResume");
        if (!isActivitiesOrderCorrect()) {
            logger.debug("vTV: activities order incorrect. Reorder.");
            showVTVonLiveTv(null);
        } else {
            logger.debug("vTV: activities order correct. Noop.");
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        logger.debug("vTV: onPause");
    }

    @Override
    protected void onStart() {
        super.onStart();
        logger.debug("vTV:onStart");
    }

    @Override
    protected void onStop() {
        super.onStop();
        logger.debug("vTV:onStop");
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        logger.debug("vTV: onRestart");
    }


    // *****************************************************************************************************
    // ************************** Button callbacks *********************************************************
    // *****************************************************************************************************


    /**
     * Overridden back button callback by empty code.
     * It's written to block possibility of turning off vTV application and leaving LiveTV alone.
     */
    @Override
    public void onBackPressed() {
        logger.debug("vTV: Back button pressed. No action.");

    }

    // *****************************************************************************************************
    // ************************** Other methods       ******************************************************
    // *****************************************************************************************************

    /**
     * Sets proper order of activities LiveTV and vTV in stack. As a result, vTV should be on top, LiveTV directly below it.
     * It's done by these steps:
     * 1. start intent to bring LiveTV on top
     * 2. start pending (after 1s) intent which bring vTV activity on top.
     *
     * @param channelUri channel uri. If set, channel will change. If null, channel will not change, only order of apps
     *                   will be set.
     */
    private void showVTVonLiveTv(String channelUri) {
        logger.debug("vTV: showVTVonLiveTv: channel="+channelUri);
        if (channelUri!=null) {
            currentChannelUri = channelUri;
        }
        logger.debug("vTV: channel to set:"+currentChannelUri);

        Intent liveTvIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(currentChannelUri));
        liveTvIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(liveTvIntent);
        logger.debug("vTV: liveTvIntent activity started");

        //Schedule application auto start
        Intent thisIntent = new Intent(this, VTVMainActivity.class);
        thisIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 678, thisIntent, PendingIntent.FLAG_ONE_SHOT);
        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        alarmManager.set(AlarmManager.RTC_WAKEUP, System.currentTimeMillis()+1000, pendingIntent);
        logger.debug("vTV: Set alarm manager to do pending intent showing VTVMainActivity after 1s");
    }

    private boolean isActivitiesOrderCorrect() {
        logger.debug("vTV: isActivitiesOrderCorrect");
        ActivityManager am = (ActivityManager) this.getSystemService(ACTIVITY_SERVICE);
        List< ActivityManager.RunningTaskInfo > taskInfo = am.getRunningTasks(Integer.MAX_VALUE);
        if (taskInfo.size()<2) {
            logger.debug("vTV: Number of tasks is less than 2. Result: false");
            return false;
        }

        return (taskInfo.get(0).topActivity.getClassName().equals("com.miquido.vtv.view.activity.VTVMainActivity") &&
                taskInfo.get(1).topActivity.getClassName().equals("com.google.tv.player.PlayerActivity") );
    }

}
