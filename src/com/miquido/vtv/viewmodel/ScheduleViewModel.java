package com.miquido.vtv.viewmodel;

import com.miquido.vtv.bo.Notification;
import com.miquido.vtv.bo.Program;
import com.miquido.vtv.bo.ScheduleEntry;

import java.util.List;

public interface ScheduleViewModel {

    List<ScheduleEntry> getSchedule();

}
