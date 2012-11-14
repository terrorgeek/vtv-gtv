package com.miquido.vtv.repositories;

import com.google.inject.Singleton;
import com.miquido.vtv.bo.ScheduleEntry;
import lombok.Getter;
import lombok.Setter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

@Singleton
public class ScheduleRepository {
  private static final Logger logger = LoggerFactory.getLogger(ScheduleRepository.class);

  @Getter
  @Setter
  String loadingErrorMessage;
  private List<ScheduleEntry> scheduleEntries = null;

  /**
   * @return null - not stored yet
   */
  public synchronized List<ScheduleEntry> getScheduleEntries() {
    return scheduleEntries;
  }

  public synchronized void setScheduleEntries(List<ScheduleEntry> entries) {
    this.scheduleEntries = copy(entries);
  }


  public void clearAll() {
    scheduleEntries = null;
    loadingErrorMessage = null;
  }

  protected static List<ScheduleEntry> copy(List<ScheduleEntry> entries) {
    if (entries == null)
      return null;
    List<ScheduleEntry> newSchedule = new ArrayList<ScheduleEntry>(entries.size());
    newSchedule.addAll(entries);
    return newSchedule;
  }
}
