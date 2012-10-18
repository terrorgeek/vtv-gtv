package com.miquido.vtv.repositories;

import com.google.inject.Singleton;
import lombok.Getter;
import lombok.Setter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created with IntelliJ IDEA.
 * User: ljazgar
 * Date: 09.08.12
 * Time: 10:50
 * To change this template use File | Settings | File Templates.
 */
@Singleton
public class PanelsStateRepository {
  private static final Logger logger = LoggerFactory.getLogger(PanelsStateRepository.class);

  @Getter
  @Setter
  private boolean applicationVisible = false;

  @Getter
  @Setter
  private boolean friendsPanelOn = false;
  @Getter
  @Setter
  private boolean channelPanelOn = false;
  @Getter
  @Setter
  private boolean notificationsPanelOn = false;
  @Getter
  @Setter
  private boolean programInfoPanelOn = false;
  @Getter
  @Setter
  private boolean dashboardPanelOn = false;
  @Getter
  @Setter
  private boolean schedulePanelOn = false;

  @Getter
  @Setter
  private boolean tabBarOn = false;

  public void setAllPanelsOff() {
    friendsPanelOn = false;
    channelPanelOn = false;
    notificationsPanelOn = false;
    programInfoPanelOn = false;
    dashboardPanelOn = false;
    schedulePanelOn = false;
  }

  public void setAllPanelsOn() {
    friendsPanelOn = true;
    channelPanelOn = true;
    notificationsPanelOn = true;
    programInfoPanelOn = true;
    dashboardPanelOn = true;
    schedulePanelOn = true;
  }

  public void toggleApplicationVisibility() {
    applicationVisible = !applicationVisible;
  }
}
