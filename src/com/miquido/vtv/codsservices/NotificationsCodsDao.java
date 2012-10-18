/**
 * Created by Krzysztof Biga on 22.09.12.
 *
 * Copyright 2012 MiQUiDO <http://www.miquido.com/>. All rights reserved.
 */
package com.miquido.vtv.codsservices;

import com.miquido.vtv.bo.Id;
import com.miquido.vtv.bo.Notification;

public interface NotificationsCodsDao {
  Notification update(String sessionId, Id notificationId, Notification notificationData, Notification dataToUpdateIndicator);

  Notification update(String sessionId, Id notificationId, Notification notificationData);

  Notification create(String sessionId, Notification notificationData);
}
