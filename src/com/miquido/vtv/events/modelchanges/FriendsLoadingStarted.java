package com.miquido.vtv.events.modelchanges;

import com.miquido.vtv.viewmodel.FriendsViewModel;

/**
 * Created with IntelliJ IDEA.
 * User: ljazgar
 * Date: 09.08.12
 * Time: 21:15
 * To change this template use File | Settings | File Templates.
 */
public class FriendsLoadingStarted extends FriendsModelChanged {

    public FriendsLoadingStarted(FriendsViewModel friendsViewModel) {
        super(friendsViewModel);
    }
}
