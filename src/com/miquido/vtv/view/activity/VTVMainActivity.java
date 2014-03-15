package com.miquido.vtv.view.activity;

import android.app.ActivityManager;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.inject.Inject;
import com.miquido.vtv.R;
import com.miquido.vtv.controllers.MainController;
import com.miquido.vtv.events.commands.InitCommand;
import com.miquido.vtv.events.modelchanges.CurrentChannelChanged;
import com.miquido.vtv.view.fragment.*;
import jphonelite.Engine;
import lombok.Getter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import roboguice.activity.RoboFragmentActivity;
import roboguice.event.Observes;
import roboguice.inject.InjectFragment;

import java.util.ArrayList;
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
  @InjectFragment(R.id.programInfoFragment)
  ProgramInfoFragment programInfoFragment;
  @InjectFragment(R.id.channelsFragment)
  GuideFragment guideFragment;
  @InjectFragment(R.id.dashboardFragment)
  DashboardFragment dashboardFragment;
  @InjectFragment(R.id  .scheduleFragment)
  ScheduleFragment scheduleFragment;

  @Inject
  MainController mainController;

  @Getter
  private Engine engine;
  public static Handler handler = null;

  private final static String DEFAULT_CHANNEL_URI = "tv://passthrough";
  /**
   * Uri of current channel. In the beginning it is passthrough of video signal. It changes as a result of channel changing.
   * It's temporary. It should be stored somewhere else (eg. service or persistent storage)
   */
  String currentChannelUri = DEFAULT_CHANNEL_URI;


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
    if (handler == null) handler = new Handler(); 
    
    setContentView(R.layout.main);
    ImageButton netflix = (ImageButton) findViewById(R.id.program_info_synopsis);
    netflix.setOnClickListener(new View.OnClickListener(){
        public void onClick(View v){
                Toast.makeText(getApplicationContext(),"netflix start!", Toast.LENGTH_LONG).show();
                ComponentName componentName=new ComponentName("com.google.tv.netflix","com.google.tv.netflix.NetflixActivity");
                Intent intent=new Intent();
                intent.setAction("android.intent.action.VIEW");
                intent.setData(Uri.parse("nflx://p=http://api.netflix.com/catalog/titles/movies/70136344&trackId=2735185&trackUrl=/HTMLTVUI?#GoogleTvSearch?"));
                //Bundle bundle = new Bundle();
                //intent.putExtra("dat", " cmp=com.google.tv.netflix/.NetflixActivity");
//                 bundle.putSerializable("", value);
                //intent.putExtras(bundle);
                intent.setComponent(componentName);
//                 intent.addCategory("android.intent.category.LAUNCHER");
                startActivity(intent);
      

        }
        
    });
    
    
    logger.debug("onCreate: fire event initcommand: ");
    eventManager.fire(new InitCommand());
    logger.debug("onCreate: event initcommand fired");
//    engine = Engine.getInstance(this, this);
     
    LinearLayout lsongyu=(LinearLayout)findViewById(R.id.songyu);
    ArrayList<String> times=VTVMainActivity_Helper.Get_Time_Line_List();
    //first add the image of arrow
   /* ImageView left_arrow=new ImageView(this.getApplicationContext(),null,R.style.BasicViewWrapped);
    left_arrow.setImageResource(R.drawable.guide_arrow_left);
    LinearLayout.LayoutParams params = (LinearLayout.LayoutParams)left_arrow.getLayoutParams();
    params.setMargins(0, 0, 20, 0); //substitute parameters for left, top, right, bottom
    left_arrow.setLayoutParams(params);
    lsongyu.addView(left_arrow);*/
    for(int i=0;i<times.size();i++)
    {
    	ImageView img=new ImageView(this.getApplicationContext(),null,R.style.BasicViewWrapped);
    	img.setImageResource(R.drawable.guide_hour_divider);
    	lsongyu.addView(img);
    	
    	TextView temp_textview=new TextView(this.getApplicationContext());
    	temp_textview.setText(times.get(i));
    	//temp_textview.setTextAppearance(getApplicationContext(), R.style.GuideTime);
    	LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        params.setMargins(0, 0, 500, 0); //substitute parameters for left, top, right, bottom
        temp_textview.setLayoutParams(params);
        temp_textview.setTextSize(10);
    	lsongyu.addView(temp_textview);
    }
    ImageView right_arrow=new ImageView(this.getApplicationContext(),null,R.style.BasicViewWrapped);
    right_arrow.setImageResource(R.drawable.guide_arrow_right);
    LinearLayout.LayoutParams params2 = (LinearLayout.LayoutParams)right_arrow.getLayoutParams();
 //   right_arrow.setLayoutParams(params2);
    lsongyu.addView(right_arrow);
  }

