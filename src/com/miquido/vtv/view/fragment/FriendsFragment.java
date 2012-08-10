package com.miquido.vtv.view.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import com.google.inject.Inject;
import com.miquido.vtv.R;
import com.miquido.vtv.domainservices.ProfilesService;
import com.miquido.vtv.events.modelchanges.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import roboguice.event.Observes;
import roboguice.fragment.RoboListFragment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: ljazgar
 * Date: 25.07.12
 * Time: 17:13
 * To change this template use File | Settings | File Templates.
 */
public class FriendsFragment extends RoboListFragment {
    private static final Logger logger = LoggerFactory.getLogger(FriendsFragment.class);

    @Inject
    private ProfilesService profilesService;

    private List<Map<String, String>> listData = new ArrayList<Map<String, String>>();

    public FriendsFragment() {
        String [] data = new String[] {
                "Sarah Roche", "Anikka Smith", "Ankur Pansari", "Aubrey Pansari", "Cameron Marlow", "Leah Pearlman", "Meredith Chin",
                "Sarah Roche", "Anikka Smith", "Ankur Pansari", "Aubrey Pansari", "Cameron Marlow", "Leah Pearlman", "Meredith Chin",
                "Sarah Roche", "Anikka Smith", "Ankur Pansari", "Aubrey Pansari", "Cameron Marlow", "Leah Pearlman", "Meredith Chin",
                "Sarah Roche", "Anikka Smith", "Ankur Pansari", "Aubrey Pansari", "Cameron Marlow", "Leah Pearlman", "Meredith Chin",
                "Sarah Roche", "Anikka Smith", "Ankur Pansari", "Aubrey Pansari", "Cameron Marlow", "Leah Pearlman", "Meredith Chin"
        };

        for(String line : data) {
            Map<String, String> map = new HashMap<String, String>();
            map.put("text", line);
            listData.add(map);
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        logger.debug("vTV: Friends Fragment onCreate");
        super.onCreate(savedInstanceState);

        this.setListAdapter(new SimpleAdapter(getActivity(), listData,
                R.layout.friends_list, new String[] {"text"}, new int[] { R.id.friendNameListField}));

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        logger.debug("vTV: Friends Fragment onCreateView");
        return inflater.inflate(R.layout.friend_fragment, container, false);
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);
        logger.debug("vTV: FriendsFragment onListItemClick");
    }

    public void onPanelsStateChanged(@Observes PanelsStateChanged panelsStateChanged) {
        logger.debug("onPanelsStateChanged");
        boolean isFriendsPanelOn = panelsStateChanged.getPanelsStateViewModel().isFriendsPanelOn();
        getView().setVisibility( isFriendsPanelOn ? View.VISIBLE : View.INVISIBLE);
    }

    public void onFriendsModelInitialized(@Observes FriendsModelInitialized friendsModelInitialized) {
        logger.debug("onFriendsModelInitialized");
        // TODO
    }

    public void onFriendsModelChanged(@Observes FriendsModelChanged friendsModelChanged) {
        logger.debug("onFriendsModelChanged");
        // TODO
    }

    public void onFriendsModelCleared(@Observes FriendsModelCleared friendsModelCleared) {
        logger.debug("onFriendsModelCleared");
        // TODO
    }

    public void onFriendsLoadingStarted(@Observes FriendsLoadingStarted friendsLoadingStarted) {
        logger.debug("onFriendsLoadingStarted");
        // TODO
    }

    public void onFriendsLoadingFinished(@Observes FriendsLoadingFinished friendsLoadingFinished) {
        logger.debug("onFriendsLoadingFinished");
        // TODO
    }

}
