package com.miquido.vtv.codsservices;

import com.google.inject.Inject;
import com.miquido.test.robolectric.RobolectricInjectionTestRunner;
import com.miquido.vtv.bo.Profile;
import com.miquido.vtv.codsservices.dataobjects.PageList;
import com.xtremelabs.robolectric.Robolectric;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * Created with IntelliJ IDEA.
 * User: ljazgar
 * Date: 29.07.12
 * Time: 23:54
 * To change this template use File | Settings | File Templates.
 */
@RunWith(RobolectricInjectionTestRunner.class)
public class ProfilesCodsDaoTest {

    private static final Logger logger = LoggerFactory.getLogger(ProfilesCodsDaoTest.class);

    @Inject
    ProfilesCodsDao profilesCodsDao;
    @Inject
    UsersCodsDao usersCodsDao;

    static String sessionId = null;
    static String userProfileId = null;

    @Before
    public void setUp() {
        Robolectric.getFakeHttpLayer().interceptHttpRequests(false);

        if (sessionId==null || userProfileId==null) {
            // Login
            logger.debug("ProfilesCodsDaoTest: First test. Executing user logging in.");
            sessionId = usersCodsDao.login(UsersCommonTestData.USER_NAME, UsersCommonTestData.PASSWORD_HASH).getId();
            userProfileId = usersCodsDao.getCurrentUserProfile(sessionId).getId();
        }
    }


    @Test
    public void testGetFriends() throws Exception {
        logger.debug("ProfilesCodsDaoTest.testGetFriends");

        PageList<Profile> profilePageList = profilesCodsDao.getFriends(sessionId, userProfileId);

        assertNotNull(profilePageList);
        logger.debug("Friends: " + profilePageList);
    }

    @Test
    public void testGetFriendsWithNumberOfEntries() throws Exception {
        logger.debug("ProfilesCodsDaoTest.testGetFriendsWithNumberOfEntries");

        PageList<Profile> profilePageList = profilesCodsDao.getFriends(sessionId, userProfileId, 10);

        assertNotNull(profilePageList);
        logger.debug("Friends: " + profilePageList);
        assertEquals(10, profilePageList.getResultCount());
        assertEquals(10, profilePageList.getNextOffset());
        assertEquals(10, profilePageList.getEntries().size());
    }

    @Test
    public void testGetFriendsByPageNum() throws Exception {
        logger.debug("ProfilesCodsDaoTest.testGetFriendsByPageNum");

        PageList<Profile> profilePageList = profilesCodsDao.getFriendsByPageNum(sessionId, userProfileId, 5, 2);

        assertNotNull(profilePageList);
        logger.debug("Friends: " + profilePageList);
        assertEquals(5, profilePageList.getResultCount());
        assertEquals(10, profilePageList.getNextOffset());
        assertEquals(5, profilePageList.getEntries().size());
    }

    @Test
    public void testGetFriendsByPageNum2() throws Exception {
        logger.debug("ProfilesCodsDaoTest.testGetFriendsByPageNum2");

        PageList<Profile> profilePageList = profilesCodsDao.getFriendsByPageNum(sessionId, userProfileId, 20, 3);

        assertNotNull(profilePageList);
        logger.debug("Friends: " + profilePageList);
        assertEquals(profilePageList.getTotalCount()-40, profilePageList.getResultCount());
        assertEquals(profilePageList.getTotalCount(), profilePageList.getNextOffset());
        assertEquals(profilePageList.getResultCount(), profilePageList.getEntries().size());
    }

    @Test
    public void testGetFriendsByOffset() throws Exception {
        logger.debug("ProfilesCodsDaoTest.testGetFriendsByOffset");

        PageList<Profile> profilePageList = profilesCodsDao.getFriendsByOffset(sessionId, userProfileId, 5, 17);

        assertNotNull(profilePageList);
        logger.debug("Friends: " + profilePageList);
        assertEquals(5, profilePageList.getResultCount());
        assertEquals(22, profilePageList.getNextOffset());
        assertEquals(5, profilePageList.getEntries().size());
    }

    @Test
    public void testGetFriendsByOffset2() throws Exception {
        logger.debug("ProfilesCodsDaoTest.testGetFriendsByOffset2");

        PageList<Profile> profilePageList = profilesCodsDao.getFriendsByOffset(sessionId, userProfileId, 100, 50);

        assertNotNull(profilePageList);
        logger.debug("Friends: " + profilePageList);
        assertEquals(profilePageList.getTotalCount()-50, profilePageList.getResultCount());
        assertEquals(profilePageList.getTotalCount(), profilePageList.getNextOffset());
        assertEquals(profilePageList.getResultCount(), profilePageList.getEntries().size());
    }

    @Test
    public void testGetAllFriends_SmallPage() throws Exception {
        logger.debug("ProfilesCodsDaoTest.testGetAllFriends_SmallPage");
        profilesCodsDao.setPageSize(10);
        // What is the total number of friends
        PageList<Profile> profilePageList = profilesCodsDao.getFriends(sessionId, userProfileId, 1);
        int total = profilePageList.getTotalCount();

        // Test
        List<Profile> allFriends = profilesCodsDao.getAllFriends(sessionId, userProfileId);
        assertNotNull(allFriends);
        assertEquals(total, allFriends.size());
        logger.debug(profileListToString(allFriends));
    }

    @Test
    public void testGetAllFriends_BigPage() throws Exception {
        logger.debug("ProfilesCodsDaoTest.testGetAllFriends_BigPage");
        profilesCodsDao.setPageSize(100);
        // What is the total number of friends
        PageList<Profile> profilePageList = profilesCodsDao.getFriends(sessionId, userProfileId, 1);
        int total = profilePageList.getTotalCount();

        // Test
        List<Profile> allFriends = profilesCodsDao.getAllFriends(sessionId, userProfileId);
        assertNotNull(allFriends);
        assertEquals(total, allFriends.size());
        logger.debug(profileListToString(allFriends));
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
