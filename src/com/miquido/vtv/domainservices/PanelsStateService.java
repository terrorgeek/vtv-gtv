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
  public boolean isApplicationVisible() {
    return panelsStateRepository.isApplicationVisible();
  }

  public void setApplicationVisible(boolean visible) {
    panelsStateRepository.setApplicationVisible(visible);
  }

  public void toggleApplicationVisibility() {
    panelsStateRepository.toggleApplicationVisibility();
  }

  @Override
  public boolean isFriendsPanelOn() {
    return panelsStateRepository.isFriendsPanelOn();
  }

  public void setFriendsPanelOn(boolean on) {
    panelsStateRepository.setFriendsPanelOn(on);
  }

  public boolean isChannelsPanelOn() {
    return panelsStateRepository.isChannelPanelOn();
  }

  public void setChannelsPanelOn(boolean on) {
    panelsStateRepository.setChannelPanelOn(on);
  }

  public void setSchedulePanelOn(boolean on) {
    panelsStateRepository.setSchedulePanelOn(on);
  }

  @Override
  public boolean isNotificationsPanelOn() {
    return panelsStateRepository.isNotificationsPanelOn();
  }

  public void setNotificationsPanelOn(boolean on) {
    panelsStateRepository.setNotificationsPanelOn(on);
  }

  @Override
  public boolean isProgramInfoPanelOn() {
    return panelsStateRepository.isProgramInfoPanelOn();
  }

  public void setProgramInfoPanelOn(boolean on) {
    panelsStateRepository.setProgramInfoPanelOn(on);
  }

  @Override
  public boolean isDashboardPanelOn() {
    return panelsStateRepository.isDashboardPanelOn();
  }

  public void setDashboardPanelOn(boolean on) {
    panelsStateRepository.setDashboardPanelOn(on);
  }

  public boolean isSchedulePanelOn() {
    return panelsStateRepository.isSchedulePanelOn();
  }

  public void hideAllPanelsAndTabBar() {
    panelsStateRepository.setAllPanelsOff();
    panelsStateRepository.setTabBarOn(false);
  }

  public void showAllPanelsAndTabBar() {
    panelsStateRepository.setTabBarOn(true);
  }

  public void togglePanelsAndTabBarVisibility() {
    if (panelsStateRepository.isTabBarOn()) {
      panelsStateRepository.setTabBarOn(false);
      panelsStateRepository.setAllPanelsOff();
    } else {
      panelsStateRepository.setTabBarOn(true);
    }
  }

  @Inject
  PanelsStateRepository panelsStateRepository;
}
