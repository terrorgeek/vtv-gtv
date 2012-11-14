package com.miquido.vtv.domainservices;

import com.google.inject.Inject;
import com.miquido.vtv.bo.Friendship;
import com.miquido.vtv.bo.Id;
import com.miquido.vtv.bo.Profile;
import com.miquido.vtv.codsservices.ProfilesCodsDao;
import com.miquido.vtv.codsservices.dataobjects.PageParams;
import com.miquido.vtv.codsservices.dataobjects.RelatedDataCollection;
import com.miquido.vtv.codsservices.util.PageByPageLoader;
import com.miquido.vtv.domainservices.internal.relationtranslation.FriendshipsTranslator;
import com.miquido.vtv.repositories.FriendsRepository;
import com.miquido.vtv.repositories.SessionRepository;
import com.miquido.vtv.viewmodel.FriendsViewModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: ljazgar
 * Date: 03.08.12
 * Time: 16:14
 * To change this template use File | Settings | File Templates.
 */
public class  FriendsService implements FriendsViewModel {
  private static final Logger logger = LoggerFactory.getLogger(FriendsService.class);

  @Override
  public boolean areFriendsLoaded() {
    return (friendsRepository.getFriends() != null);
  }

  @Override
  public boolean areFriendsLoading() {
    return friendsRepository.areFriendsLoading();
  }

  public void setFriendsLoading(boolean areFriendsLoading) {
    friendsRepository.setFriendsLoading(areFriendsLoading);
  }

  @Override
  public String getFriendLoadingError() {
    return friendsRepository.getLoadingErrorMessage();
  }

  public String getFriendNameByProfileId(Id friendProfileId) {
    for (Friendship friendship : getFriends()) {
      if (friendProfileId.equals(friendship.getFriendProfileId())) {
        return friendship.getFriend().getName();
      }
    }
    return null;
  }

  public void loadFriends() {
    try {
      if (!sessionService.isLoggedIn())
        return;

      friendsRepository.setFriendsLoading(true);
      List<Friendship> friends = getAllFriends(sessionRepository.getSession().getId(), sessionRepository.getCurrentUserProfile().getId());
      friendsRepository.setFriends(friends);
    } catch (Exception e) {
      friendsRepository.setLoadingErrorMessage(e.getMessage());
    } finally {
      friendsRepository.setFriendsLoading(false);
    }
  }

  public void updateFriends() {
    try {
      if (!sessionService.isLoggedIn())
        return;

      List<Friendship> codsFriends = getAllFriends(sessionRepository.getSession().getId(), sessionRepository.getCurrentUserProfile().getId());
      friendsRepository.setFriends(codsFriends);
    } catch (Exception e) {
      logger.warn("Exception while updating friends from CODS.", e);
    }
  }

  public void clearFriends() {
    friendsRepository.getFriends().clear();
  }

  public List<Friendship> getFriends() {
    return friendsRepository.getFriends();
  }

  private List<Friendship> getAllFriends(final String sessionId, final Id profileId) {

    List<Friendship> friendships = pageByPageLoader.getWholeRelationCollection(
        new PageByPageLoader.SingleRelatedDataCollectionLoader<Profile>() {
          @Override
          public RelatedDataCollection<Profile> load(PageParams pageParams) {
            return profilesCodsDao.getFriends(sessionId, profileId, pageParams);
          }
        }, friendshipsTranslator);
    return friendships;
  }

  @Inject
  SessionRepository sessionRepository;
  @Inject
  FriendsRepository friendsRepository;
  @Inject
  ProfilesCodsDao profilesCodsDao;
  @Inject
  SessionService sessionService;
  @Inject
  FriendshipsTranslator friendshipsTranslator;
  @Inject
  PageByPageLoader pageByPageLoader;
}
