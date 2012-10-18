/**
 * Created by Krzysztof Biga on 21.09.12.
 *
 * CopyrightInviteButtonOnClickListeneriquido.com/>. All rights reserved.
 */
package com.miquido.vtv.view.fragment;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import com.google.inject.Inject;
import com.google.inject.name.Named;
import com.miquido.android.imageloader.uri.URIBetterLazyImageLoader;
import com.miquido.android.utils.adapter.list.ListWithSectionsAdapter;
import com.miquido.vtv.R;
import com.miquido.vtv.bo.Friendship;
import com.miquido.vtv.bo.Id;
import com.miquido.vtv.bo.Notification;
import com.miquido.vtv.bo.WatchWithMeNotification;
import com.miquido.vtv.domainservices.ChannelsService;
import com.miquido.vtv.domainservices.FriendsService;
import com.miquido.vtv.domainservices.ImagesService;
import com.miquido.vtv.events.commands.NotificationAcceptButtonClicked;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import roboguice.event.EventManager;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

/**
 * Adapter providing data for notifications list.
 *
 * @author Krzysztof Biga
 */
public class NotificationsAdapter extends ListWithSectionsAdapter<NotificationsAdapter.SectionType, Notification> {

  private static final Logger logger = LoggerFactory.getLogger(NotificationsAdapter.class);

  final List<Notification> ongoing;
  final List<Notification> reminders;
  private List<Notification> notifications;
  final Activity activity;
  final URIBetterLazyImageLoader uriLazyImageLoader;
  final EventManager eventManager;
  final ImagesService imagesService;
  final FriendsService friendsService;
  final ChannelsService channelsService;
  final View.OnClickListener acceptButtonOnClickListener;

  @Inject
  public NotificationsAdapter(Activity activity,
                              @Named("friendsAvatarsImageLoader") URIBetterLazyImageLoader uriLazyImageLoader,
                              ImagesService imagesService, FriendsService friendsService, ChannelsService channelsService, EventManager eventManager) {
    super(activity, null);
    this.activity = activity;
    this.uriLazyImageLoader = uriLazyImageLoader;
    this.eventManager = eventManager;
    this.imagesService = imagesService;
    this.friendsService = friendsService;
    this.channelsService = channelsService;
    sections = new ArrayList<ListSection<SectionType, Notification>>();
    ongoing = initSection(sections, SectionType.ongoing);
    reminders = initSection(sections, SectionType.reminder);
    acceptButtonOnClickListener = new AcceptButtonOnClickListener(eventManager);
  }

  protected static enum SectionType {
    ongoing(R.string.notifications_section_ongoing),
    reminder(R.string.notifications_section_reminder);

    final int stringResource;

    private SectionType(int stringResource) {
      this.stringResource = stringResource;
    }

    public int getStringResource() {
      return stringResource;
    }
  }

  @RequiredArgsConstructor(suppressConstructorProperties = true)
  static class AcceptButtonOnClickListener implements View.OnClickListener {
    final EventManager eventManager;

    @Override
    public void onClick(View view) {
      Notification notification = (Notification) view.getTag();
      if (notification == null)
        return;
      logger.debug(String.format("clickedNotification: %s", notification));
      eventManager.fire(new NotificationAcceptButtonClicked(notification));
    }
  }

  private static List<Notification> initSection(List<ListSection<SectionType, Notification>> sections, SectionType sectionType) {
    ListSection<SectionType, Notification> section = new ListSection<SectionType, Notification>(sectionType);
    sections.add(section);
    return section.getRowElements();
  }

  public void updateNotifications(List<Notification> notifications) {
    this.notifications = (notifications != null) ? notifications : new ArrayList<Notification>();
    reloadDataAndNotifyDataSetChanged();
  }

  static class NotificationRowViewHolder {
    public TextView notificationText;
    public TextView friendName;
    public ImageView friendAvatar;
    public Button acceptButton;
  }

  static class HeaderRowViewHolder {
    public TextView sectionNameListField;
  }