//  @Override
//  protected void onDestroy() {
//    engine.uninit();
//    engine.release();
//    super.onDestroy();
//  }

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

//  @Override
//  protected void onRestart() {
//    super.onRestart();
//    logger.debug("vTV: onRestart");
//  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    MenuInflater inflater = getMenuInflater();
    inflater.inflate(R.menu.main, menu);
    return true;
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    switch (item.getItemId()) {
      case R.id.exit:
//        if (Engine.active) {
//          engine.uninit();
//          engine.release();
//        }
        finish();
        return true;
      default:
        return super.onOptionsItemSelected(item);
    }
  }

  public void updateScreenHandler() {
    Button callButton = (Button) findViewById(R.id.callButton);
//    if (engine.line == -1) {
//      callButton.setBackgroundResource(R.drawable.phone_red);
//      return;
//    }
    //Log.i(TAG, "updateScreen():status=" + engine.lines[engine.line].status);
//    callButton.setBackgroundResource(engine.lines[engine.line].incall ? R.drawable.phone_red : R.drawable.phone_green);
  }

  public void updateScreen() {
    handler.post(new Runnable() {
      public void run() {
        updateScreenHandler();
      }
    });
  }

  public void onCurrentChannelChanged(@Observes CurrentChannelChanged currentChannelChanged) {
    logger.debug("onCurrentChannelChanged");
    String newModelChannelUri = currentChannelChanged.getCurrentChannelViewModel().getCurrentlyWatchedChannelUri();
    String newChannelUri = (newModelChannelUri != null) ? newModelChannelUri : DEFAULT_CHANNEL_URI;
    logger.debug("newChannelUri: " + newChannelUri);
    if (!newChannelUri.equals(currentChannelUri))
      showVTVonLiveTv(newChannelUri);
    else
      logger.debug(String.format("Channel %s is on the screen now. No need to change", newChannelUri));
  }

  // *****************************************************************************************************
  // ************************** Button callbacks *********************************************************
  // *****************************************************************************************************
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
    logger.debug("vTV: showVTVonLiveTv: channel=" + channelUri);
    if (channelUri == null) {
      channelUri = currentChannelUri;
    }
    logger.debug("vTV: showVTVonLiveTv: switching channel to:" + channelUri);
    Intent liveTvIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(channelUri));
    liveTvIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
    startActivity(liveTvIntent);
    logger.debug("vTV: liveTvIntent activity started");

    //Schedule application auto start
    Intent thisIntent = new Intent(this, VTVMainActivity.class);
    thisIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
    PendingIntent pendingIntent = PendingIntent.getActivity(this, 678, thisIntent, PendingIntent.FLAG_ONE_SHOT);
    AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
    alarmManager.set(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + 1000, pendingIntent);
    logger.debug("vTV: Set alarm manager to do pending intent showing VTVMainActivity after 1s");
  }

  private boolean isActivitiesOrderCorrect() {
    logger.debug("vTV: isActivitiesOrderCorrect");
    ActivityManager am = (ActivityManager) this.getSystemService(ACTIVITY_SERVICE);
    List<ActivityManager.RunningTaskInfo> taskInfo = am.getRunningTasks(Integer.MAX_VALUE);
    if (taskInfo.size() < 2) {
      logger.debug("vTV: Number of tasks is less than 2. Result: false");
      return false;
    }

    return (taskInfo.get(0).topActivity.getClassName().equals("com.miquido.vtv.view.activity.VTVMainActivity") &&
        taskInfo.get(1).topActivity.getClassName().equals("com.google.tv.player.PlayerActivity"));
  }

}
