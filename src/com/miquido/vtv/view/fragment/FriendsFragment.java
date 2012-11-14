package com.miquido.vtv.view.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import com.google.inject.Inject;
import com.miquido.vtv.R;
import com.miquido.vtv.bo.Friendship;
import com.miquido.vtv.domainservices.CurrentChannelService;
import com.miquido.vtv.events.modelchanges.CurrentChannelChanged;
import com.miquido.vtv.events.modelchanges.FriendsModelChanged;
import com.miquido.vtv.events.modelchanges.PanelsStateChanged;
import com.miquido.vtv.viewmodel.PanelsStateViewModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import roboguice.event.Observes;
import roboguice.fragment.RoboFragment;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: ljazgar
 * Date: 25.07.12
 * Time: 17:13
 * To change this template use File | Settings | File Templates.
 */
public class FriendsFragment extends RoboFragment {
  private static final Logger logger = LoggerFactory.getLogger(FriendsFragment.class);

  @Inject
  FriendsSectionListAdapter friendsSectionListAdapter;
  @Inject
  CurrentChannelService currentChannelService;
  @Inject
  FriendsGridViewAdapter friendsGridViewAdapter;
  private GridView friendsGridView;

  public FriendsFragment() {
  }

  @Override
  public void onCreate(Bundle savedInstanceState) {
    logger.debug("vTV: Friends Fragment onCreate");
    logger.debug("friendsSectionListAdapter:" + friendsSectionListAdapter);
    super.onCreate(savedInstanceState);
  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    logger.debug("vTV: Friends Fragment onCreateView");
    View view = inflater.inflate(R.layout.friends_fragment_new, container, false);
    friendsGridView = (GridView) view.findViewById(android.R.id.list);
    return view;
  }

  @Override
  public void onActivityCreated(Bundle savedInstanceState) {
    super.onActivityCreated(savedInstanceState);
    logger.debug("vTV: Friends Fragment onActivityCreated");
    friendsGridView.setAdapter(friendsGridViewAdapter);
  }

  public void onPanelsStateChanged(@Observes PanelsStateChanged panelsStateChanged) {
    logger.debug("onPanelsStateChanged");
    PanelsStateViewModel panelsStateViewModel = panelsStateChanged.getPanelsStateViewModel();
    boolean isFriendsPanelVisible = panelsStateViewModel.isApplicationVisible() && panelsStateViewModel.isFriendsPanelOn();
    getView().setVisibility(isFriendsPanelVisible ? View.VISIBLE : View.INVISIBLE);
  }


  public void onFriendsModelChanged(@Observes FriendsModelChanged friendsModelChanged) {
    logger.debug("onFriendsModelChanged");
//    friendsSectionListAdapter.updateFriends(friendsModelChanged.getFriendsViewModel().getFriends());
    List<Friendship> friends = friendsModelChanged.getFriendsViewModel().getFriends();
    friendsGridViewAdapter.updateFriends(friends);
//        if (getListAdapter()==null)
//            this.setListAdapter(friendsSectionListAdapter);
  }


  public void onCurrentChannelChanged(@Observes CurrentChannelChanged currentChannelChanged) {
    logger.debug("onCurrentChannelChanged");
//    friendsSectionListAdapter.updateCurrentChannel(currentChannelChanged.getCurrentChannelViewModel().getCurrentlyWatchedChannel().getId());
  }
}
