package com.miquido.vtv.codsservices;

import com.google.inject.Inject;
import com.miquido.test.robolectric.RobolectricInjectionTestRunner;
import com.miquido.vtv.bo.Channel;
import com.miquido.vtv.codsservices.dataobjects.PageParams;
import com.miquido.vtv.codsservices.dataobjects.Subcollection;
import com.miquido.vtv.codsservices.internal.impl.ChannelsCodsDaoImpl;
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
public class ChannelsCodsDaoTest {

    private static final Logger logger = LoggerFactory.getLogger(ChannelsCodsDaoTest.class);

    @Inject
    ChannelsCodsDaoImpl channelsCodsDao;
    @Inject
    UsersCodsDao usersCodsDao;

    static String sessionId = null;

    @Before
    public void setUp() {
        Robolectric.getFakeHttpLayer().interceptHttpRequests(false);

        if (sessionId==null) {
            // Login
            logger.debug("ChannelsCodsDaoTest: First test. Executing user logging in.");
            sessionId = usersCodsDao.login(UsersCommonTestData.USER_NAME, UsersCommonTestData.PASSWORD_HASH).getId();
        }
    }


    @Test
    public void testGetChannels() throws Exception {
        logger.debug("ChannelsCodsDaoTest.testGetChannels");

        Subcollection<Channel> channelSubcollection = channelsCodsDao.getChannels(sessionId);

        assertNotNull(channelSubcollection);
        logger.debug("Channels: " + channelSubcollection);
    }

    @Test
    public void testGetChannelsWithNumberOfEntries() throws Exception {
        logger.debug("ChannelsCodsDaoTest.testGetChannelsWithNumberOfEntries");

        Subcollection<Channel> channelSubcollection = channelsCodsDao.getChannels(sessionId, PageParams.createForNOE(10));

        assertNotNull(channelSubcollection);
        logger.debug("Channels: " + channelSubcollection);
        if (channelSubcollection.getTotalCount()>=10) {
            assertEquals(10, channelSubcollection.getResultCount());
            assertEquals(10, channelSubcollection.getNextOffset());
            assertEquals(10, channelSubcollection.getEntries().size());
        } else {
            logger.warn("There is to few channels. Test is not reliable!!!!!");
        }
    }

    @Test
    public void testGetChannelsByPageNum() throws Exception {
        logger.debug("ChannelsCodsDaoTest.testGetChannelsByPageNum");

        Subcollection<Channel> channelSubcollection = channelsCodsDao.getChannels(sessionId, PageParams.createForPageNum(5, 2));

        assertNotNull(channelSubcollection);
        logger.debug("Channels: " + channelSubcollection);
        if (channelSubcollection.getTotalCount()>=10) {
            assertEquals(5, channelSubcollection.getResultCount());
            assertEquals(10, channelSubcollection.getNextOffset());
            assertEquals(5, channelSubcollection.getEntries().size());
        } else {
            logger.warn("There is to few channels. Test is not reliable!!!!!");
        }
    }

    @Test
    public void testGetChannelsByPageNum2() throws Exception {
        logger.debug("ChannelsCodsDaoTest.testGetChannelsByPageNum2");

        Subcollection<Channel> channelSubcollection = channelsCodsDao.getChannels(sessionId, PageParams.createForPageNum(20, 3));

        assertNotNull(channelSubcollection);
        logger.debug("Channels: " + channelSubcollection);
        if (channelSubcollection.getTotalCount()>40 && channelSubcollection.getTotalCount()<60) {
            assertEquals(channelSubcollection.getTotalCount()-40, channelSubcollection.getResultCount());
            assertTrue(channelSubcollection.getTotalCount()<= channelSubcollection.getNextOffset());
            assertEquals(channelSubcollection.getResultCount(), channelSubcollection.getEntries().size());
        } else {
            logger.warn("There is to few channels. Test is not reliable!!!!!");
        }
    }

    @Test
    public void testGetChannelsByOffset() throws Exception {
        logger.debug("ChannelsCodsDaoTest.testGetChannelsByOffset");

        Subcollection<Channel> channelSubcollection = channelsCodsDao.getChannels(sessionId, PageParams.createForOffset(5, 17));

        assertNotNull(channelSubcollection);
        logger.debug("Channels: " + channelSubcollection);
        if (channelSubcollection.getTotalCount()>=22) {
            assertEquals(5, channelSubcollection.getResultCount());
            assertEquals(22, channelSubcollection.getNextOffset());
            assertEquals(5, channelSubcollection.getEntries().size());
        } else {
            logger.warn("There is to few channels. Test is not reliable!!!!!");
        }
    }

    @Test
    public void testGetChannelsByOffset2() throws Exception {
        logger.debug("ChannelsCodsDaoTest.testGetChannelsByOffset2");

        Subcollection<Channel> channelSubcollection = channelsCodsDao.getChannels(sessionId, PageParams.createForOffset(100, 50));

        assertNotNull(channelSubcollection);
        logger.debug("Channels: " + channelSubcollection);
        if (channelSubcollection.getTotalCount()>50) {
            assertEquals(channelSubcollection.getTotalCount()-50, channelSubcollection.getResultCount());
            assertTrue(channelSubcollection.getTotalCount()<= channelSubcollection.getNextOffset());
            assertEquals(channelSubcollection.getResultCount(), channelSubcollection.getEntries().size());
        } else {
            logger.warn("There is to few channels. Test is not reliable!!!!!");
        }
    }

    private static String channelsListToString(List<Channel> channelsList) {
        final StringBuilder sb = new StringBuilder();
        sb.append("Channels List:");
        for (Channel channel: channelsList) {
            sb.append("\n     ").append(channel.toString());
        }
        sb.append("\n----------------");
        return sb.toString();
    }

}
