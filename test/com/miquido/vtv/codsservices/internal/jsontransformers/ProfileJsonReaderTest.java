package com.miquido.vtv.codsservices.internal.jsontransformers;

import com.google.inject.Inject;
import com.miquido.test.guice.GuiceTest;
import com.miquido.vtv.bo.Id;
import com.miquido.vtv.bo.Profile;
import com.miquido.vtv.codsservices.exceptions.CodsResponseFormatException;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Calendar;
import java.util.Date;

import static org.junit.Assert.*;

/**
 * Created with IntelliJ IDEA.
 * User: ljazgar
 * Date: 29.07.12
 * Time: 11:34
 * To change this template use File | Settings | File Templates.
 */
public class ProfileJsonReaderTest extends GuiceTest {
    private static final Logger logger = LoggerFactory.getLogger(ProfileJsonReaderTest.class);

    private JSONObject profileJson;
    private JSONObject errorJson;

    @Inject
    private ProfileJsonReader profileJsonReader;

    @Before
    public void before() throws JSONException {
        String profileJsonString = "{\n" +
                "  \"id\": \"12bb9aa9-fbdd-1e0a-c53a-4fff6358f941\",\n" +
                "  \"name\": \"Bob Smith\",\n" +
                "  \"first_name\": \"Bob\",\n" +
                "  \"last_name\": \"Smith\",\n" +
                "  \"facebook_id\": \"9999\",\n" +
                "  \"sip_status\": \"online\",\n" +
                "  \"date_entered\": \"2012-07-12 23:55:38\",\n" +
                "  \"date_modified\": \"2012-08-23 18:42:15\",\n" +
                "  \"avatar_id\": \"56e4c1f0-753a-5a8a-f456-5005e11879d8\",\n" +
                "  \"current_channel\": \"2ce4c1f0-753a-5a8a-f06e-5005e11879d8\",\n" +
                "  \"_new_custom_fields\": \"test value\",\n" +
                "  \"test\": \"qwerqwer\",\n" +
                "  \"channel\": \"11\"\n" +
                "}";

        profileJson = (JSONObject) new JSONTokener(profileJsonString).nextValue();

        String errorJsonString = "{\"status\":\"error\",\"error\":\"Access denied.\"}";
        errorJson = (JSONObject) new JSONTokener(errorJsonString).nextValue();

    }

    @Test
    public void testIsProfile() throws Exception {
        logger.debug("ProfileJsonReaderTest.testIsProfile()");

        boolean result = profileJsonReader.isCorrectType(profileJson);
        assertTrue(result);
    }

    @Test
    public void testIsProfile_Error() throws Exception {
        logger.debug("ProfileJsonReaderTest.testIsProfile_Error()");

        boolean result = profileJsonReader.isCorrectType(errorJson);
        assertFalse(result);
    }

    @Test
    public void testTransform() throws Exception {
        logger.debug("ProfileJsonReaderTest.testTransform()");

        Profile profile = profileJsonReader.createObjectFromJson(profileJson);

        logger.debug("Profile: " + profile);

        assertNotNull(profile);
        assertEquals(Id.valueOf("12bb9aa9-fbdd-1e0a-c53a-4fff6358f941"), profile.getId());
        assertEquals("Bob Smith", profile.getName());
        assertEquals("", profile.getDescription());
        assertEquals("Bob", profile.getFirstName());
        assertEquals("Smith", profile.getLastName());
        assertNotNull(profile.getAvatarId());
        assertEquals(Id.valueOf("56e4c1f0-753a-5a8a-f456-5005e11879d8"), profile.getAvatarId());
        assertEquals("9999", profile.getFacebookId());

//    2012-08-23 18:42:15
        Calendar cal = Calendar.getInstance();
        cal.clear();
        cal.set(2012,Calendar.AUGUST,23,18,42,15);
        Date expectedDateModified = cal.getTime();
        assertEquals(expectedDateModified, profile.getDateModified());

    }

    @Test
    public void testTransform_Error() throws Exception {
        logger.debug("ProfileJsonReaderTest.testTransform_Error()");

        try {
            Profile profile = profileJsonReader.createObjectFromJson(errorJson);
            fail("Exception should be thrown, but it's not. Fail.");
        } catch(CodsResponseFormatException e) {
            logger.debug("Expected exception catched:" + e.getMessage());
        }

    }

}
