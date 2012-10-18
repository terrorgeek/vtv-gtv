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
import com.miquido.vtv.bo.Channel;
import com.miquido.vtv.bo.Friendship;
import com.miquido.vtv.bo.Id;
import com.miquido.vtv.bo.Profile;
import com.miquido.vtv.domainservices.ChannelsService;
import com.miquido.vtv.domainservices.ImagesService;
import com.miquido.vtv.events.commands.ChannelsButtonToggledCommand;
import com.miquido.vtv.events.commands.InviteToWatchTogetherButtonClicked;
import lombok.Getter;
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

/**
* Created with IntelliJ IDEA.
* User: ljazgar
* Date: 17.08.12
* Time: 19:12
* To change this template use File | Settings | File Templates.
*/
public class FriendsSectionListAdapter extends ListWithSectionsAdapter<FriendsSectionListAdapter.SectionType, Friendship> {
    private static final Logger logger = LoggerFactory.getLogger(FriendsSectionListAdapter.class);

    protected static enum SectionType {
        watchingSameChannel(R.string.friends_section_watchingSameChannel),
        online(R.string.friends_section_online),
        offline(R.string.friends_section_offline);

        final int stringResource;
        private SectionType(int stringResource) {
            this.stringResource = stringResource;
        }
        public int getStringResource() {
            return stringResource;
        }
    }

    static class FriendRowViewHolder {
        public TextView friendNameListField;
        public ImageView friendAvatar;
        public TextView channelCallSignFriendsListField;
        public ImageView channelLogoFriendsListField;
        public Button inviteButton;
    }
    static class HeaderRowViewHolder {
        public TextView sectionNameListField;
    }

    @RequiredArgsConstructor(suppressConstructorProperties = true)
    static class InviteButtonOnClickListener implements View.OnClickListener {
        final EventManager eventManager;

