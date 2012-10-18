package com.miquido.vtv.events.modelchanges;

import com.miquido.vtv.viewmodel.NotificationsViewModel;
import lombok.Getter;

/**
 * Created with IntelliJ IDEA.
 * User: ljazgar
 * Date: 09.08.12
 * Time: 02:50
 * To change this template use File | Settings | File Templates.
 */
public class NotificationsModelChanged {
  @Getter
  private final NotificationsViewModel notificationsViewModel;

  public NotificationsModelChanged(NotificationsViewModel notificationsViewModel) {
    this.notificationsViewModel = notificationsViewModel;
  }
}
