package com.miquido.vtv.view.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ToggleButton;
import com.google.inject.Inject;
import com.miquido.vtv.R;
import com.miquido.vtv.events.commands.FriendsButtonToggledCommand;
import com.miquido.vtv.events.commands.LoginButtonClickedCommand;
import com.miquido.vtv.events.modelchanges.PanelsStateChanged;
import com.miquido.vtv.events.modelchanges.SessionModelChanged;
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

    @InjectView(R.id.loginButton)
    Button loginButton;
    @InjectView(R.id.friendsButton)
    ToggleButton friendsButton;
    @InjectView(R.id.notificationsButton)
    ToggleButton notificationButton;
    @InjectView(R.id.chatButton)
    ToggleButton chatButton;

    @Inject
    EventManager eventManager;

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

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                eventManager.fire(new LoginButtonClickedCommand());
            }
        });

    }

    public void onPanelsStateChanged(@Observes PanelsStateChanged panelsStateChanged) {
        logger.debug("onPanelsStateChanged");
        boolean newButtonState = (panelsStateChanged.getPanelsStateViewModel().isFriendsPanelOn());
        friendsButton.setChecked(newButtonState);
    }

    public void onSessionModelChanged(@Observes SessionModelChanged sessionModelChanged) {
        logger.debug("onSessionModelChanged");
        boolean isLoggedIn = sessionModelChanged.getSessionViewModel().isLoggedIn();
        boolean isLoggingIn = sessionModelChanged.getSessionViewModel().isLoggingIn();

        // All buttons except login are visible when user is logged in
        int panelButtonsVisibility = (isLoggedIn)?View.VISIBLE:View.INVISIBLE;
        friendsButton.setVisibility( panelButtonsVisibility );
        notificationButton.setVisibility( panelButtonsVisibility );
        chatButton.setVisibility( panelButtonsVisibility );

        // Login button is visible always, but his image depends on state of session.
        loginButton.setVisibility(View.VISIBLE);
        int loginImageResourceId = (isLoggedIn) ?  R.drawable.login_button_loggedin :
                                        ((isLoggingIn) ?    R.drawable.login_button_logging :
                                                            R.drawable.login_button_loggedout );
        loginButton.setBackgroundResource(loginImageResourceId);

    }


}