package com.miquido.vtv.view.fragment;

import android.app.Activity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import com.google.inject.Inject;
import com.google.inject.name.Named;
import com.miquido.android.imageloader.uri.URIBetterLazyImageLoader;
import com.miquido.android.utils.adapter.list.BaseListAdapter;
import com.miquido.vtv.R;
import com.miquido.vtv.bo.Actor;
import com.miquido.vtv.bo.Friendship;
import com.miquido.vtv.bo.Id;
import com.miquido.vtv.domainservices.ChannelsService;
import com.miquido.vtv.domainservices.ImagesService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import roboguice.event.EventManager;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class ActorsGridViewAdapter extends BaseListAdapter<Actor, ActorsGridViewAdapter.ActorRowViewHolder> {
  private static final Logger logger = LoggerFactory.getLogger(ActorsGridViewAdapter.class);

  static class ActorRowViewHolder {
    public TextView actorNameListField;
    public ImageView actorAvatar;
  }

  final Activity activity;
  final EventManager eventManager;
  final ImagesService imagesService;
  final ChannelsService channelsService;
  List<Actor> actors;
  Id currentChannelId;
  final URIBetterLazyImageLoader uriLazyImageLoader;

  @Inject
  public ActorsGridViewAdapter(Activity activity,
                               @Named("friendsAvatarsImageLoader") URIBetterLazyImageLoader uriLazyImageLoader,
                               ImagesService imagesService, ChannelsService channelsService,
                               EventManager eventManager) {
    super(activity, R.layout.actors_list_entry);
    this.activity = activity;
    this.imagesService = imagesService;
    this.channelsService = channelsService;
    this.eventManager = eventManager;
    this.actors = new ArrayList<Actor>();
    this.uriLazyImageLoader = uriLazyImageLoader;
    this.currentChannelId = null;
    reloadData();
  }

  public void initData(List<Actor> actors, Id currentChannelId) {
    this.actors = (actors != null) ? actors : new ArrayList<Actor>();
    this.currentChannelId = currentChannelId;
    reloadDataAndNotifyDataSetChanged();
  }

  public void updateActors(List<Actor> actors) {
    this.actors = (actors != null) ? actors : new ArrayList<Actor>();
    reloadDataAndNotifyDataSetChanged();
  }

  public void updateCurrentChannel(Id currentChannelId) {
    this.currentChannelId = currentChannelId;
    reloadDataAndNotifyDataSetChanged();
  }

  @Override
  protected ActorRowViewHolder createViewHolder(View view) {
    ActorRowViewHolder actorRowViewHolder = new ActorRowViewHolder();
    actorRowViewHolder.actorNameListField = (TextView) view.findViewById(R.id.actorNameListField);
    actorRowViewHolder.actorAvatar = (ImageView) view.findViewById(R.id.actorAvatar);
    view.setTag(actorRowViewHolder);
    return actorRowViewHolder;
  }

  @Override
  protected void fillViewHolder(ActorRowViewHolder actorRowViewHolder, Actor actor, View view, int i) {
    actorRowViewHolder.actorNameListField.setText(actor.getName());
//    actorRowViewHolder.actorAvatar.setImageResource(actor.getPhotoResourceId());
    URI avatarUri = getImageURI(actor.getPhotoId());
    if (avatarUri != null) {
      logger.debug(String.format("setAvatarImage i:%d, view:%s,  uri:%s", i, actorRowViewHolder.actorAvatar, avatarUri.toString()));
      uriLazyImageLoader.scheduleForLoad(actorRowViewHolder.actorAvatar, avatarUri);
    } else {
      uriLazyImageLoader.unregisterFromUpdateAfterDownload(actorRowViewHolder.actorAvatar);
      actorRowViewHolder.actorAvatar.setImageResource(R.drawable.no_image);
    }
  }

  @Override
  protected List<Actor> loadData() {



    return actors;
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
