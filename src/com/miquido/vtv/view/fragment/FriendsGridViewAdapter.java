package com.miquido.vtv.view.fragment;

import android.app.Activity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import com.google.inject.Inject;
import com.google.inject.name.Named;
import com.miquido.android.imageloader.uri.URIBetterLazyImageLoader;
import com.miquido.android.utils.adapter.list.BaseListAdapter;
import com.miquido.vtv.R;
import com.miquido.vtv.bo.Channel;
import com.miquido.vtv.bo.Friendship;
import com.miquido.vtv.bo.Id;
import com.miquido.vtv.domainservices.ChannelsService;
import com.miquido.vtv.domainservices.ImagesService;
import com.miquido.vtv.events.commands.InviteToWatchTogetherButtonClicked;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import roboguice.event.EventManager;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class FriendsGridViewAdapter extends BaseListAdapter<Friendship, FriendsGridViewAdapter.FriendRowViewHolder> {
  private static final Logger logger = LoggerFactory.getLogger(FriendsGridViewAdapter.class);

  static class FriendRowViewHolder {
    public TextView friendNameListField;
    public ImageView friendAvatar;
    public TextView channelCallSignFriendsListField;
    public ImageView channelLogoFriendsListField;
//    public Button inviteButton;
  }

  @RequiredArgsConstructor(suppressConstructorProperties = true)
  static class InviteButtonOnClickListener implements View.OnClickListener {
    final EventManager eventManager;

    @Override
    public void onClick(View view) {
      Friendship friendship = (Friendship) view.getTag();
      if (friendship == null)
        return;
      logger.debug(String.format("clickedFriendShip: %s", friendship));
      eventManager.fire(new InviteToWatchTogetherButtonClicked(friendship));
    }
  }

  final Activity activity;
  final EventManager eventManager;
  final ImagesService imagesService;
  final ChannelsService channelsService;
  List<Friendship> friends;
  Id currentChannelId;
  final URIBetterLazyImageLoader uriLazyImageLoader;

  final View.OnClickListener inviteButtonOnClickListener;

  @Inject
  public FriendsGridViewAdapter(Activity activity,
                                @Named("friendsAvatarsImageLoader") URIBetterLazyImageLoader uriLazyImageLoader,
                                ImagesService imagesService, ChannelsService channelsService,
                                EventManager eventManager) {
    super(activity, R.layout.friends_list_friend_entry);
    this.activity = activity;
    this.imagesService = imagesService;
    this.channelsService = channelsService;
    this.eventManager = eventManager;
    this.friends = new ArrayList<Friendship>();
    this.uriLazyImageLoader = uriLazyImageLoader;
    this.currentChannelId = null;
    this.inviteButtonOnClickListener = new InviteButtonOnClickListener(eventManager);
    reloadData();
  }

  public void initData(List<Friendship> friends, Id currentChannelId) {
    this.friends = (friends != null) ? friends : new ArrayList<Friendship>();
    this.currentChannelId = currentChannelId;
    reloadDataAndNotifyDataSetChanged();
  }

  public void updateFriends(List<Friendship> friends) {
    this.friends = (friends != null) ? friends : new ArrayList<Friendship>();
    reloadDataAndNotifyDataSetChanged();
  }

  public void updateCurrentChannel(Id currentChannelId) {
    this.currentChannelId = currentChannelId;
    reloadDataAndNotifyDataSetChanged();
  }


  private class FriendComparator implements Comparator<Friendship> {
    @Override
    public int compare(Friendship friendship, Friendship friendship1) {
      return friendship.getFriend().getName().compareTo(friendship1.getFriend().getName());
    }
  }

  @Override
  protected FriendRowViewHolder createViewHolder(View view) {
    FriendRowViewHolder friendRowViewHolder = new FriendRowViewHolder();
    friendRowViewHolder.friendNameListField = (TextView) view.findViewById(R.id.friendNameListField);
    friendRowViewHolder.friendAvatar = (ImageView) view.findViewById(R.id.friendAvatar);
    friendRowViewHolder.channelCallSignFriendsListField = (TextView) view.findViewById(R.id.channelCallSignFriendsListField);
    friendRowViewHolder.channelLogoFriendsListField = (ImageView) view.findViewById(R.id.channelLogoFriendsListField);
//    friendRowViewHolder.inviteButton = (Button) view.findViewById(R.id.inviteButton);
    view.setTag(friendRowViewHolder);
    return friendRowViewHolder;
  }

  @Override
  protected void fillViewHolder(FriendRowViewHolder friendRowViewHolder, Friendship friendship, View view, int i) {
    friendRowViewHolder.friendNameListField.setText(friendship.getFriend().getName());
    URI avatarUri = getImageURI(friendship.getFriend().getAvatarId());
    if (avatarUri != null) {
      logger.debug(String.format("setAvatarImage i:%d, view:%s,  uri:%s", i, friendRowViewHolder.friendAvatar, avatarUri.toString()));
      uriLazyImageLoader.scheduleForLoad(friendRowViewHolder.friendAvatar, avatarUri);
    } else {
      uriLazyImageLoader.unregisterFromUpdateAfterDownload(friendRowViewHolder.friendAvatar);
      friendRowViewHolder.friendAvatar.setImageResource(R.drawable.no_image);
    }

    // Channel Logo or callsign
    if (friendship.getFriend().isOnline()) {
      Id channelId = friendship.getFriend().getCurrentChannelId();
      Channel channel = channelsService.getChannel(channelId);
      if (channel != null) {
        URI channelLogoUri = getImageURI(channel.getLogoId());
        logger.debug(String.format("setChannelLogoImage i:%d, view:%s,  uri:%s", i, friendRowViewHolder.channelLogoFriendsListField, channelLogoUri));
        if (channelLogoUri != null) {
          uriLazyImageLoader.scheduleForLoad(friendRowViewHolder.channelLogoFriendsListField, channelLogoUri);
          friendRowViewHolder.channelLogoFriendsListField.setVisibility(View.VISIBLE);
          friendRowViewHolder.channelCallSignFriendsListField.setVisibility(View.INVISIBLE);
        } else {
          uriLazyImageLoader.unregisterFromUpdateAfterDownload(friendRowViewHolder.channelLogoFriendsListField);
          friendRowViewHolder.channelLogoFriendsListField.setVisibility(View.INVISIBLE);
          friendRowViewHolder.channelCallSignFriendsListField.setVisibility(View.VISIBLE);
          friendRowViewHolder.channelCallSignFriendsListField.setText(channel.getCallSign());
        }
      } else {
        friendRowViewHolder.channelLogoFriendsListField.setVisibility(View.INVISIBLE);
        friendRowViewHolder.channelCallSignFriendsListField.setVisibility(View.INVISIBLE);
      }
//      friendRowViewHolder.inviteButton.setVisibility(View.VISIBLE);
//      friendRowViewHolder.inviteButton.setTag(friendship);
//      friendRowViewHolder.inviteButton.setOnClickListener(inviteButtonOnClickListener);
    } else {
      friendRowViewHolder.channelLogoFriendsListField.setVisibility(View.INVISIBLE);
      friendRowViewHolder.channelCallSignFriendsListField.setVisibility(View.INVISIBLE);
//      friendRowViewHolder.inviteButton.setVisibility(View.INVISIBLE);
//      friendRowViewHolder.inviteButton.setTag(null);
//      friendRowViewHolder.inviteButton.setOnClickListener(null);
    }
  }

  @Override
  protected List<Friendship> loadData() {

    FriendComparator friendComparator = new FriendComparator();
    Collections.sort(friends, friendComparator);
    return friends;
  }

  private URI getImageURI(Id imageId) {
    if (imageId == null)
      return null;
    String imageUriString = imagesService.getImageURL(imageId);
    return getURIFromString(imageUriString);
  }

  private URI getURIFromString(String str) {
    if (str == null || str.isEmpty()) {
      return null;
    }
    try {
      return new URI(str);
    } catch (URISyntaxException e) {
      logger.warn(e.getMessage());
      return null;
    }
  }
}
