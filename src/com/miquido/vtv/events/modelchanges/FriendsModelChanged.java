package com.miquido.vtv.events.modelchanges;

import com.miquido.vtv.viewmodel.FriendsViewModel;
import lombok.Getter;

/**
 * Created with IntelliJ IDEA.
 * User: ljazgar
 * Date: 09.08.12
 * Time: 02:50
 * To change this template use File | Settings | File Templates.
 */
public class FriendsModelChanged {
    @Getter private final FriendsViewModel friendsViewModel;

    public FriendsModelChanged(FriendsViewModel friendsViewModel) {
        this.friendsViewModel = friendsViewModel;
    }
}
