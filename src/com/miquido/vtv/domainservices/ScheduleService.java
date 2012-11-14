package com.miquido.vtv.domainservices;

import com.google.inject.Inject;
import com.miquido.vtv.bo.Id;
import com.miquido.vtv.bo.ScheduleEntry;
import com.miquido.vtv.codsservices.ProfilesCodsDao;
import com.miquido.vtv.codsservices.ProgramGuideCodsDao;
import com.miquido.vtv.codsservices.dataobjects.PageParams;
import com.miquido.vtv.codsservices.dataobjects.Subcollection;
import com.miquido.vtv.codsservices.util.PageByPageLoader;
import com.miquido.vtv.repositories.ScheduleRepository;
import com.miquido.vtv.repositories.SessionRepository;
import com.miquido.vtv.viewmodel.ScheduleViewModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: ljazgar
 * Date: 03.08.12
 * Time: 16:14
 * To change this template use File | Settings | File Templates.
 */
public class ScheduleService implements ScheduleViewModel {
  private static final Logger logger = LoggerFactory.getLogger(ScheduleService.class);

  @Override
  public List<ScheduleEntry> getSchedule() {
    return scheduleRepository.getScheduleEntries();
  }

  public void loadSchedule() {
    try {
      if (!sessionService.isLoggedIn())
        return;
      List<ScheduleEntry> schedule = getUserSchedule(sessionRepository.getSession().getId(), sessionRepository.getCurrentUserProfile().getId());
      scheduleRepository.setScheduleEntries(schedule);
      logger.debug("Loaded schedule size: {}", schedule.size());
      logger.debug("Schedule guide id: {}", schedule.get(0).getGuideId());
    } catch (Exception e) {
      scheduleRepository.setLoadingErrorMessage(e.getMessage());
    }
  }

  public void updateSchedule() {
    try {
      if (!sessionService.isLoggedIn())
        return;
      List<ScheduleEntry> scheduleFromCods = getUserSchedule(sessionRepository.getSession().getId(), sessionRepository.getCurrentUserProfile().getId());
      scheduleRepository.setScheduleEntries(scheduleFromCods);
    } catch (Exception e) {
      logger.warn("Exception while updating schedule from CODS.", e);
    }
  }

  public void clearSchedule() {
    scheduleRepository.getScheduleEntries().clear();
  }

  private List<ScheduleEntry> getUserSchedule(final String sessionId, final Id profileId) {

    List<ScheduleEntry> scheduleEntries = pageByPageLoader.getWholeCollection(
        new PageByPageLoader.SingleSubcollectionLoader<ScheduleEntry>() {
          @Override
          public Subcollection<ScheduleEntry> load(PageParams pageParams) {
            return profilesCodsDao.getProfileSchedule(sessionId, profileId);//TODO add page params, bug in CODS prevents us from adding it here
          }
        });
    for (ScheduleEntry scheduleEntry : scheduleEntries) {
      scheduleEntry.setGuideEntry(programGuideCodsDao.getGuideById(sessionId, scheduleEntry.getGuideId().toString()));
    }
    return scheduleEntries;
  }

  @Inject
  SessionRepository sessionRepository;
  @Inject
  ScheduleRepository scheduleRepository;
  @Inject
  ProfilesCodsDao profilesCodsDao;
  @Inject
  ProgramGuideCodsDao programGuideCodsDao;
  @Inject
  SessionService sessionService;
  @Inject
  PageByPageLoader pageByPageLoader;
}
