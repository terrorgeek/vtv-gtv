package com.miquido.vtv.viewmodel;

import com.miquido.vtv.bo.Friendship;
import com.miquido.vtv.bo.Profile;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: ljazgar
 * Date: 09.08.12
 * Time: 02:36
 * To change this template use File | Settings | File Templates.
 */
public interface FriendsViewModel {

    List<Friendship> getFriends();
    boolean areFriendsLoaded();
    boolean areFriendsLoading();
    String getFriendLoadingError();

}
