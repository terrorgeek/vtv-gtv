package com.miquido.vtv.codsservices.internal.impl.mocks;

import com.google.inject.Inject;
import com.miquido.test.robolectric.RobolectricInjectionTestRunner;
import com.miquido.vtv.bo.Id;
import com.miquido.vtv.bo.Profile;
import com.miquido.vtv.codsservices.dataobjects.RelatedDataCollection;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created with IntelliJ IDEA.
 * User: ljazgar
 * Date: 16.08.12
 * Time: 12:58
 * To change this template use File | Settings | File Templates.
 */
@RunWith(RobolectricInjectionTestRunner.class)
public class ProfilesCodsDaoMockImplTest {
    private static final Logger logger = LoggerFactory.getLogger(ProfilesCodsDaoMockImplTest.class);

    @Inject
    ProfilesCodsDaoMockImpl profilesCodsDaoMock;

    @Test
    public void testGetAllFriends() throws Exception {
        RelatedDataCollection<Profile> friendsRelatedDataCollection = profilesCodsDaoMock.getFriends("session", Id.valueOf("00000000-0000-0000-0000-000000000000"), null);

        logger.debug("Count friends: " + friendsRelatedDataCollection.getEntries().size());
        logger.debug("All friends: " + friendsRelatedDataCollection.getEntries());
    }
}
