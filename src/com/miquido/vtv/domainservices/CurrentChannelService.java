package com.miquido.vtv.domainservices;

import com.google.inject.Inject;
import com.miquido.DemoChannels;
import com.miquido.vtv.bo.Channel;
import com.miquido.vtv.bo.Id;
import com.miquido.vtv.bo.Profile;
import com.miquido.vtv.codsservices.ProfilesCodsDao;
import com.miquido.vtv.codsservices.UsersCodsDao;
import com.miquido.vtv.repositories.ChannelsRepository;
import com.miquido.vtv.repositories.CurrentChannelRepository;
import com.miquido.vtv.repositories.SessionRepository;
import com.miquido.vtv.viewmodel.CurrentChannelViewModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: ljazgar
 * Date: 07.09.12
 * Time: 00:41
 * To change this template use File | Settings | File Templates.
 */
public class CurrentChannelService implements CurrentChannelViewModel {
  private static final Logger logger = LoggerFactory.getLogger(CurrentChannelService.class);

  /**
   *
   * @return true if currently watched channel is different than currentChannel in user profile in CODS, so profile
   *          in CODS need to be updated.
   */
  public boolean initiateCurrentlyWatchedChannel() {

    Id currentlyWatchedChannel = currentChannelRepository.getCurrentlyWatchedChannelId();
    Profile userProfile = sessionService.getUserProfile();
    Id codsCurrentChannelId = (userProfile!=null) ? userProfile.getCurrentChannelId() : null;

    if (currentlyWatchedChannel==null) {
      currentlyWatchedChannel =
          (codsCurrentChannelId!=null && channelsService.isSelectableChannel(codsCurrentChannelId))
              ? codsCurrentChannelId
              : chooseFirstSelectableChannel();
      currentChannelRepository.setCurrentlyWatchedChannelId(currentlyWatchedChannel);
    }

    return !equalsChannelsId(currentlyWatchedChannel, codsCurrentChannelId);
  }

  /**
   * Changes channel in local repository.
   * It does not update current channel in CODS server.
   * @param channelId
   * @return true - if changed
   */
  public boolean changeChannel(Id channelId) {
    if (channelId==null || channelsRepository.getCodsChannelForId(channelId)==null) {
      return false;
    }
    currentChannelRepository.setCurrentlyWatchedChannelId(channelId);
    return true;
  }

  public boolean codsChannelNeedsChange() {
    Id currentlyWatchedChannel = currentChannelRepository.getCurrentlyWatchedChannelId();
    Profile userProfile = sessionService.getUserProfile();
    Id codsCurrentChannelId = (userProfile!=null) ? userProfile.getCurrentChannelId() : null;
    return !equalsChannelsId(currentlyWatchedChannel, codsCurrentChannelId);
  }

  public void updateCodsChannel() {
    Id currentlyWatchedChannel = currentChannelRepository.getCurrentlyWatchedChannelId();
    Profile profileChangeData = new Profile();
    profileChangeData.setCurrentChannelId( currentlyWatchedChannel );
    profileChangeData.setRequestedChannelId(null);
    Profile changeDataIndicator = new Profile();
    changeDataIndicator.setCurrentChannelId(Id.valueOf("00000000-0000-0000-0000-000000000000"));
    changeDataIndicator.setRequestedChannelId(Id.valueOf("00000000-0000-0000-0000-000000000000"));

    Profile resultProfile = profilesCodsDao.update(sessionRepository.getSession().getId(), sessionRepository.getCurrentUserProfile().getId(),
            profileChangeData, changeDataIndicator);

    sessionRepository.getCurrentUserProfile().setCurrentChannelId(resultProfile.getCurrentChannelId());
    sessionRepository.getCurrentUserProfile().setRequestedChannelId(resultProfile.getRequestedChannelId());
  }

  private static boolean equalsChannelsId(Id id1, Id id2) {
    if (id1==null) {
      return (id2==null);
    } else {
      return id1.equals(id2);
    }
  }

  private Id chooseFirstSelectableChannel() {
    List<Channel> channels = channelsRepository.getSelectableChannels();
    return  (channels!=null && channels.size()>0) ? channels.get(0).getId() : null;
  }

  public Channel getCurrentlyWatchedChannel() {
    Id channelId = currentChannelRepository.getCurrentlyWatchedChannelId();
    if (channelId==null)
      return null;
    return channelsRepository.getCodsChannelForId(channelId);
  }

  public String getCurrentlyWatchedChannelUri() {
    Channel channel = getCurrentlyWatchedChannel();
    if (channel==null)
      return null;
    return channelsRepository.getGtvChannelUriForCallSign(channel.getCallSign());
  }

  /**
   * Updates user requested channel id from cods server.
   */
  public void updateRequestedChannelId() {
    try {
      if (!sessionService.isLoggedIn())
        return;

      Profile newProfile = usersCodsDao.getCurrentUserProfile(sessionRepository.getSession().getId());
      Id requestedChannelId = newProfile.getRequestedChannelId();
      if(!DemoChannels.CHANNEL_TNT.equals(requestedChannelId) && !DemoChannels.CHANNEL_USA.equals(requestedChannelId)) {
        return;
      }

      Profile repositoryProfile = sessionRepository.getCurrentUserProfile();
      repositoryProfile.setRequestedChannelId(requestedChannelId);

      logger.debug("Got requested channel id from CODS: {}", (requestedChannelId!=null)?requestedChannelId.toString():"null");

    } catch (Exception e) {
      logger.warn("Exception while updating requested channel id from CODS.", e);
    }
  }

  /**
   * Check if channel needs to changed, because of request from remote device (eg. iPad).
   * If requested channel is different than currently watched channel, method returns id of requested channel. Otherwise null.
   */
  public Id getRemotelyRequestedChannelChange() {
    if (sessionRepository.getCurrentUserProfile() == null) {
      return null;
    }
    Id requestedChannelId = sessionRepository.getCurrentUserProfile().getRequestedChannelId();
    Id currentlyWatchedChannelId = currentChannelRepository.getCurrentlyWatchedChannelId();
    if (requestedChannelId!=null && !requestedChannelId.equals(currentlyWatchedChannelId)) {
      logger.debug(String.format("Requested channel (%s) is different than currently watched channel (%s). Channel needs change.", requestedChannelId.toString(),
          (currentlyWatchedChannelId!=null)?currentlyWatchedChannelId.toString():"null"));
      return requestedChannelId;
    } else
      return null;
  }

  @Inject
  SessionRepository sessionRepository;
  @Inject
  ChannelsRepository channelsRepository;
  @Inject
  CurrentChannelRepository currentChannelRepository;
  @Inject
  SessionService sessionService;
  @Inject
  ChannelsService channelsService;
  @Inject
  UsersCodsDao usersCodsDao;
  @Inject
  ProfilesCodsDao profilesCodsDao;
}
