package com.miquido.vtv.domainservices;

import com.google.inject.Inject;
import com.miquido.vtv.bo.Profile;
import com.miquido.vtv.codsservices.ProfilesCodsDao;
import com.miquido.vtv.repositories.FriendsRepository;
import com.miquido.vtv.repositories.SessionRepository;
import com.miquido.vtv.viewmodel.FriendsViewModel;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: ljazgar
 * Date: 03.08.12
 * Time: 16:14
 * To change this template use File | Settings | File Templates.
 */
public class FriendsService implements FriendsViewModel {

    @Override
    public boolean areFriendsLoaded() {
        return (friendsRepository.getFriends()!=null);
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

    public void loadFriends() {
        if (!sessionService.isLoggedIn())
            return;
        try {
            friendsRepository.setFriendsLoading(true);
            List<Profile> friends = profilesCodsDao.getAllFriends(sessionRepository.getSession().getId(), sessionRepository.getCurrentUserProfile().getId());
            friendsRepository.setFriends(friends);
        } catch (Exception e) {
            friendsRepository.setLoadingErrorMessage(e.getMessage());
        } finally {
            friendsRepository.setFriendsLoading(false);
        }
    }

    public List<Profile> getFriends() {
        return friendsRepository.getFriends();
    }



    @Inject
    SessionRepository sessionRepository;
    @Inject
    FriendsRepository friendsRepository;
    @Inject
    ProfilesCodsDao profilesCodsDao;
    @Inject
    SessionService sessionService;
}
