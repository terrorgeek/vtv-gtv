package com.miquido.vtv.events.commands;

import lombok.Getter;

public class DashboardButtonToggledCommand {
  @Getter
  private final boolean isDashboardButtonChecked;

  public DashboardButtonToggledCommand(boolean dashboardButtonChecked) {
    isDashboardButtonChecked = dashboardButtonChecked;
  }
}
