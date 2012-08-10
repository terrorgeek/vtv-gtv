package com.miquido.vtv.view.activity;

import android.app.AlertDialog;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.ToggleButton;
import com.miquido.test.robolectric.RobolectricInjectionTestRunner;
import com.miquido.vtv.R;
import com.miquido.vtv.view.fragment.FriendsFragment;
import com.xtremelabs.robolectric.Robolectric;
import com.xtremelabs.robolectric.shadows.ShadowHandler;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static junit.framework.Assert.*;

/**
 * Created with IntelliJ IDEA.
 * User: ljazgar
 * Date: 08.08.12
 * Time: 16:40
 * To change this template use File | Settings | File Templates.
 */
@RunWith(RobolectricInjectionTestRunner.class)
public class VTVMainActivityTest {

    private static final Logger logger = LoggerFactory.getLogger(VTVMainActivityTest.class);

    private VTVMainActivity vtvMainActivity;
    private FriendsFragment friendsFragment;
    private ToggleButton friendsButton;
    private Button loginButton;
    private Drawable loginButtonLoggingDrawable;
    private Drawable loginButtonLoggedInDrawable;

    @Before
    public void setUp() {
        logger.debug("setUp");

        Robolectric.addPendingHttpResponse(200, "{\"id\":\"89b99e2be7e1e835ccff199e1146411a\",\"module_name\":\"Users\",\"name_value_list\":{\"user_id\":{\"name\":\"user_id\",\"value\":\"48ca2e78-da4c-11e1-83cd-c263aa2dded5\"},\"user_name\":{\"name\":\"user_name\",\"value\":\"bob.smith@cods.pyctex.net\"}}}");
        Robolectric.addPendingHttpResponse(200, "{\"id\":\"12bb9aa9-fbdd-1e0a-c53a-4fff6358f941\",\"name\":\"Bob Smith\",\"date_entered\":\"2012-07-12 23:55:38\",\"date_modified\":\"2012-07-26 16:52:50\",\"description\":\"\",\"first_name\":\"Bob\",\"last_name\":\"Smith\",\"full_name\":\"Bob Smith\",\"email\":\"\",\"email1\":\"\",\"email2\":\"\",\"avatar_id\":\"8083a680-ebf9-2244-b346-4ff74b9c2ae8\",\"facebook_id\":\"\",\"sip_status\":\"offline\",\"avatar_url\":\"http:\\/\\/api.cods.pyctex.net\\/images\\/8083a680-ebf9-2244-b346-4ff74b9c2ae8\"}");
        Robolectric.addPendingHttpResponse(200, "{\"result_count\":20,\"total_count\":\"55\",\"next_offset\":20,\"entry_list\":[{\"id\":\"103782b6-aa79-f369-412a-4fff634bdd8b\",\"name\":\"Oscar Mcknight\",\"date_entered\":\"2012-07-12 23:55:38\",\"date_modified\":\"2012-07-12 23:55:38\",\"description\":\"\",\"first_name\":\"Oscar\",\"last_name\":\"Mcknight\",\"full_name\":\"Oscar Mcknight\",\"email\":\"\",\"email1\":\"\",\"email2\":\"\",\"avatar_id\":\"f171e97a-05d9-d464-80b8-4ff741476e0a\",\"facebook_id\":\"\",\"sip_status\":\"offline\",\"avatar_url\":\"http:\\/\\/api.cods.pyctex.net\\/images\\/f171e97a-05d9-d464-80b8-4ff741476e0a\",\"date_added_to_list\":\"2012-07-12 23:55:38\",\"SIP_status\":\"online\"},{\"id\":\"10c54e7d-e76e-69d9-824d-5003f8a0803a\",\"name\":\"Gilbert Coffey\",\"date_entered\":\"2012-07-16 11:19:35\",\"date_modified\":\"2012-07-16 11:19:35\",\"description\":\"\",\"first_name\":\"Gilbert\",\"last_name\":\"Coffey\",\"full_name\":\"Gilbert Coffey\",\"email\":\"\",\"email1\":\"\",\"email2\":\"\",\"avatar_id\":\"817d0ca5-f623-3c5b-9873-4ff74bef025a\",\"facebook_id\":\"\",\"sip_status\":\"offline\",\"avatar_url\":\"http:\\/\\/api.cods.pyctex.net\\/images\\/817d0ca5-f623-3c5b-9873-4ff74bef025a\",\"date_added_to_list\":\"2012-07-16 11:19:35\",\"SIP_status\":\"offline\"}]}");

        Robolectric.getBackgroundScheduler().pause();
        vtvMainActivity = new VTVMainActivity();
        vtvMainActivity.onCreate(null);
        friendsFragment =(FriendsFragment) vtvMainActivity.getSupportFragmentManager().findFragmentById(R.id.friendsFragment);
        friendsButton = (ToggleButton) vtvMainActivity.findViewById(R.id.friendsButton);
        loginButton = (Button) vtvMainActivity.findViewById(R.id.loginButton);
        loginButtonLoggingDrawable = vtvMainActivity.getResources().getDrawable(R.drawable.login_button_logging);
        loginButtonLoggedInDrawable = vtvMainActivity.getResources().getDrawable(R.drawable.login_button_loggedin);

//        Robolectric.addHttpResponseRule("http://api.cods.pyctex.net/rest/v2/login/bob.smith@cods.pyctex.net/103891baca2751a856b094db796e3fee",
//                "{\"id\":\"89b99e2be7e1e835ccff199e1146411a\",\"module_name\":\"Users\",\"name_value_list\":{\"user_id\":{\"name\":\"user_id\",\"value\":\"48ca2e78-da4c-11e1-83cd-c263aa2dded5\"},\"user_name\":{\"name\":\"user_name\",\"value\":\"bob.smith@cods.pyctex.net\"}})");
        logger.debug("setUp - end");
    }

    @Test
    public void testInit() throws InterruptedException {

        assertEquals(View.INVISIBLE, friendsFragment.getView().getVisibility());
        assertEquals(View.INVISIBLE, friendsButton.getVisibility());
        assertEquals(loginButtonLoggingDrawable, loginButton.getBackground());

        logger.debug("Run background task (LoginTask)");
        Robolectric.getBackgroundScheduler().runOneTask();
        logger.debug("BackgroundTask (LoginTask) ended");

        assertEquals(View.INVISIBLE, friendsFragment.getView().getVisibility());
        assertEquals(View.VISIBLE, friendsButton.getVisibility());
        assertEquals(loginButtonLoggedInDrawable, loginButton.getBackground());

//        logger.debug("Press friendsButton");
//        friendsButton.performClick();
//        logger.debug("click performed");
//
//        assertEquals(View.VISIBLE, friendsFragment.getView().getVisibility());
//        assertEquals(View.VISIBLE, friendsButton.getVisibility());
//        assertSame(loginButtonLoggedInDrawable, loginButton.getBackground());
//
//        visibility = friendsFragment.getView().getVisibility();
//        logger.debug("are friends visible:" + (visibility == View.VISIBLE));
//
    }


}
