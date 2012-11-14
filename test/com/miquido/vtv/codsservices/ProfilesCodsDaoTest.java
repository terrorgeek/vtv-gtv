package com.miquido.vtv.codsservices;

import com.google.inject.Inject;
import com.miquido.test.robolectric.RobolectricInjectionTestRunner;
import com.miquido.vtv.bo.Id;
import com.miquido.vtv.bo.Notification;
import com.miquido.vtv.bo.Profile;
import com.miquido.vtv.codsservices.dataobjects.Subcollection;
import com.miquido.vtv.codsservices.dataobjects.PageParams;
import com.miquido.vtv.codsservices.internal.impl.ProfilesCodsDaoImpl;
import com.xtremelabs.robolectric.Robolectric;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

import static org.junit.Assert.*;

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
    ProfilesCodsDaoImpl profilesCodsDao;
    @Inject
    UsersCodsDao usersCodsDao;

    static String sessionId = null;
    static Id userProfileId = null;

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

        Subcollection<Profile> profileSubcollection = profilesCodsDao.getFriends(sessionId, userProfileId);

        assertNotNull(profileSubcollection);
        logger.debug("Friends: " + profileSubcollection);
    }

    @Test
    public void testGetFriendsWithNumberOfEntries() throws Exception {
        logger.debug("ProfilesCodsDaoTest.testGetFriendsWithNumberOfEntries");

        Subcollection<Profile> profileSubcollection = profilesCodsDao.getFriends(sessionId, userProfileId, PageParams.createForNOE(10));

        assertNotNull(profileSubcollection);
        logger.debug("Friends: " + profileSubcollection);
        if (profileSubcollection.getTotalCount()>=10) {
            assertEquals(10, profileSubcollection.getResultCount());
            assertEquals(10, profileSubcollection.getNextOffset());
            assertEquals(10, profileSubcollection.getEntries().size());
        } else {
            logger.warn("There is to few profiles. Test is not reliable!!!!!");
        }
    }

    @Test
    public void testGetFriendsByPageNum() throws Exception {
        logger.debug("ProfilesCodsDaoTest.testGetFriendsByPageNum");

        Subcollection<Profile> profileSubcollection = profilesCodsDao.getFriends(sessionId, userProfileId, PageParams.createForPageNum(5, 2));

        assertNotNull(profileSubcollection);
        logger.debug("Friends: " + profileSubcollection);
        if (profileSubcollection.getTotalCount()>=10) {
            assertEquals(5, profileSubcollection.getResultCount());
            assertEquals(10, profileSubcollection.getNextOffset());
            assertEquals(5, profileSubcollection.getEntries().size());
        } else {
            logger.warn("There is to few profiles. Test is not reliable!!!!!");
        }
    }

    @Test
    public void testGetFriendsByPageNum2() throws Exception {
        logger.debug("ProfilesCodsDaoTest.testGetFriendsByPageNum2");

        Subcollection<Profile> profileSubcollection = profilesCodsDao.getFriends(sessionId, userProfileId, PageParams.createForPageNum(20, 3));

        assertNotNull(profileSubcollection);
        logger.debug("Friends: " + profileSubcollection);
        if (profileSubcollection.getTotalCount()>40) {
            assertEquals(profileSubcollection.getTotalCount()-40, profileSubcollection.getResultCount());
            assertEquals(profileSubcollection.getTotalCount(), profileSubcollection.getNextOffset());
            assertEquals(profileSubcollection.getResultCount(), profileSubcollection.getEntries().size());
        } else {
            logger.warn("There is to few profiles. Test is not reliable!!!!!");
        }
    }

    @Test
    public void testGetFriendsByOffset() throws Exception {
        logger.debug("ProfilesCodsDaoTest.testGetFriendsByOffset");

        Subcollection<Profile> profileSubcollection = profilesCodsDao.getFriends(sessionId, userProfileId, PageParams.createForOffset(5, 17));

        assertNotNull(profileSubcollection);
        logger.debug("Friends: " + profileSubcollection);
        if (profileSubcollection.getTotalCount()>=22) {
            assertEquals(5, profileSubcollection.getResultCount());
            assertEquals(22, profileSubcollection.getNextOffset());
            assertEquals(5, profileSubcollection.getEntries().size());
        } else {
            logger.warn("There is to few profiles. Test is not reliable!!!!!");
        }
    }

    @Test
    public void testGetFriendsByOffset2() throws Exception {
        logger.debug("ProfilesCodsDaoTest.testGetFriendsByOffset2");

        Subcollection<Profile> profileSubcollection = profilesCodsDao.getFriends(sessionId, userProfileId, PageParams.createForOffset(100, 50));

        assertNotNull(profileSubcollection);
        logger.debug("Friends: " + profileSubcollection);
        if (profileSubcollection.getTotalCount()>50) {
            assertEquals(profileSubcollection.getTotalCount()-50, profileSubcollection.getResultCount());
            assertEquals(profileSubcollection.getTotalCount(), profileSubcollection.getNextOffset());
            assertEquals(profileSubcollection.getResultCount(), profileSubcollection.getEntries().size());
        } else {
            logger.warn("There is to few profiles. Test is not reliable!!!!!");
        }
    }

    @Test
    public void testUpdate() {
        logger.debug("ProfilesCodsDaoTest.testUpdate");
        Profile profileData = new Profile();
        Profile resultProfile;
        Id codsCurrentChannelId;

        // FIRST UPDATE
        final String bbcAmericaId = "ff563048-dbcc-11e1-83cd-c263aa2dded5";
        profileData.setCurrentChannelId(Id.valueOf(bbcAmericaId));   // BBC America
        resultProfile = profilesCodsDao.update(sessionId, userProfileId, profileData);
        logger.debug("Result profile 1: " + resultProfile.toString());
        assertEquals(userProfileId, resultProfile.getId());
        assertEquals(bbcAmericaId, resultProfile.getCurrentChannelId().toString());

        codsCurrentChannelId = usersCodsDao.getCurrentUserProfile(sessionId).getCurrentChannelId();
        assertEquals(bbcAmericaId, codsCurrentChannelId.toString());

        // SECOND UPDATE
        final String ngWildId = "ff563a98-dbcc-11e1-83cd-c263aa2dded5";
        profileData.setCurrentChannelId(Id.valueOf(ngWildId));   // National Geografic Wild
        resultProfile = profilesCodsDao.update(sessionId, userProfileId, profileData);
        logger.debug("Result profile 2: " + resultProfile.toString());
        assertEquals(userProfileId, resultProfile.getId());
        assertEquals(ngWildId, resultProfile.getCurrentChannelId().toString());

        codsCurrentChannelId = usersCodsDao.getCurrentUserProfile(sessionId).getCurrentChannelId();
        assertEquals(ngWildId, codsCurrentChannelId.toString());

    }

  @Test
  public void testUpdateRequestedChannelId() {
    logger.debug("ProfilesCodsDaoTest.testUpdateRequestedChannelId");
    Profile profileData = new Profile();
    Profile resultProfile;
    Id codsRequestedChannelId;

    // FIRST UPDATE
    final String bbcAmericaId = "ff563048-dbcc-11e1-83cd-c263aa2dded5";
    profileData.setRequestedChannelId(Id.valueOf(bbcAmericaId));   // BBC America
    resultProfile = profilesCodsDao.update(sessionId, userProfileId, profileData);
    logger.debug("Result profile 1: " + resultProfile.toString());
    assertEquals(userProfileId, resultProfile.getId());
    assertEquals(bbcAmericaId, resultProfile.getRequestedChannelId().toString());

    codsRequestedChannelId = usersCodsDao.getCurrentUserProfile(sessionId).getRequestedChannelId();
    assertEquals(bbcAmericaId, codsRequestedChannelId.toString());

    logger.debug("Second update null");
    // SECOND UPDATE - NULL
    final String ngWildId = "ff563a98-dbcc-11e1-83cd-c263aa2dded5";
    profileData.setRequestedChannelId(null);
    Profile profileUpdateIndicator = new Profile();
    profileUpdateIndicator.setRequestedChannelId(Id.valueOf(ngWildId));

    resultProfile = profilesCodsDao.update(sessionId, userProfileId, profileData, profileUpdateIndicator);
    logger.debug("Result profile 2: " + resultProfile.toString());
    assertEquals(userProfileId, resultProfile.getId());
    assertNull(resultProfile.getRequestedChannelId());

    logger.debug("Read after update null");
    codsRequestedChannelId = usersCodsDao.getCurrentUserProfile(sessionId).getRequestedChannelId();
    assertNull(codsRequestedChannelId);

  }

  @Test
    public void testGetNotifications() throws Exception {
        logger.debug("ProfilesCodsDaoTest.testGetNotifications");

        Subcollection<Notification> notificationSubcollection = profilesCodsDao.getProfileNotifications(sessionId, userProfileId);

        assertNotNull(notificationSubcollection);
        logger.debug("Notifications: " + notificationSubcollection);
    }


}
