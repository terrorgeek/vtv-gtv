package com.miquido.vtv.view.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import com.google.inject.Inject;
import com.miquido.vtv.R;
import com.miquido.vtv.bo.Actor;
import com.miquido.vtv.bo.Channel;
import com.miquido.vtv.bo.Program;
import com.miquido.vtv.domainservices.ProgramsService;
import com.miquido.vtv.events.modelchanges.CurrentChannelChanged;
import com.miquido.vtv.events.modelchanges.CurrentlyWatchedProgramChanged;
import com.miquido.vtv.events.modelchanges.PanelsStateChanged;
import com.miquido.vtv.viewmodel.PanelsStateViewModel;
import com.miquido.vtv.viewmodel.ProgramsViewModel;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import roboguice.event.Observes;
import roboguice.fragment.RoboFragment;
import roboguice.inject.InjectView;

import java.util.ArrayList;

/**
 * Created with IntelliJ IDEA.
 * User: ljazgar
 * Date: 07.09.12
 * Time: 09:59
 * To change this template use File | Settings | File Templates.
 */
public class ProgramInfoFragment extends RoboFragment {
  private static final Logger logger = LoggerFactory.getLogger(ProgramInfoFragment.class);

  @InjectView(R.id.program_title)
  TextView programTitle;

  @InjectView(R.id.program_description)
  TextView programDescription;

  @InjectView(R.id.season_title)
  TextView seasonTitle;

  @InjectView(R.id.season_subtitle)
  TextView seasonSubtitle;

  @InjectView(R.id.season_description)
  TextView seasonDescription;

  @InjectView(R.id.program_info_synopsis)
  ImageView programInfoSynopsis;

  @Inject
  ProgramsService programsService;

  @Inject
  ActorsGridViewAdapter actorsGridViewAdapter;
  private GridView actorsGridView;

  @Override
  public void onCreate(Bundle savedInstanceState) {
    logger.debug("ProgramInfoFragment onCreate");
    super.onCreate(savedInstanceState);
  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    logger.debug("ProgramInfoFragment onCreateView");
    View view = inflater.inflate(R.layout.program_info_fragment, container, false);
    actorsGridView = (GridView) view.findViewById(android.R.id.list);
    return view;
  }

  @Override
  public void onActivityCreated(Bundle savedInstanceState) {
    super.onActivityCreated(savedInstanceState);
    actorsGridView.setAdapter(actorsGridViewAdapter);
  }

  public void onCurrentChannelChanged(@Observes CurrentChannelChanged currentChannelChanged) {
    logger.debug("CurrentChannelInfoFragment onCurrentChannelChanged");
    Channel currentChannel = currentChannelChanged.getCurrentChannelViewModel().getCurrentlyWatchedChannel();
    logger.debug("currentChannel=" + currentChannel);
  }

  public void onCurrentlyWatchedProgramChanged(@Observes final CurrentlyWatchedProgramChanged currentlyWatchedProgramChanged) {
    getActivity().runOnUiThread(new Runnable() {
      @Override
      public void run() {
        updateCurrentProgramValues(currentlyWatchedProgramChanged.getProgramsViewModel());
      }
    });

  }

  private void updateCurrentProgramValues(ProgramsViewModel currentlyWatchedProgramChanged) {
    Program currentlyWatchedProgram = currentlyWatchedProgramChanged.getCurrentlyWatchedProgram();
    if (currentlyWatchedProgram != null && StringUtils.isNotBlank(currentlyWatchedProgram.getName())) {
      programTitle.setText(currentlyWatchedProgram.getName());
      String descr = StringUtils.isNotBlank(currentlyWatchedProgram.getDescription()) ? currentlyWatchedProgram.getDescription() : "";
      if(descr.startsWith("Lorem ipsum") || descr == null || (descr != null && descr.equals("null"))){
      	descr = "The team investigates after a homeless teenager is found burned to death outside a restaurant. The victim's father tells them there could be a link with some violent videos uploaded to the internet, so the team sets up a sting operation which eventually leads them to the same school the dead man had attended - and two groups of girls who are very single-minded";
      }
      programDescription.setText(descr);
      actorsGridViewAdapter.updateActors(currentlyWatchedProgram.getActors());
      seasonTitle.setText("Season " + (currentlyWatchedProgram.getSeasonNo() == null ? "" :currentlyWatchedProgram.getSeasonNo()) + ", " +
          "Episode " + (currentlyWatchedProgram.getEpisodeNo() == null ? "" :currentlyWatchedProgram.getEpisodeNo()));
      seasonDescription.setText(descr);
      seasonSubtitle.setText(currentlyWatchedProgram.getEpisodeName());
      String episodeNo = currentlyWatchedProgram.getEpisodeNo();
//      if (episodeNo == null) {
//        programInfoSynopsis.setImageResource(R.drawable.gtv_season2episode8);
//      } else if (episodeNo.contains("8")) {
//        programInfoSynopsis.setImageResource(R.drawable.gtv_season2episode8);
//      } else if (episodeNo.contains("10")) {
//        programInfoSynopsis.setImageResource(R.drawable.gtv_season2episode10);
//      } else if (episodeNo.contains("5")) {
//        programInfoSynopsis.setImageResource(R.drawable.gtv_season3episode5);
//      } else if (episodeNo.contains("6")) {
//        programInfoSynopsis.setImageResource(R.drawable.gtv_season2episode6);
//      } else {
//        programInfoSynopsis.setImageResource(R.drawable.gtv_season2episode8);
//      }
    } else {
      programTitle.setText(getText(R.string.unknown_program));
      programDescription.setText(getText(R.string.details_not_available));
      actorsGridViewAdapter.updateActors(new ArrayList<Actor>());
      seasonTitle.setText(getText(R.string.details_not_available));
      seasonDescription.setText(getText(R.string.details_not_available));
      seasonSubtitle.setText(getText(R.string.details_not_available));
    }
  }

  @Override
  public void onResume() {
    super.onResume();
    updateCurrentProgramValues(programsService);
  }

  public void onPanelsStateChanged(@Observes PanelsStateChanged panelsStateChanged) {
    logger.debug("onPanelsStateChanged");
    PanelsStateViewModel panelsStateViewModel = panelsStateChanged.getPanelsStateViewModel();
    boolean isProgramInfoPanelVisible = panelsStateViewModel.isApplicationVisible() && panelsStateViewModel.isProgramInfoPanelOn();
    getView().setVisibility(isProgramInfoPanelVisible ? View.VISIBLE : View.INVISIBLE);
  }
}