  @Override
  protected List<ListSection<NotificationsAdapter.SectionType, Notification>> loadData() {
    ongoing.clear();
    reminders.clear();
    for (Notification notification : notifications) {
      if (notification instanceof WatchWithMeNotification && ((WatchWithMeNotification) notification).getStatus() == WatchWithMeNotification.Status.Accepted) {
        reminders.add(notification);
      } else {
        ongoing.add(notification);
      }
    }
    return sections;
  }

  @Override
  protected View getHeaderView(int i, NotificationsAdapter.SectionType sectionType, View view, ViewGroup viewGroup) {
    View rowView = view;
    if (rowView == null) {
      LayoutInflater inflater = activity.getLayoutInflater();
      rowView = inflater.inflate(R.layout.friends_list_section_header_entry, null);
      HeaderRowViewHolder rowViewHolder = new HeaderRowViewHolder();
      rowViewHolder.sectionNameListField = (TextView) rowView.findViewById(R.id.sectionNameListField);
      rowView.setTag(rowViewHolder);
    }
    HeaderRowViewHolder rowViewHolder = (HeaderRowViewHolder) rowView.getTag();
    rowViewHolder.sectionNameListField.setText(activity.getString(sectionType.getStringResource()));
    return rowView;
  }

  @Override
  protected View getRowView(int i, Notification notification, View convertView, ViewGroup viewGroup) {
    View rowView = convertView;
    if (rowView == null) {
      LayoutInflater inflater = activity.getLayoutInflater();
      rowView = inflater.inflate(R.layout.notification_row, null);
      NotificationRowViewHolder rowViewHolder = new NotificationRowViewHolder();
      rowViewHolder.friendName = (TextView) rowView.findViewById(R.id.friendNameListField);
      rowViewHolder.notificationText = (TextView) rowView.findViewById(R.id.notificationText);
      rowViewHolder.friendAvatar = (ImageView) rowView.findViewById(R.id.friendAvatar);
      rowViewHolder.acceptButton = (Button) rowView.findViewById(R.id.acceptButton);
      rowView.setTag(rowViewHolder);
    }

    NotificationRowViewHolder rowViewHolder = (NotificationRowViewHolder) rowView.getTag();
    rowViewHolder.friendName.setText(getFriendName(notification));
    rowViewHolder.notificationText.setText(getNotificationText(notification));
    rowViewHolder.acceptButton.setTag(notification);
    rowViewHolder.acceptButton.setOnClickListener(acceptButtonOnClickListener);

    // Avatar
    URI avatarUri = getImageURI(getFriendAvatarId(notification));
    if (avatarUri != null) {
      logger.debug(String.format("setAvatarImage i:%d, view:%s,  uri:%s", i, rowViewHolder.friendAvatar, avatarUri.toString()));
      uriLazyImageLoader.scheduleForLoad(rowViewHolder.friendAvatar, avatarUri);
    } else {
      uriLazyImageLoader.unregisterFromUpdateAfterDownload(rowViewHolder.friendAvatar);
      rowViewHolder.friendAvatar.setImageResource(R.drawable.friend_avatar_default);
    }
    return rowView;
  }

  private Id getFriendAvatarId(Notification notification) {
    List<Friendship> friends = friendsService.getFriends();
    if (friends != null) {
      for (Friendship friendship : friends) {
        if (notification.getFromProfileId().equals(friendship.getFriendProfileId())) {
          return notification.getFromProfileId();
        }
      }
    }
    return null;
  }

  private String getNotificationText(Notification notification) {
    if (notification.getType() == Notification.Type.watchWithMe) {
      return channelsService.getChannel(notification.getSubjectId()) == null ? "Channel not found" : activity.getString(R.string.notification_text, channelsService.getChannel(notification.getSubjectId()).getName());
    }
    return "";
  }

  private String getFriendName(Notification notification) {
    String friendName = "";
    List<Friendship> friends = friendsService.getFriends();
    if (friends != null) {
      for (Friendship friendship : friends) {
        if (notification.getFromProfileId().equals(friendship.getFriendProfileId())) {
          friendName = friendship.getFriend().getName();
          break;
        }
      }
    }
    return friendName;
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
