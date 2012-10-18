package com.miquido.vtv.events.commands;

import lombok.Getter;

/**
 * Created with IntelliJ IDEA.
 * User: ljazgar
 * Date: 09.08.12
 * Time: 12:20
 * To change this template use File | Settings | File Templates.
 */
public class NotificationsButtonToggledCommand {
    @Getter private final boolean isNotificationsButtonChecked;

    public NotificationsButtonToggledCommand(boolean notificationsButtonChecked) {
        isNotificationsButtonChecked = notificationsButtonChecked;
    }
}
