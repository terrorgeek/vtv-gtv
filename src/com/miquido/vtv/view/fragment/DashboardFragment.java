package com.miquido.vtv.view.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.google.inject.Inject;
import com.miquido.DemoChannels;
import com.miquido.vtv.R;
import com.miquido.vtv.bo.Program;
import com.miquido.vtv.controllers.tasks.LoadCurrentProgramTask;
import com.miquido.vtv.controllers.tasks.TaskProvider;
import com.miquido.vtv.domainservices.ProgramsService;
import com.miquido.vtv.events.modelchanges.CurrentChannelChanged;
import com.miquido.vtv.events.modelchanges.CurrentlyWatchedProgramChanged;
import com.miquido.vtv.events.modelchanges.PanelsStateChanged;
import com.miquido.vtv.viewmodel.PanelsStateViewModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import roboguice.event.Observes;
import roboguice.fragment.RoboFragment;
import roboguice.inject.InjectView;

public class DashboardFragment extends RoboFragment {
  private static final Logger logger = LoggerFactory.getLogger(DashboardFragment.class);

  @InjectView(R.id.currently_watching_title)
  TextView currentlyWatching;

  @InjectView(R.id.currently_watching_thumbnail)
  ImageView currentlyWatchingThumbnail;

  @Inject
  ProgramsService programsService;

  @Inject
  TaskProvider<LoadCurrentProgramTask> loadCurrentProgramTaskProvider;

  @Override
  public void onCreate(Bundle savedInstanceState) {
    logger.debug("DashboardFragment onCreate");
    super.onCreate(savedInstanceState);
  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    logger.debug("DashboardFragment onCreateView");
    return inflater.inflate(R.layout.dashboard_fragment, container, false);
  }

  public void onPanelsStateChanged(@Observes PanelsStateChanged panelsStateChanged) {
    logger.debug("onPanelsStateChanged");
    PanelsStateViewModel panelsStateViewModel = panelsStateChanged.getPanelsStateViewModel();
    boolean isDashboardPanelVisible = panelsStateViewModel.isApplicationVisible() && panelsStateViewModel.isDashboardPanelOn();
    getView().setVisibility(isDashboardPanelVisible ? View.VISIBLE : View.INVISIBLE);
  }

  public void onCurrentlyWatching(@Observes CurrentChannelChanged currentChannelChanged) {
    logger.info("Currently watching channel changed");
    loadCurrentProgramTaskProvider.get().execute();
  }

  public void onCurrentlyWatchedProgram(@Observes CurrentlyWatchedProgramChanged currentlyWatchedProgramChanged) {
    Program currentlyWatchedProgram = currentlyWatchedProgramChanged.getProgramsViewModel().getCurrentlyWatchedProgram();
    if (currentlyWatchedProgram != null) {
      currentlyWatching.setText(currentlyWatchedProgram.getName());
      if(currentlyWatchedProgram.getName().contains("Mentalist")) {
        currentlyWatchingThumbnail.setImageResource(R.drawable.mentalist);
      } else  {
        currentlyWatchingThumbnail.setImageResource(R.drawable.ncis);
      }
    } else {
      currentlyWatching.setText(getText(R.string.unknown_program));
    }
  }

}
