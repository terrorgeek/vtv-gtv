package com.miquido.vtv.view.fragment;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import com.google.inject.Inject;
import com.miquido.DemoChannels;
import com.miquido.MockData;
import com.miquido.vtv.R;
import com.miquido.vtv.bo.Channel;
import com.miquido.vtv.bo.Program;
import com.miquido.vtv.components.GuideItem;
import com.miquido.vtv.components.ObservableScrollView;
import com.miquido.vtv.domainservices.CurrentChannelService;
import com.miquido.vtv.events.commands.ClickedChannelCommand;
import com.miquido.vtv.events.modelchanges.ChannelsModelChanged;
import com.miquido.vtv.events.modelchanges.PanelsStateChanged;
import com.miquido.vtv.utils.ResourceHelper;
import com.miquido.vtv.viewmodel.PanelsStateViewModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import roboguice.event.EventManager;
import roboguice.event.Observes;
import roboguice.fragment.RoboListFragment;
import roboguice.inject.InjectView;

import java.util.Calendar;
import java.util.List;

public class GuideFragment extends RoboListFragment implements ObservableScrollView.ScrollViewListener {
  private static final Logger logger = LoggerFactory.getLogger(GuideFragment.class);

  @Inject
  ChannelsListAdapter channelsListAdapter;
  @Inject
  EventManager eventManager;

  @InjectView(R.id.currently_watching_thumbnail)
  ImageView currentlyWatchingThumbnail;

  @InjectView(R.id.currently_watching_description)
  TextView currentlyWatchingDescription;

  @InjectView(R.id.current_program_title)
  TextView programTitle;

  @InjectView(R.id.current_program_title)
  TextView programHeader;

  @InjectView(R.id.you_are_watching)
  TextView programTime;

  @InjectView(R.id.programsContainer)
  LinearLayout programsContainer;

  @InjectView(R.id.programsList)
  ObservableScrollView programsList;

  @InjectView(R.id.channelsList)
  ScrollView channelsList;

  @Inject
  CurrentChannelService currentChannelService;

  List<Channel> channels = null;

  GuideItem programToSelect;

  public GuideFragment() {
  }

  @Override
  public void onCreate(Bundle savedInstanceState) {
    logger.debug("vTV: Channels Fragment onCreate");
    super.onCreate(savedInstanceState);
  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    logger.debug("vTV: Channels Fragment onCreateView");
    this.setListAdapter(channelsListAdapter);
    return inflater.inflate(R.layout.channels_fragment, container, false);
  }