        @Override
        public void onClick(View view) {
            Friendship friendship = (Friendship)view.getTag();
            if (friendship==null)
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
    final List<Friendship> friendsWatchingSameChannel;
    final List<Friendship> friendsOnline;
    final List<Friendship> friendsOffline;

    final View.OnClickListener inviteButtonOnClickListener;

    @Inject
    public FriendsSectionListAdapter(Activity activity,
                                     @Named("friendsAvatarsImageLoader") URIBetterLazyImageLoader uriLazyImageLoader,
                                     ImagesService imagesService, ChannelsService channelsService,
                                     EventManager eventManager) {
        super(activity, null);
        this.activity = activity;
        this.imagesService = imagesService;
        this.channelsService = channelsService;
        this.eventManager = eventManager;
        this.friends = new ArrayList<Friendship>();
        this.uriLazyImageLoader = uriLazyImageLoader;
        this.currentChannelId = null;
        this.inviteButtonOnClickListener = new InviteButtonOnClickListener(eventManager);

        sections = new ArrayList<ListSection<SectionType, Friendship>>();
        friendsWatchingSameChannel = initSection(sections, SectionType.watchingSameChannel);
        friendsOnline = initSection(sections, SectionType.online);
        friendsOffline = initSection(sections, SectionType.offline);

        reloadData();
    }

    private static List<Friendship> initSection(List<ListSection<SectionType, Friendship>> sections,
                                                          SectionType sectionType) {
        ListSection<SectionType, Friendship> section = new ListSection<SectionType, Friendship>(sectionType);
        sections.add( section );
        return section.getRowElements();
    }

    public void initData(List<Friendship> friends, Id currentChannelId) {
        this.friends = (friends!=null) ? friends : new ArrayList<Friendship>();
        this.currentChannelId = currentChannelId;
        reloadDataAndNotifyDataSetChanged();
    }

    public void updateFriends(List<Friendship> friends) {
        this.friends = (friends!=null) ? friends : new ArrayList<Friendship>();
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
    protected List<ListSection<SectionType, Friendship>> loadData() {
        friendsWatchingSameChannel.clear();
        friendsOnline.clear();
        friendsOffline.clear();
        for(Friendship friendship : friends) {
            Profile friend = friendship.getFriend();
            if (friend.isOnline()
                    && currentChannelId!=null && friend.getCurrentChannelId()!=null
                    && friend.getCurrentChannelId().equals(currentChannelId)) {
                friendsWatchingSameChannel.add(friendship);
            } else if (friend.isOnline()) {
                friendsOnline.add(friendship);
            } else {
                friendsOffline.add(friendship);
            }
        }
        FriendComparator friendComparator = new FriendComparator();
        Collections.sort(friendsWatchingSameChannel, friendComparator);
        Collections.sort(friendsOnline, friendComparator);
        Collections.sort(friendsOffline, friendComparator);

        return sections;
    }

    @Override
    protected View getHeaderView(int i, SectionType sectionType, View convertView, ViewGroup viewGroup) {
        View rowView = convertView;
        if (rowView==null) {
            LayoutInflater inflater = activity.getLayoutInflater();
            rowView = inflater.inflate(R.layout.friends_list_section_header_entry, null);
            HeaderRowViewHolder rowViewHolder = new HeaderRowViewHolder();
            rowViewHolder.sectionNameListField = (TextView)rowView.findViewById(R.id.sectionNameListField);
            rowView.setTag(rowViewHolder);
        }

        HeaderRowViewHolder rowViewHolder = (HeaderRowViewHolder)rowView.getTag();
        rowViewHolder.sectionNameListField.setText(context.getString(sectionType.getStringResource()));
        return rowView;
    }

    @Override
    protected View getRowView(int i, Friendship friendship, View convertView, ViewGroup viewGroup) {
        View rowView = convertView;
        if (rowView==null) {
            LayoutInflater inflater = activity.getLayoutInflater();
            rowView = inflater.inflate(R.layout.friends_list_friend_entry, null);
            FriendRowViewHolder rowViewHolder = new FriendRowViewHolder();
            rowViewHolder.friendNameListField = (TextView)rowView.findViewById(R.id.friendNameListField);
            rowViewHolder.friendAvatar = (ImageView)rowView.findViewById(R.id.friendAvatar);
            rowViewHolder.channelCallSignFriendsListField = (TextView)rowView.findViewById(R.id.channelCallSignFriendsListField);
            rowViewHolder.channelLogoFriendsListField = (ImageView)rowView.findViewById(R.id.channelLogoFriendsListField);
//            rowViewHolder.inviteButton = (Button)rowView.findViewById(R.id.inviteButton);
            rowView.setTag(rowViewHolder);
        }

        FriendRowViewHolder rowViewHolder = (FriendRowViewHolder)rowView.getTag();
        rowViewHolder.friendNameListField.setText(friendship.getFriend().getName());

        // Avatar
        URI avatarUri = getImageURI( friendship.getFriend().getAvatarId() );
        if (avatarUri!=null) {
            logger.debug(String.format("setAvatarImage i:%d, view:%s,  uri:%s", i, rowViewHolder.friendAvatar, avatarUri.toString()));
            uriLazyImageLoader.scheduleForLoad( rowViewHolder.friendAvatar, avatarUri );
        } else {
            uriLazyImageLoader.unregisterFromUpdateAfterDownload(rowViewHolder.friendAvatar);
            rowViewHolder.friendAvatar.setImageResource(R.drawable.friend_avatar_default);
        }

        // Channel Logo or callsign
        if (friendship.getFriend().isOnline()) {
            Id channelId = friendship.getFriend().getCurrentChannelId();
            Channel channel = channelsService.getChannel(channelId);
            if (channel!=null) {
                URI channelLogoUri = getImageURI( channel.getLogoId() );
                logger.debug(String.format("setChannelLogoImage i:%d, view:%s,  uri:%s", i, rowViewHolder.channelLogoFriendsListField, channelLogoUri));
                if (channelLogoUri!=null) {
                    uriLazyImageLoader.scheduleForLoad( rowViewHolder.channelLogoFriendsListField, channelLogoUri );
                    rowViewHolder.channelLogoFriendsListField.setVisibility(View.VISIBLE);
                    rowViewHolder.channelCallSignFriendsListField.setVisibility(View.INVISIBLE);
                } else {
                    uriLazyImageLoader.unregisterFromUpdateAfterDownload(rowViewHolder.channelLogoFriendsListField);
                    rowViewHolder.channelLogoFriendsListField.setVisibility(View.INVISIBLE);
                    rowViewHolder.channelCallSignFriendsListField.setVisibility(View.VISIBLE);
                    rowViewHolder.channelCallSignFriendsListField.setText(channel.getCallSign());
                }
            } else {
                rowViewHolder.channelLogoFriendsListField.setVisibility(View.INVISIBLE);
                rowViewHolder.channelCallSignFriendsListField.setVisibility(View.INVISIBLE);
            }
            rowViewHolder.inviteButton.setVisibility(View.VISIBLE);
            rowViewHolder.inviteButton.setTag(friendship);
            rowViewHolder.inviteButton.setOnClickListener(inviteButtonOnClickListener);
        } else {
            rowViewHolder.channelLogoFriendsListField.setVisibility(View.INVISIBLE);
            rowViewHolder.channelCallSignFriendsListField.setVisibility(View.INVISIBLE);
            rowViewHolder.inviteButton.setVisibility(View.INVISIBLE);
            rowViewHolder.inviteButton.setTag(null);
            rowViewHolder.inviteButton.setOnClickListener(null);
        }

        return rowView;
    }

    private URI getImageURI(Id imageId) {
        if (imageId==null)
            return null;
        String imageUriString = imagesService.getImageURL(imageId);
        return getURIFromString( imageUriString );
    }

    private URI getURIFromString(String str) {
        if (str==null || str.isEmpty()) {
            return null;
        }
        try {
            URI uri = new URI(str);
            return uri;
        } catch (URISyntaxException e) {
            logger.warn(e.getMessage());
            return null;
        }
    }

}
