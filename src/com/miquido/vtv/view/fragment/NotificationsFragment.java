package com.miquido.vtv.view.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.google.inject.Inject;
import com.miquido.vtv.events.modelchanges.NotificationsModelChanged;
import com.miquido.vtv.events.modelchanges.PanelsStateChanged;
import com.miquido.vtv.viewmodel.PanelsStateViewModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import roboguice.event.EventManager;
import roboguice.event.Observes;
import roboguice.fragment.RoboListFragment;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: ljazgar
 * Date: 25.07.12
 * Time: 13:47
 * To change this template use File | Settings | File Templates.
 */
public class NotificationsFragment extends RoboListFragment {
  private static final Logger logger = LoggerFactory.getLogger(NotificationsFragment.class);

  @Inject
  NotificationsAdapter notificationsAdapter;
  @Inject
  EventManager eventManager;
  private List<Map<String, String>> listData = new ArrayList<Map<String, String>>();

  public NotificationsFragment() {
  }

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    return super.onCreateView(inflater, container, savedInstanceState);
  }

  public void onPanelsStateChanged(@Observes PanelsStateChanged panelsStateChanged) {
    logger.debug("onPanelsStateChanged");
    PanelsStateViewModel panelsStateViewModel = panelsStateChanged.getPanelsStateViewModel();
    boolean isNotificationsPanelVisible = panelsStateViewModel.isApplicationVisible() && panelsStateViewModel.isNotificationsPanelOn();
    getView().setVisibility(isNotificationsPanelVisible ? View.VISIBLE : View.INVISIBLE);
  }

  public void onNotificationsModelChanged(@Observes NotificationsModelChanged notificationsModelChanged) {
    logger.debug("onNotificationsModelChanged");
    logger.info("Notifications list size: {}", notificationsModelChanged.getNotificationsViewModel().getNotifications() == null ?
        0 : notificationsModelChanged.getNotificationsViewModel().getNotifications().size());
    notificationsAdapter.updateNotifications(notificationsModelChanged.getNotificationsViewModel().getNotifications());
    if (getListAdapter() == null)
      this.setListAdapter(notificationsAdapter);
  }
}
