package com.miquido.vtv.codsservices;

import com.google.inject.Inject;
import com.miquido.test.robolectric.RobolectricInjectionTestRunner;
import com.miquido.vtv.bo.Profile;
import com.miquido.vtv.bo.Session;
import com.miquido.vtv.codsservices.exceptions.CodsSystemException;
import com.xtremelabs.robolectric.Robolectric;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.junit.Assert.*;

/**
 * Created with IntelliJ IDEA.
 * User: ljazgar
 * Date: 28.07.12
 * Time: 16:15
 * To change this template use File | Settings | File Templates.
 */
@RunWith(RobolectricInjectionTestRunner.class)
public class UsersCodsDaoTest {
    private static final Logger logger = LoggerFactory.getLogger(UsersCodsDaoTest.class);

    @Inject
    UsersCodsDao usersCodsDao;

    @Before
    public void setUp() {
        Robolectric.getFakeHttpLayer().interceptHttpRequests(false);
    }

    @Test
    public void testLogin() throws Exception {
        logger.debug("UsersCodsDaoTest.testLogin");

        Session session = usersCodsDao.login(UsersCommonTestData.USER_NAME, UsersCommonTestData.PASSWORD_HASH);

        assertNotNull(session);
        assertNotNull(session.getId());
        assertEquals(64, session.getId().length());
        assertEquals(UsersCommonTestData.USER_NAME, session.getUserName());
        assertNotNull(session.getUserId());
    }

    @Test
    public void testLoginIncorrectUser() throws Exception {
        logger.debug("UsersCodsDaoTest.testLoginIncorrectUser");

        Session session = usersCodsDao.login("wronguser", "c72381ccfd9e53616d5fd76eceaca638");

        assertNull(session);
    }

    public void testLoginIncorrectUser2() throws Exception {
        logger.debug("UsersCodsDaoTest.testLoginIncorrectUser2");

        Session session = usersCodsDao.login("abcd", "c72381ccfd9e53616d5fd76eceaca638");

        assertNull(session);
    }

    @Test
    public void testLoginWrongPassword() throws Exception {
        logger.debug("UsersCodsDaoTest.testLoginWrongPassword");

        Session session = usersCodsDao.login(UsersCommonTestData.USER_NAME, "c72381ccfd9e53616d5fd76eceaca639");

        assertNull(session);
    }

    @Test
    public void testGetCurrentUserProfile() throws Exception {
        logger.debug("UsersCodsDaoTest.testGetCurrentUserProfile");

        // Login
        Session session = usersCodsDao.login(UsersCommonTestData.USER_NAME, UsersCommonTestData.PASSWORD_HASH);
        Profile profile = usersCodsDao.getCurrentUserProfile(session.getId());

        logger.debug("Profile: " + profile);
        assertNotNull(profile);

    }

//    @Test  Commented because APIUser just have profile.
    public void testGetCurrentUserProfile_UserWithoutProfile() throws Exception {
        logger.debug("UsersCodsDaoTest.testGetCurrentUserProfile");

        // Login
        Session session = usersCodsDao.login("APIuser", "c72381ccfd9e53616d5fd76eceaca638");
        Profile profile = usersCodsDao.getCurrentUserProfile(session.getId());

        logger.debug("Profile: " + profile);
        assertNull(profile);

    }

    @Test
    public void testGetCurrentUserProfileWrongSession() throws Exception {
        logger.debug("UsersCodsDaoTest.testGetCurrentUserProfile_WrongSession");

        try {
            usersCodsDao.getCurrentUserProfile("c72381ccfd9e84732909432eceaca638");
            fail("Exception should be thrown, but it's not. Fail.");
        } catch(CodsSystemException e) {
            logger.debug("Expected exception catched:" + e.getMessage());
        }

    }


}
