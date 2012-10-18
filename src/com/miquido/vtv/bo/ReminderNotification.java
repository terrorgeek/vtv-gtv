/**
 * Created by Krzysztof Biga on 21.09.12.
 *
 * Copyright 2012 MiQUiDO <http://www.miquido.com/>. All rights reserved.
 */
package com.miquido.vtv.bo;

/**
 * Reminder notification.
 *
 * @author Krzysztof Biga
 */
public class ReminderNotification extends Notification {
  public static enum Status {
    New("new"), Read("read"), Accepted("accepted"), Rejected("rejected");
    String name;

    private Status(String name) {
      this.name = name;
    }

    public static Status forName(String name) {
      if (name == null) return null;
      for (Status type : Status.class.getEnumConstants()) {
        if (type.name.equalsIgnoreCase(name))
          return type;
      }
      return null;
    }

    public String toString() {
      return name;
    }

  }

  public ReminderNotification() {
    super(Type.reminder);
  }

  public Id getChannelId() {
    return getSubjectId();
  }

  public void setChannelId(Id channelId) {
    setSubjectId(channelId);
  }

  public Status getStatus() {
    return Status.forName(getStatusString());
  }

  public void setStatus(Status status) {
    setStatusString(status.toString());
  }

}
