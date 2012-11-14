package com.miquido.vtv.view.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import com.google.inject.Inject;
import com.miquido.vtv.R;
import com.miquido.vtv.bo.Program;
import com.miquido.vtv.bo.ScheduleEntry;
import com.miquido.vtv.controllers.tasks.LoadCurrentProgramTask;
import com.miquido.vtv.controllers.tasks.TaskProvider;
import com.miquido.vtv.domainservices.ProgramsService;
import com.miquido.vtv.events.modelchanges.CurrentChannelChanged;
import com.miquido.vtv.events.modelchanges.CurrentlyWatchedProgramChanged;
import com.miquido.vtv.events.modelchanges.PanelsStateChanged;
import com.miquido.vtv.events.modelchanges.ScheduleModelChanged;
import com.miquido.vtv.utils.ResourceHelper;
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

  @InjectView(R.id.currently_watching_thumbnail_popup)
  ImageView currentlyWatchingThumbnailPopup;

  @InjectView(R.id.youAreWatchingPopup)
  RelativeLayout youAreWatchingPopup;

  @InjectView(R.id.upcomingShowPopup)
  RelativeLayout upcomingShowPopup;

  @InjectView(R.id.friendsButton1)
  ImageButton friendsButton1;

  @InjectView(R.id.friendsButton2)
  ImageButton friendsButton2;

  @InjectView(R.id.friendsButton3)
  ImageButton friendsButton3;

  @InjectView(R.id.recommendedButton1)
  ImageButton recommendedButton1;

  @InjectView(R.id.recommendedButton2)
  ImageButton recommendedButton2;

  @InjectView(R.id.recommendedButton3)
  ImageButton recommendedButton3;

  @InjectView(R.id.currently_watching_title_popup)
  TextView currentlyWatchingTitlePopup;

  @InjectView(R.id.currently_watching_description_popup)
  TextView currentlyWatchingDescriptionPopup;

  @InjectView(R.id.upcoming_show_thumbnail_popup)
  ImageView upcomingShowThumbnailPopup;

  @InjectView(R.id.upcoming_show_title_popup)
  TextView upcomingShowTitlePopup;

  @InjectView(R.id.upcoming_show_description_popup)
  TextView upcomingShowDescriptionPopup;

  @InjectView(R.id.upcoming_show_time_popup)
  TextView upcomingShowTimePopup;

  @InjectView(R.id.friendsWatchingPopup)
  RelativeLayout friendsWatchingPopup;

  @InjectView(R.id.itemsList)
  ListView upcomingShowsListView;

  @Inject
  ProgramsService programsService;

  @Inject
  TaskProvider<LoadCurrentProgramTask> loadCurrentProgramTaskProvider;

  private UpcomingShowsAdapter upcomingShowsAdapter;

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

  @Override
  public void onActivityCreated(Bundle savedInstanceState) {
    super.onActivityCreated(savedInstanceState);
    if (upcomingShowsAdapter == null) {
      upcomingShowsAdapter = new UpcomingShowsAdapter(getActivity());
    }
    upcomingShowsListView.setAdapter(upcomingShowsAdapter);
    upcomingShowsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
      @Override
      public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        upcomingShowPopup.setVisibility(View.VISIBLE);
        ScheduleEntry scheduleEntry = upcomingShowsAdapter.getItem(i);
        upcomingShowTitlePopup.setText(scheduleEntry.getGuideEntry().getProgram().getName());
        upcomingShowDescriptionPopup.setText(scheduleEntry.getGuideEntry().getProgram().getDescription());
        upcomingShowTimePopup.setText(upcomingShowsAdapter.formatDate(scheduleEntry.getGuideEntry().getStartTime()));
        int resourceIndex = upcomingShowsAdapter.getCount() - i;
        upcomingShowThumbnailPopup.setImageDrawable(ResourceHelper.getResourceDrawable("dashboard_upcoming_sample_" + resourceIndex, getActivity()));
      }
    });
    currentlyWatchingThumbnail.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        youAreWatchingPopup.setVisibility(View.VISIBLE);
      }
    });
    youAreWatchingPopup.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        youAreWatchingPopup.setVisibility(View.GONE);
      }
    });
    friendsButton1.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        friendsWatchingPopup.setVisibility(View.VISIBLE);
      }
    });
    friendsButton2.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        friendsWatchingPopup.setVisibility(View.VISIBLE);
      }
    });
    friendsButton3.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        friendsWatchingPopup.setVisibility(View.VISIBLE);
      }
    });
    friendsWatchingPopup.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        friendsWatchingPopup.setVisibility(View.GONE);
      }
    });
    upcomingShowPopup.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        upcomingShowPopup.setVisibility(View.GONE);
      }
    });
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

  public void onCurrentlyWatchedProgram(@Observes final CurrentlyWatchedProgramChanged currentlyWatchedProgramChanged) {
    getActivity().runOnUiThread(new Runnable() {
      @Override
      public void run() {
        Program currentlyWatchedProgram = currentlyWatchedProgramChanged.getProgramsViewModel().getCurrentlyWatchedProgram();
        if (currentlyWatchedProgram != null) {
          currentlyWatching.setText(currentlyWatchedProgram.getName());
          currentlyWatchingTitlePopup.setText(currentlyWatchedProgram.getName());
          currentlyWatchingDescriptionPopup.setText(currentlyWatchedProgram.getDescription());
          if (currentlyWatchedProgram.getName().contains("Mentalist")) {
            currentlyWatchingThumbnail.setImageResource(R.drawable.mentalist);
            currentlyWatchingThumbnailPopup.setImageResource(R.drawable.mentalist);
          } else {
            currentlyWatchingThumbnail.setImageResource(R.drawable.ncis);
            currentlyWatchingThumbnailPopup.setImageResource(R.drawable.ncis);
          }
        } else {
          currentlyWatching.setText(getText(R.string.unknown_program));
        }
      }
    });
  }

  public void onScheduleModelChanged(@Observes ScheduleModelChanged scheduleModelChanged) {
    logger.debug("onScheduleModelChanged");
    if (upcomingShowsAdapter != null) {
      upcomingShowsAdapter.updateSchedule(scheduleModelChanged.getScheduleViewModel().getSchedule());
    }
  }
}
