package com.miquido.vtv.events.modelchanges;

import com.miquido.vtv.viewmodel.ScheduleViewModel;
import lombok.Getter;


public class ScheduleModelChanged {
  @Getter
  private final ScheduleViewModel scheduleViewModel;

  public ScheduleModelChanged(ScheduleViewModel scheduleViewModel) {
    this.scheduleViewModel = scheduleViewModel;
  }
}
