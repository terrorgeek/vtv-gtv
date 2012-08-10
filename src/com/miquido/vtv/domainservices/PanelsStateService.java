package com.miquido.vtv.domainservices;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.miquido.vtv.repositories.PanelsStateRepository;
import com.miquido.vtv.viewmodel.PanelsStateViewModel;

/**
 * Created with IntelliJ IDEA.
 * User: ljazgar
 * Date: 09.08.12
 * Time: 10:57
 * To change this template use File | Settings | File Templates.
 */
@Singleton
public class PanelsStateService implements PanelsStateViewModel {

    @Override
    public boolean isFriendsPanelOn() {
        return panelsStateRepository.isFriendsPanelOn();
    }

    public void setFriendsPanelOn(boolean on) {
        panelsStateRepository.setFriendsPanelOn(on);
    }


    @Inject
    PanelsStateRepository panelsStateRepository;
}
