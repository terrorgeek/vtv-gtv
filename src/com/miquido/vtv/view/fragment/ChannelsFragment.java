package com.miquido.vtv.view.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import com.google.inject.Inject;
import com.miquido.DemoChannels;
import com.miquido.MockData;
import com.miquido.vtv.R;
import com.miquido.vtv.bo.Channel;
import com.miquido.vtv.bo.Id;
import com.miquido.vtv.bo.Program;
import com.miquido.vtv.domainservices.CurrentChannelService;
import com.miquido.vtv.events.commands.ChannelsButtonToggledCommand;
import com.miquido.vtv.events.commands.ClickedChannelCommand;
import com.miquido.vtv.events.modelchanges.*;
import com.miquido.vtv.viewmodel.PanelsStateViewModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import roboguice.event.EventManager;
import roboguice.event.Observes;
import roboguice.fragment.RoboListFragment;
import roboguice.inject.InjectView;

import java.text.ParseException;
import java.util.List;

public class ChannelsFragment extends RoboListFragment {
    private static final Logger logger = LoggerFactory.getLogger(ChannelsFragment.class);

    @Inject
    ChannelsListAdapter channelsListAdapter;
    @Inject
    EventManager eventManager;
    @InjectView(R.id.guide_widget)
    ImageView channelsImageView;
  @InjectView(R.id.currently_watching_thumbnail)
  ImageView currentlyWatchingThumbnail;

  @InjectView(R.id.currently_watching_description)
    TextView currentlyWatchingDescription;

    @InjectView(R.id.currently_watching_title)
    TextView programTitle;

  @InjectView(R.id.current_program_title)
  TextView programHeader;

  @Inject
  CurrentChannelService currentChannelService;

    List<Channel> channels = null;

    public ChannelsFragment() {
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
  public void onResume() {
    super.onResume();
    setAppropriateChannel();
  }

  private void setAppropriateChannel() {
    final Channel currentlyWatchedChannel = currentChannelService.getCurrentlyWatchedChannel();
    if(currentlyWatchedChannel == null) {
      return;
    }
    logger.info("Channels fragment. Current channel is: {}", currentlyWatchedChannel.getName());
    Program currentProgram = MockData.getProgramForChannel(currentlyWatchedChannel.getId());
    if (DemoChannels.CHANNEL_TNT.equals(currentlyWatchedChannel.getId())) {
      channelsImageView.setImageResource(R.drawable.guide_mentalist);
      currentlyWatchingThumbnail.setImageResource(R.drawable.mentalist);
    } else {
      channelsImageView.setImageResource(R.drawable.guide_ncis);
      currentlyWatchingThumbnail.setImageResource(R.drawable.ncis);
    }
    currentlyWatchingDescription.setText(currentProgram.getDescription());
    programTitle.setText(currentProgram.getName());
    programHeader.setText(currentProgram.getName());
    channelsImageView.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View view) {
        logger.info("Current channel is: {}", currentlyWatchedChannel.getName());
        if (DemoChannels.CHANNEL_TNT.equals(currentlyWatchedChannel.getId())) {
          logger.info("Changing channel to USA");
          channelsImageView.setImageResource(R.drawable.guide_ncis);
          Program currentProgram = MockData.getProgramForChannel(DemoChannels.CHANNEL_USA);
          programTitle.setText(currentProgram.getName());
          currentlyWatchingDescription.setText(currentProgram.getDescription());
          currentlyWatchingThumbnail.setImageResource(R.drawable.ncis);
          eventManager.fire(new ClickedChannelCommand(DemoChannels.CHANNEL_USA));
        } else {
          logger.info("Changing channel to TNT");
          channelsImageView.setImageResource(R.drawable.guide_mentalist);
          Program currentProgram = MockData.getProgramForChannel(DemoChannels.CHANNEL_TNT);
          programTitle.setText(currentProgram.getName());
          currentlyWatchingDescription.setText(currentProgram.getDescription());
          currentlyWatchingThumbnail.setImageResource(R.drawable.mentalist);
          eventManager.fire(new ClickedChannelCommand(DemoChannels.CHANNEL_TNT));
        }
      }
    });
  }

  public void onPanelsStateChanged(@Observes PanelsStateChanged panelsStateChanged) {
        logger.debug("onPanelsStateChanged");
        PanelsStateViewModel panelsStateViewModel = panelsStateChanged.getPanelsStateViewModel();
        boolean isChannelPanelVisible = panelsStateViewModel.isApplicationVisible() && panelsStateViewModel.isChannelsPanelOn();
        getView().setVisibility( isChannelPanelVisible ? View.VISIBLE : View.INVISIBLE);
    }

    public void onChannelsModelChanged(@Observes ChannelsModelChanged channelsModelChanged) {
        logger.debug("onChannelsModelChanged");
        channels = channelsModelChanged.getChannelsViewModel().getSelectableChannels();
        if (channels!=null && channels.size()>0) {
            channelsListAdapter.setChannels(channelsModelChanged.getChannelsViewModel().getSelectableChannels());
        } else {
            channelsListAdapter.setChannels(null);
        }
    }


    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);
        logger.debug("vTV: ChannelsFragment onListItemClick");
        Channel clickedChannel = channels.get(position);
        logger.debug("Clicked channel: " + clickedChannel);
        if (clickedChannel!=null) {
            eventManager.fire(new ClickedChannelCommand(clickedChannel.getId()));
        }
    }


}
