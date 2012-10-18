package com.miquido.vtv.domainservices;

import com.google.inject.Inject;
import com.miquido.vtv.bo.Id;
import com.miquido.vtv.bo.Notification;
import com.miquido.vtv.bo.WatchWithMeNotification;
import com.miquido.vtv.codsservices.NotificationsCodsDao;
import com.miquido.vtv.codsservices.ProfilesCodsDao;
import com.miquido.vtv.codsservices.dataobjects.PageParams;
import com.miquido.vtv.codsservices.dataobjects.Subcollection;
import com.miquido.vtv.codsservices.util.PageByPageLoader;
import com.miquido.vtv.repositories.NotificationsRepository;
import com.miquido.vtv.repositories.SessionRepository;
import com.miquido.vtv.viewmodel.NotificationsViewModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.LinkedList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: ljazgar
 * Date: 03.08.12
 * Time: 16:14
 * To change this template use File | Settings | File Templates.
 */
public class NotificationsService implements NotificationsViewModel {
  private static final Logger logger = LoggerFactory.getLogger(NotificationsService.class);

  @Override
  public boolean areNotificationsLoaded() {
    return (notificationsRepository.getNotifications() != null);
  }

  @Override
  public boolean areNotificationsLoading() {
    return notificationsRepository.areNotificationsLoading();
  }

  public void setNotificationsLoading(boolean areNotificationsLoading) {
    notificationsRepository.setNotificationsLoading(areNotificationsLoading);
  }

  @Override
  public String getNotificationsLoadingError() {
    return notificationsRepository.getLoadingErrorMessage();
  }

  public void loadNotifications() {
    try {
      if (!sessionService.isLoggedIn())
        return;

      notificationsRepository.setNotificationsLoading(true);
      List<Notification> notifications = getAllNotifications(sessionRepository.getSession().getId(), sessionRepository.getCurrentUserProfile().getId());
      notificationsRepository.setNotifications(notifications);
    } catch (Exception e) {
      notificationsRepository.setLoadingErrorMessage(e.getMessage());
    } finally {
      notificationsRepository.setNotificationsLoading(false);
    }
  }

  public List<Notification> updateNotifications() {
    try {
      if (!sessionService.isLoggedIn())
        return null;
      List<Notification> result = new LinkedList<Notification>();
      List<Notification> codsNotifications = getAllNotifications(sessionRepository.getSession().getId(), sessionRepository.getCurrentUserProfile().getId());
      for(Notification notification : codsNotifications) {
        if (!notificationsRepository.getNotifications().contains(notification)) {
          result.add(notification);
        }
      }
      notificationsRepository.setNotifications(codsNotifications);
      return result;
    } catch (Exception e) {
      logger.warn("Exception while updating friends from CODS.", e);
      return null;
    }
  }

  public void createWatchWithMeNotification(WatchWithMeNotification watchWithMeNotification) {
    notificationsCodsDao.create(sessionRepository.getSession().getId(), watchWithMeNotification);
  }

  public void clearNotifications() {
    notificationsRepository.getNotifications().clear();
  }

  public List<Notification> getNotifications() {
    return notificationsRepository.getNotifications();
  }

  private List<Notification> getAllNotifications(final String sessionId, final Id profileId) {

    List<Notification> notifications = pageByPageLoader.getWholeCollection(
        new PageByPageLoader.SingleSubcollectionLoader<Notification>() {
          @Override
          public Subcollection<Notification> load(PageParams pageParams) {
            return profilesCodsDao.getProfileNotifications(sessionId, profileId, pageParams);
          }
        });
    return notifications;
  }

  public void updateCodsNotificationStatus(Notification notification) {
    WatchWithMeNotification changeDataIndicator = new WatchWithMeNotification();
    changeDataIndicator.setStatus(WatchWithMeNotification.Status.Accepted);
    Notification resultNotification =
        notificationsCodsDao.update(sessionRepository.getSession().getId(), notification.getId(), notification, changeDataIndicator);
  }

  @Inject
  SessionRepository sessionRepository;
  @Inject
  NotificationsRepository notificationsRepository;
  @Inject
  ProfilesCodsDao profilesCodsDao;
  @Inject
  NotificationsCodsDao notificationsCodsDao;
  @Inject
  SessionService sessionService;
  @Inject
  PageByPageLoader pageByPageLoader;
}
