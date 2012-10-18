package com.miquido.vtv.repositories;

import com.google.inject.Singleton;
import com.miquido.vtv.bo.Id;
import com.miquido.vtv.bo.Notification;
import lombok.Getter;
import lombok.Setter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

/**
 */
@Singleton
public class NotificationsRepository {
  private static final Logger logger = LoggerFactory.getLogger(NotificationsRepository.class);

  private boolean areNotificationsLoading;
  @Getter
  @Setter
  String loadingErrorMessage;
  private List<Notification> notifications = null;
  private Map<Id, Notification> idNotificationMap;
  private Date modifyTime = null;

  public NotificationsRepository() {
    logger.debug("vTV: FriendsRepository created");
  }

  public boolean areNotificationsLoading() {
    return areNotificationsLoading;
  }

  public void setNotificationsLoading(boolean areNotificationsLoading) {
    this.areNotificationsLoading = areNotificationsLoading;
  }

  /**
   * @return null - not stored yet
   */
  public synchronized List<Notification> getNotifications() {
    return notifications;
  }

  public synchronized void setNotifications(List<Notification> notificationList) {
    this.notifications = copy(notificationList);
    idNotificationMap = new HashMap<Id, Notification>();
    for (Notification notification : notificationList) {
      idNotificationMap.put(notification.getId(), notification);
    }
  }


  public void clearAll() {
    notifications = null;
    loadingErrorMessage = null;
    areNotificationsLoading = false;
    modifyTime = null;
  }

  protected static List<Notification> copy(List<Notification> notificationList) {
    if (notificationList == null)
      return null;
    List<Notification> newNotifications = new ArrayList<Notification>(notificationList.size());
    newNotifications.addAll(notificationList);
    return newNotifications;
  }
}
