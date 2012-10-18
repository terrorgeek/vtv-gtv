package com.miquido.vtv.domainservices;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.miquido.vtv.bo.Profile;
import com.miquido.vtv.bo.Session;
import com.miquido.vtv.codsservices.UsersCodsDao;
import com.miquido.vtv.repositories.CurrentChannelRepository;
import com.miquido.vtv.repositories.FriendsRepository;
import com.miquido.vtv.repositories.PanelsStateRepository;
import com.miquido.vtv.repositories.SessionRepository;
import com.miquido.vtv.viewmodel.SessionViewModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created with IntelliJ IDEA.
 * User: ljazgar
 * Date: 03.08.12
 * Time: 13:49
 * To change this template use File | Settings | File Templates.
 */
@Singleton
public class SessionService implements SessionViewModel {
    private static final Logger logger = LoggerFactory.getLogger(SessionService.class);


    @Override
    public Profile getUserProfile() {
        return sessionRepository.getCurrentUserProfile();
    }

    public boolean isLoggedIn() {
        return ( (sessionRepository.getSessionState()==SessionRepository.SessionState.LoggedIn
                  || sessionRepository.getSessionState()==SessionRepository.SessionState.Initializing
                  || sessionRepository.getSessionState()==SessionRepository.SessionState.Initialized)
                && sessionRepository.getSession()!=null && sessionRepository.getCurrentUserProfile()!=null);
    }

    public boolean isLoggingIn() {
        return sessionRepository.getSessionState()==SessionRepository.SessionState.LoggingIn;
    }

    public void setLoggingIn() {
        sessionRepository.setSessionState(SessionRepository.SessionState.LoggingIn);
    }

    public void login() {
        sessionRepository.setSessionState(SessionRepository.SessionState.LoggingIn);
        try {
            // Get username and password
            // TODO do it properly
            String userName = "bob.smith@cods.pyctex.net";
            String passwordHash = "103891baca2751a856b094db796e3fee";

            Session session = usersCodsDao.login(userName, passwordHash);
            // TODO LJ Handle errors
            Profile currentUserProfile = usersCodsDao.getCurrentUserProfile(session.getId());
            logger.debug("LoggedIn: session="+session.getId());
            sessionRepository.storeSessionData(session, currentUserProfile);
            sessionRepository.setSessionState(SessionRepository.SessionState.LoggedIn);
        } catch(Exception e) {
            sessionRepository.setSessionState(SessionRepository.SessionState.LoggingInError);
            sessionRepository.setLoggingInErrorMessage(e.getMessage());
        }
    }

    public void setSessionInitializing() {
        sessionRepository.setSessionState(SessionRepository.SessionState.Initializing);
    }

    public void initialize() {
        sessionRepository.setSessionState(SessionRepository.SessionState.Initializing);
        try {
            logger.debug("Loading channels");
            channelsService.loadChannels();
            logger.debug("Channels loaded");
            sessionRepository.setSessionState(SessionRepository.SessionState.Initialized);
        } catch(Exception e) {
            sessionRepository.setSessionState(SessionRepository.SessionState.LoggedOut);
            logger.error("Error while loading channels:", e);
        }
    }

    public boolean isSessionInitializing() {
        return sessionRepository.getSessionState()==SessionRepository.SessionState.Initializing;
    }

    public void setSessionInitialized() {
        sessionRepository.setSessionState(SessionRepository.SessionState.Initialized);
    }

    public boolean isSessionInitialized() {
        return sessionRepository.getSessionState()==SessionRepository.SessionState.Initialized;
    }

    public void logout() {
        sessionRepository.clearSessionData();
        sessionRepository.setSessionState(SessionRepository.SessionState.LoggedOut);
        friendsRepository.clearAll();
        channelsService.clearAll();
        panelsStateRepository.setAllPanelsOff();
        currentChannelRepository.setCurrentlyWatchedChannelId(null);
    }

    // ***************************** DEPENDENCIES ************************************************************

    @Inject
    UsersCodsDao usersCodsDao;
    @Inject
    SessionRepository sessionRepository;
    @Inject
    FriendsRepository friendsRepository;
    @Inject
    PanelsStateRepository panelsStateRepository;
    @Inject
    CurrentChannelRepository currentChannelRepository;
    @Inject
    ChannelsService channelsService;
}
