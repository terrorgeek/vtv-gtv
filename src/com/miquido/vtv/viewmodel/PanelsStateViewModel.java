package com.miquido.vtv.viewmodel;

/**
 * Created with IntelliJ IDEA.
 * User: ljazgar
 * Date: 09.08.12
 * Time: 10:53
 * To change this template use File | Settings | File Templates.
 */
public interface PanelsStateViewModel {

  boolean isApplicationVisible();

  boolean isFriendsPanelOn();
  boolean isChannelsPanelOn();
  boolean isNotificationsPanelOn();
  boolean isProgramInfoPanelOn();
  boolean isDashboardPanelOn();
  boolean isSchedulePanelOn();

}