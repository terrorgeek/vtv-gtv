package com.miquido.vtv.domainservices;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.miquido.vtv.bo.Profile;
import com.miquido.vtv.codsservices.ProfilesCodsDao;
import com.miquido.vtv.codsservices.UsersCodsDao;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * TODO Delete. It's not used.
 */
@Singleton
public class ProfilesService {
    private static final Logger logger = LoggerFactory.getLogger(ProfilesService.class);

    @Inject
    UsersCodsDao usersCodsDao;
    @Inject
    ProfilesCodsDao profilesCodsDao;

    public ProfilesService() {
        logger.debug("vTV: ProfilesService constructor");
    }

    public Profile getProfile(String id) {
        logger.debug("vTV: ProfilesService getProfile invoked");

        Profile profile = new Profile();
        profile.setId(id);
        profile.setName("pearlman");
        profile.setFirstName("Leah");
        profile.setLastName("Pearlman");
        profile.setFullName("Leah Pearlman");

        // Test
        logger.debug("vTV: Get friends");
        String sessionId = usersCodsDao.login("bob.smith@cods.pyctex.net", "103891baca2751a856b094db796e3fee").getId();
        String userProfileId = usersCodsDao.getCurrentUserProfile(sessionId).getId();
        List<Profile> allFriends = profilesCodsDao.getAllFriends(sessionId, userProfileId);
        logger.debug(profileListToString(allFriends));
        return profile;
    }

    private static String profileListToString(List<Profile> profileList) {
        final StringBuilder sb = new StringBuilder();
        sb.append("Profiles List:");
        for (Profile profile: profileList) {
            sb.append("\n     ").append(profile.toString());
        }
        sb.append("\n----------------");
        return sb.toString();
    }

}