  @Override
  public void onViewCreated(View view, Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);
    buildGuide();
    programsList.setScrollViewListener(this);
    channelsList.setVerticalScrollBarEnabled(false);
  }

  @Override
  public void onStart() {
    super.onStart();
    logger.debug("onStart");
  }

  @Override
  public void onResume() {
    super.onResume();
    logger.debug("onResume");
//    setAppropriateChannel();
  }

  private void setAppropriateChannel() {
    final Channel currentlyWatchedChannel = currentChannelService.getCurrentlyWatchedChannel();
    if (currentlyWatchedChannel == null) {
      return;
    }
    logger.info("Channels fragment. Current channel is: {}", currentlyWatchedChannel.getName());
    Program currentProgram = MockData.getProgramForChannel(currentlyWatchedChannel.getId());
    if (DemoChannels.CHANNEL_TNT.equals(currentlyWatchedChannel.getId())) {
//      channelsImageView.setImageResource(R.drawable.guide_mentalist);
      currentlyWatchingThumbnail.setImageResource(R.drawable.mentalist);
    } else {
//      channelsImageView.setImageResource(R.drawable.guide_ncis);
      currentlyWatchingThumbnail.setImageResource(R.drawable.ncis);
    }
    currentlyWatchingDescription.setText(currentProgram.getDescription());
    programTitle.setText(currentProgram.getName());
    programHeader.setText(currentProgram.getName());
//    channelsImageView.setOnClickListener(new View.OnClickListener() {
//      @Override
//      public void onClick(View view) {
//        logger.info("Current channel is: {}", currentlyWatchedChannel.getName());
//        if (DemoChannels.CHANNEL_TNT.equals(currentlyWatchedChannel.getId())) {
//          logger.info("Changing channel to USA");
//          channelsImageView.setImageResource(R.drawable.guide_ncis);
//          Program currentProgram = MockData.getProgramForChannel(DemoChannels.CHANNEL_USA);
//          programTitle.setText(currentProgram.getName());
//          currentlyWatchingDescription.setText(currentProgram.getDescription());
//          currentlyWatchingThumbnail.setImageResource(R.drawable.ncis);
//          eventManager.fire(new ClickedChannelCommand(DemoChannels.CHANNEL_USA));
//        } else {
//          logger.info("Changing channel to TNT");
//          channelsImageView.setImageResource(R.drawable.guide_mentalist);
//          Program currentProgram = MockData.getProgramForChannel(DemoChannels.CHANNEL_TNT);
//          programTitle.setText(currentProgram.getName());
//          currentlyWatchingDescription.setText(currentProgram.getDescription());
//          currentlyWatchingThumbnail.setImageResource(R.drawable.mentalist);
//          eventManager.fire(new ClickedChannelCommand(DemoChannels.CHANNEL_TNT));
//        }
//      }
//    });
  }

  public void onPanelsStateChanged(@Observes PanelsStateChanged panelsStateChanged) {
    logger.debug("onPanelsStateChanged");
    PanelsStateViewModel panelsStateViewModel = panelsStateChanged.getPanelsStateViewModel();
    boolean isChannelPanelVisible = panelsStateViewModel.isApplicationVisible() && panelsStateViewModel.isChannelsPanelOn();
    getView().setVisibility(isChannelPanelVisible ? View.VISIBLE : View.INVISIBLE);
  }

  public void onChannelsModelChanged(@Observes ChannelsModelChanged channelsModelChanged) {
    logger.debug("onChannelsModelChanged");
    channels = channelsModelChanged.getChannelsViewModel().getSelectableChannels();
    if (channels != null && channels.size() > 0) {
      channelsListAdapter.setChannels(channelsModelChanged.getChannelsViewModel().getSelectableChannels());
    } else {
      channelsListAdapter.setChannels(null);
    }
  }

  @Override
  public void onListItemClick(ListView l, View v, int position, long id) {
    super.onListItemClick(l, v, position, id);
    logger.debug("vTV: GuideFragment onListItemClick");
    Channel clickedChannel = channels.get(position);
    logger.debug("Clicked channel: " + clickedChannel);
    if (clickedChannel != null) {
      eventManager.fire(new ClickedChannelCommand(clickedChannel.getId()));
    }
  }

  private void buildGuide() {
    logger.debug("Building guide");
    int[] guideItemWidths = new int[]{350, 550, 350, 400, 350, 450, 350, 300, 350, 350, 300, 450, 500, 300, 350, 550, 300, 350, 400, 350, 350, 400, 450, 300, 350};

    final int rowsNo = 5;
    final int programsInRowNo = 5;

    for (int i = 0; i < rowsNo; i++) {
      //Create layout for a new row
      LinearLayout guideRow = new LinearLayout(getActivity());
      for (int j = 0; j < programsInRowNo; j++) {
        //Add programs to the row
        GuideItem program = new GuideItem(getActivity());
        program.setProgramTitle(ResourceHelper.getResourceString("guide_program_title_" + i + "_" + j, getActivity()));
        program.setProgramTime(ResourceHelper.getResourceString("guide_program_time_" + i + "_" + j, getActivity()));
        program.setWidth(guideItemWidths[(i * rowsNo) + j]);
        final int finalRowNo = i;
        final int finalItemNo = j;
        program.setOnClickListener(new View.OnClickListener() {
          @Override
          public void onClick(View view) {
            logger.debug("Guide item clicked");
            if (view.isSelected()) {
              //programTime.setVisibility(View.VISIBLE);
              Toast.makeText(getActivity(), "changing channel...", Toast.LENGTH_SHORT).show();
              logger.debug("vTV: GuideFragment changing channel");
              if (channels != null) {
                Channel clickedChannel;
                if (finalRowNo == 0) {
                  clickedChannel = findChannelByCallSign("TNT");
                } else if (finalRowNo == 3) {
                  clickedChannel = findChannelByCallSign("USA");
                } else {
                  clickedChannel = channels.get(finalRowNo);
                }
                logger.debug("Clicked channel: " + clickedChannel);
                if (clickedChannel != null) {
                  eventManager.fire(new ClickedChannelCommand(clickedChannel.getId()));
                }
              }
            } else {
              updateProgramInfo(((GuideItem) view).getProgramTitle(), ResourceHelper.getResourceString("guide_program_desc_" + finalRowNo + "_" + finalItemNo, getActivity()), ((GuideItem) view).getProgramTime(), ResourceHelper.getResourceDrawable(ResourceHelper.getResourceString("guide_program_thumbnail_" + finalRowNo + "_" + finalItemNo, getActivity()), getActivity()));
              removeSelectionFromAllItems(rowsNo, programsInRowNo);
              view.setBackgroundDrawable(getResources().getDrawable(R.drawable.guide_item_background_selected));
              view.setSelected(true);
              Toast toast = Toast.makeText(getActivity(), "Press again to switch to this channel", Toast.LENGTH_SHORT);
              toast.setGravity(0, 0, Gravity.CENTER);
              toast.show();
            }
          }
        });
        //Add program to the row
        guideRow.addView(program);
        if (i == 0 && j == 0) program.requestFocus();
        if (i == 3 && j == 1) {
          programToSelect = program;
        }
      }
      //Add row to the container
      programsContainer.addView(guideRow);
    }
    updateProgramInfo(programToSelect.getProgramTitle(), ResourceHelper.getResourceString("guide_program_desc_" + 3 + "_" + 1, getActivity()), programToSelect.getProgramTime(), ResourceHelper.getResourceDrawable(ResourceHelper.getResourceString("guide_program_thumbnail_" + 3 + "_" + 1, getActivity()), getActivity()));
    removeSelectionFromAllItems(rowsNo, programsInRowNo);
    programToSelect.setBackgroundDrawable(getResources().getDrawable(R.drawable.guide_item_background_selected));
    programToSelect.setSelected(true);
    programToSelect.requestFocus();
  }

  private Channel findChannelByCallSign(String callSign) {
    for (Channel channel : channels) {
      if (callSign.equalsIgnoreCase(channel.getCallSign())) {
        return channel;
      }
    }
    return null;
  }

  private void updateProgramInfo(String title, String description, String time, Drawable drawable) {
    //programTime.setVisibility(View.INVISIBLE);
    Calendar calendar = Calendar.getInstance();
    int dayNo = calendar.get(Calendar.DAY_OF_WEEK);
    String dayOfTheWeek = "";
    if (dayNo == 2) {
      dayOfTheWeek = "Mon";
    } else if (dayNo == 3) {
      dayOfTheWeek = "Tue";
    } else if (dayNo == 4) {
      dayOfTheWeek = "Wed";
    } else if (dayNo == 5) {
      dayOfTheWeek = "Thu";
    } else if (dayNo == 6) {
      dayOfTheWeek = "Fry";
    } else if (dayNo == 7) {
      dayOfTheWeek = "Sat";
    } else if (dayNo == 1) {
      dayOfTheWeek = "Sun";
    }
    programTime.setText(dayOfTheWeek + ", " + time);
    programTitle.setText(title);
    currentlyWatchingDescription.setText(description);
    if (drawable != null)
      currentlyWatchingThumbnail.setImageDrawable(drawable);
  }

  private void removeSelectionFromAllItems(int rowsNo, int itemsNo) {
    for (int j = 0; j < rowsNo; j++) {
      LinearLayout row = (LinearLayout) programsContainer.getChildAt(j);
      for (int i = 0; i < itemsNo; i++) {
        LinearLayout item = (LinearLayout) row.getChildAt(i);
        item.setBackgroundDrawable(getResources().getDrawable(R.drawable.guide_item_background));
        item.setSelected(false);
      }
    }
  }

  @Override
  public void onScrollChanged(ScrollView scrollView, int x, int y, int oldx, int oldy) {
    logger.debug("Scrolling to: " + x + ", " + y);
    channelsList.smoothScrollTo(x, y);
  }
}
