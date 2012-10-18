/**
 * Created by Krzysztof Biga on 22.09.12.
 *
 * Copyright 2012 MiQUiDO <http://www.miquido.com/>. All rights reserved.
 */
package com.miquido.vtv.events.commands;

import com.miquido.vtv.bo.Notification;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * @author Krzysztof Biga
 */
@RequiredArgsConstructor(suppressConstructorProperties = true)
public class NotificationAcceptButtonClicked {
  @Getter final Notification notification;
}
