package com.miquido.vtv.repositories;

import com.google.inject.Singleton;
import com.miquido.vtv.bo.Profile;
import com.miquido.vtv.bo.Session;
import lombok.Getter;
import lombok.Setter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created with IntelliJ IDEA.
 * User: ljazgar
 * Date: 03.08.12
 * Time: 13:45
 * To change this template use File | Settings | File Templates.
 */
@Singleton
public class SessionRepository {
    private static final Logger logger = LoggerFactory.getLogger(SessionRepository.class);

    public enum SessionState {
        LoggedOut, LoggingIn, LoggedIn, LoggingInError, Initializing, Initialized;
    }
    @Getter @Setter private SessionState sessionState = SessionState.LoggedOut;
    @Getter @Setter String loggingInErrorMessage;
    @Getter private Session session = null;
    @Getter @Setter private Profile currentUserProfile = null;

    public void storeSessionData(Session session, Profile currentUserProfile) {
        this.session = session;
        this.currentUserProfile = currentUserProfile;
    }

    public void clearSessionData() {
        session = null;
        currentUserProfile = null;
        loggingInErrorMessage = null;
        sessionState = SessionState.LoggedOut;
    }


}
