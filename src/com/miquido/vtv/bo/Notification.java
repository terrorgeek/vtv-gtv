package com.miquido.vtv.bo;

import lombok.AccessLevel;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

/**
 * Created with IntelliJ IDEA.
 * User: ljazgar
 * Date: 16.09.12
 * Time: 13:06
 * To change this template use File | Settings | File Templates.
 */
@Data
public abstract class Notification extends Entity {

  public static enum Type {
    watchWithMe,
    reminder;
  }

  @Setter(AccessLevel.PROTECTED)
  private Type type;

  private Id fromProfileId;
  private Id toProfileId;
  private Id subjectId;

  @Getter(AccessLevel.PROTECTED)
  @Setter(AccessLevel.PROTECTED)
  private String statusString;

  protected Notification(Type type) {
    this.type = type;
  }
}
