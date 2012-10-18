/**
 * Created by raho on 10/10/12.
 *
 * Copyright 2012 MiQUiDO <http://www.miquido.com/>. All rights reserved.
 */
package com.miquido.vtv.view.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.miquido.vtv.R;
import com.miquido.vtv.events.modelchanges.PanelsStateChanged;
import com.miquido.vtv.viewmodel.PanelsStateViewModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import roboguice.event.Observes;
import roboguice.fragment.RoboFragment;

public class ScheduleFragment extends RoboFragment {

  private static final Logger logger = LoggerFactory.getLogger(ScheduleFragment.class);

  @Override
  public void onCreate(Bundle savedInstanceState) {
    logger.debug("DashboardFragment onCreate");
    super.onCreate(savedInstanceState);
  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    logger.debug("DashboardFragment onCreateView");
    return inflater.inflate(R.layout.schedule_fragment, container, false);
  }

  public void onPanelsStateChanged(@Observes PanelsStateChanged panelsStateChanged) {
    logger.debug("onPanelsStateChanged");
    PanelsStateViewModel panelsStateViewModel = panelsStateChanged.getPanelsStateViewModel();
    boolean isChannelPanelVisible = panelsStateViewModel.isApplicationVisible() && panelsStateViewModel.isSchedulePanelOn();
    getView().setVisibility(isChannelPanelVisible ? View.VISIBLE : View.INVISIBLE);
  }
}
