/**
 * Created by raho on 10/10/12.
 *
 * Copyright 2012 MiQUiDO <http://www.miquido.com/>. All rights reserved.
 */
package com.miquido.vtv.events.commands;

import lombok.Getter;

public class ScheduleProgramButtonToggledCommand {

  @Getter
  private final boolean isScheduleProgramButtonChecked;

  public ScheduleProgramButtonToggledCommand(boolean scheduleProgramButtonChecked) {
    isScheduleProgramButtonChecked = scheduleProgramButtonChecked;
  }
}
