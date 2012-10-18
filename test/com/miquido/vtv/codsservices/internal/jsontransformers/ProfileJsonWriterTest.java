package com.miquido.vtv.codsservices.internal.jsontransformers;

import com.miquido.vtv.bo.Id;
import com.miquido.vtv.bo.Profile;
import com.miquido.vtv.codsservices.internal.impl.mocks.IdRandomizer;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.junit.Assert.*;

/**
 * Created with IntelliJ IDEA.
 * User: ljazgar
 * Date: 12.09.12
 * Time: 12:06
 * To change this template use File | Settings | File Templates.
 */
public class ProfileJsonWriterTest {

    private static final Logger logger = LoggerFactory.getLogger(ProfileJsonWriterTest.class);

    IdRandomizer idRandomizer = new IdRandomizer();
    ProfileJsonWriter profileJsonWriter = new ProfileJsonWriter();


    @Test
    public void testCreateJsonFromObject() throws Exception {
        logger.debug("ProfileJsonWriterTest.testCreateJsonFromObject");

        Profile profile = new Profile();
        Id channelId = idRandomizer.getNewId();
        profile.setCurrentChannelId(channelId);

        JSONObject jsonObject = profileJsonWriter.createJsonFromObject(profile);
        logger.debug("JSON:\n" + jsonObject);
        assertEquals(channelId.toString(), jsonObject.get("current_channel_id"));
        assertEquals( 1, jsonObject.length() );
    }

    @Test
    public void testCreateJsonFromObject2() throws Exception {
        logger.debug("ProfileJsonWriterTest.testCreateJsonFromObject2");

        Profile profile = new Profile();
        Id channelId = idRandomizer.getNewId();
        profile.setCurrentChannelId(channelId);
        profile.setName("testname");
        profile.setFirstName("testfirstname");

        Profile dataToUpdateIndicator = new Profile();
        dataToUpdateIndicator.setName("");
        dataToUpdateIndicator.setOnline(true);
        dataToUpdateIndicator.setCurrentChannelId(channelId);
        dataToUpdateIndicator.setDescription("");

        JSONObject jsonObject = profileJsonWriter.createJsonFromObject(profile, dataToUpdateIndicator);
        logger.debug("JSON:\n" + jsonObject);

        assertEquals( 4, jsonObject.length() );
        assertEquals(channelId.toString(), jsonObject.get("current_channel_id"));
        assertEquals("testname", jsonObject.get("name"));
        assertEquals("offline", jsonObject.get("sip_status"));
        assertEquals(JSONObject.NULL, jsonObject.get("description"));
        assertTrue( !jsonObject.has("first_name"));
    }
}
