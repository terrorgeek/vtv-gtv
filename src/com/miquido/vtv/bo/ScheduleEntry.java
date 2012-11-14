/**
 * Created by Krzysztof Biga on 05.11.12.
 *
 * Copyright 2012 MiQUiDO <http://www.miquido.com/>. All rights reserved.
 */
package com.miquido.vtv.bo;

import lombok.Data;

@Data
public class ScheduleEntry extends Entity implements Comparable<ScheduleEntry> {
  private Id guideId;
  private GuideEntry guideEntry;

  @Override
  public int compareTo(ScheduleEntry scheduleEntry) {
    if (guideEntry == null) {
      return -1;
    }
    if (scheduleEntry.getGuideEntry() == null) {
      return 1;
    }
    return guideEntry.getStartTime().compareTo(scheduleEntry.getGuideEntry().getStartTime());
  }
}
