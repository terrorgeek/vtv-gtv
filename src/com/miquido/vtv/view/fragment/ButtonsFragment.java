package com.miquido.vtv.view.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import com.google.inject.Inject;
import com.miquido.vtv.R;
import com.miquido.vtv.domainservices.PanelsStateService;
import com.miquido.vtv.events.commands.*;
import com.miquido.vtv.events.modelchanges.PanelsStateChanged;
import com.miquido.vtv.events.modelchanges.SessionModelChanged;
import com.miquido.vtv.view.activity.VTVMainActivity;
import com.miquido.vtv.viewmodel.SessionViewModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import roboguice.event.EventManager;
import roboguice.event.Observes;
import roboguice.fragment.RoboFragment;
import roboguice.inject.InjectView;

/**
 * Fragment of UI with buttons located at left top corner of screen.
 */
public class ButtonsFragment extends RoboFragment {

  private static final Logger logger = LoggerFactory.getLogger(ButtonsFragment.class);

  @InjectView(R.id.tabBar)
  LinearLayout tabBar;

  @InjectView(R.id.loginBadge)
  TextView loginBadge;
  @InjectView(R.id.loginButton)
  Button loginButton;
  @InjectView(R.id.friendsButton)
  RadioButton friendsButton;
  @InjectView(R.id.friendsBadge)
  TextView friendsBadge;
  @InjectView(R.id.notificationsBadge)
  TextView notificationsBadge;
  @InjectView(R.id.notificationsButton)
  RadioButton notificationButton;
  @InjectView(R.id.guideButton)
  RadioButton guideButton;
  @InjectView(R.id.scheduleButton)
  RadioButton scheduleButton;
  @InjectView(R.id.programInfoButton)
  RadioButton programInfoButton;
  @InjectView(R.id.dashboardButton)
  RadioButton dashboardButton;

  @InjectView(R.id.callButton)
  Button callButton;

  @Inject
  EventManager eventManager;

  @Inject
  PanelsStateService panelsStateService;

  @Override
  public void onCreate(Bundle savedInstanceState) {
    logger.debug("vTv: ButtonsFragment onCreate");
    super.onCreate(savedInstanceState);
  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    logger.debug("vTv: ButtonsFragment onCreateView");
    return inflater.inflate(R.layout.buttons_fragment, container, false);
  }

  @Override
  public void onActivityCreated(Bundle savedInstanceState) {
    super.onActivityCreated(savedInstanceState);

    friendsButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
      public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        eventManager.fire(new FriendsButtonToggledCommand(isChecked));
      }
    });
    friendsBadge.setText("2");

    loginButton.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        eventManager.fire(new LoginButtonClickedCommand());
      }
    });
    loginBadge.setText("5");

    guideButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
      public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        eventManager.fire(new ChannelsButtonToggledCommand(isChecked));
      }
    });
    scheduleButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
      @Override
      public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
        eventManager.fire(new ScheduleProgramButtonToggledCommand(isChecked));
      }
    });
    callButton.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        ((VTVMainActivity) getActivity()).getEngine().do_call();
      }
    });
    programInfoButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
      @Override
      public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
        eventManager.fire(new ProgramInfoButtonToggledCommand(isChecked));
      }
    });
    notificationButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
      @Override
      public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
        eventManager.fire(new NotificationsButtonToggledCommand(isChecked));
      }
    });
    notificationsBadge.setText("3");

    dashboardButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
      @Override
      public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
        eventManager.fire(new DashboardButtonToggledCommand(isChecked));
      }
    });

  }

  public void onPanelsStateChanged(@Observes PanelsStateChanged panelsStateChanged) {
    logger.debug("onPanelsStateChanged");

    boolean isApplicationVisible = panelsStateChanged.getPanelsStateViewModel().isApplicationVisible();
    tabBar.setVisibility((isApplicationVisible) ? View.VISIBLE : View.INVISIBLE);

    boolean newFriendsButtonState = (panelsStateChanged.getPanelsStateViewModel().isFriendsPanelOn());
    friendsButton.setChecked(newFriendsButtonState);

    boolean newChannelsButtonState = (panelsStateChanged.getPanelsStateViewModel().isChannelsPanelOn());
    guideButton.setChecked(newChannelsButtonState);

    boolean newNotificationsButtonState = (panelsStateChanged.getPanelsStateViewModel().isNotificationsPanelOn());
    notificationButton.setChecked(newNotificationsButtonState);

    boolean newProgramInfoButtonState = panelsStateChanged.getPanelsStateViewModel().isProgramInfoPanelOn();
    programInfoButton.setChecked(newProgramInfoButtonState);
  }

  public void onSessionModelChanged(@Observes SessionModelChanged sessionModelChanged) {
    logger.debug("onSessionModelChanged");
    SessionViewModel session = sessionModelChanged.getSessionViewModel();
    boolean isInitializing = session.isLoggingIn() || session.isLoggedIn() || session.isSessionInitializing();
    boolean isSessionInitialized = sessionModelChanged.getSessionViewModel().isSessionInitialized();

    //Login button is visible always, but his image depends on state of session.
    loginButton.setVisibility(View.VISIBLE);
    loginButton.bringToFront();
    int loginImageResourceId = (isSessionInitialized) ? R.drawable.tab_bar_icon_logo_active :
        ((isInitializing) ? R.drawable.tab_bar_icon_logo_in_progress :
            R.drawable.tab_bar_icon_logo);
    loginButton.setBackgroundResource(loginImageResourceId);
  }


}