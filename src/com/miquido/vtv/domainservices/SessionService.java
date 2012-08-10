package com.miquido.vtv.domainservices;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.miquido.vtv.bo.Profile;
import com.miquido.vtv.bo.Session;
import com.miquido.vtv.codsservices.UsersCodsDao;
import com.miquido.vtv.repositories.FriendsRepository;
import com.miquido.vtv.repositories.PanelsStateRepository;
import com.miquido.vtv.repositories.SessionRepository;
import com.miquido.vtv.viewmodel.SessionViewModel;

/**
 * Created with IntelliJ IDEA.
 * User: ljazgar
 * Date: 03.08.12
 * Time: 13:49
 * To change this template use File | Settings | File Templates.
 */
@Singleton
public class SessionService implements SessionViewModel {


    @Override
    public Profile getUserProfile() {
        return sessionRepository.getCurrentUserProfile();
    }

    public boolean isLoggedIn() {
        return (sessionRepository.getSession()!=null && sessionRepository.getCurrentUserProfile()!=null);
    }

    public boolean isLoggingIn() {
        return sessionRepository.isLoggingIn();
    }
    public void setLoggingIn(boolean loggingIn) {
        sessionRepository.setLoggingIn(loggingIn);
    }

    public void login() {
        sessionRepository.setLoggingIn(true);
        try {
            // Get username and password
            // TODO do it properly
            String userName = "bob.smith@cods.pyctex.net";
            String passwordHash = "103891baca2751a856b094db796e3fee";

            Session session = usersCodsDao.login(userName, passwordHash);
            // TODO LJ Handle errors
            Profile currentUserProfile = usersCodsDao.getCurrentUserProfile(session.getId());

            sessionRepository.storeSessionData(session, currentUserProfile);
        } catch(Exception e) {
            sessionRepository.setLoggingInErrorMessage(e.getMessage());
        } finally {
            sessionRepository.setLoggingIn(false);
        }
    }


    public void logout() {
        sessionRepository.clearSessionData();
        friendsRepository.clearAll();
        panelsStateRepository.setAllPanelsOff();
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
}
