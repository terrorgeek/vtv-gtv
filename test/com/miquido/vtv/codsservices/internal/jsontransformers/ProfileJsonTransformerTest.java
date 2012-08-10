package com.miquido.vtv.codsservices.internal.jsontransformers;

import com.google.inject.Inject;
import com.miquido.test.guice.GuiceTest;
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
public class ProfileJsonTransformerTest extends GuiceTest {
    private static final Logger logger = LoggerFactory.getLogger(ProfileJsonTransformerTest.class);

    private JSONObject profileJson;
    private JSONObject errorJson;

    @Inject
    private ProfileJsonReader profileJsonReader;

    @Before
    public void before() throws JSONException {
        String profileJsonString = "  {\"id\":\"12bb9aa9-fbdd-1e0a-c53a-4fff6358f941\",\n" +
                "      \"name\":\"Nathaniel Russell\",\n" +
                "      \"date_modified\":\"2012-07-26 16:52:50\",\n" +
                "      \"description\":\"\",\n" +
                "      \"first_name\":\"Nathaniel\",\n" +
                "      \"last_name\":\"Russell\",\n" +
                "      \"full_name\":\"Nathaniel Russell\",\n" +
                "      \"email\":\"\",\n" +
                "      \"avatar_id\":\"8083a680-ebf9-2244-b346-4ff74b9c2ae8\",\n" +
                "      \"facebook_id\":\"\",\n" +
                "      \"avatar_url\":\"http:\\/\\/api.cods.pyctex.net\\/images\\/8083a680-ebf9-2244-b346-4ff74b9c2ae8\"}\n";

        profileJson = (JSONObject) new JSONTokener(profileJsonString).nextValue();

        String errorJsonString = "{\"status\":\"error\",\"error\":\"Access denied.\"}";
        errorJson = (JSONObject) new JSONTokener(errorJsonString).nextValue();

    }

    @Test
    public void testIsProfile() throws Exception {
        logger.debug("ProfileJsonTransformerTest.testIsProfile()");

        boolean result = profileJsonReader.isCorrectType(profileJson);
        assertTrue(result);
    }

    @Test
    public void testIsProfile_Error() throws Exception {
        logger.debug("ProfileJsonTransformerTest.testIsProfile_Error()");

        boolean result = profileJsonReader.isCorrectType(errorJson);
        assertFalse(result);
    }

    @Test
    public void testTransform() throws Exception {
        logger.debug("ProfileJsonTransformerTest.testTransform()");

        Profile profile = profileJsonReader.createObjectFromJson(profileJson);

        logger.debug("Profile: " + profile);

        assertNotNull(profile);
        assertEquals("12bb9aa9-fbdd-1e0a-c53a-4fff6358f941", profile.getId());
        assertEquals("Nathaniel Russell", profile.getName());
        assertEquals("", profile.getDescription());
        assertEquals("Nathaniel", profile.getFirstName());
        assertEquals("Russell", profile.getLastName());
        assertEquals("Nathaniel Russell", profile.getFullName());
        assertEquals("", profile.getEmail());
        assertEquals("8083a680-ebf9-2244-b346-4ff74b9c2ae8", profile.getAvatarId());
        assertEquals("http://api.cods.pyctex.net/images/8083a680-ebf9-2244-b346-4ff74b9c2ae8", profile.getAvatarURL());
        assertEquals("", profile.getFacebookId());

//    2012-07-26 16:52:50
        Calendar cal = Calendar.getInstance();
        cal.clear();
        cal.set(2012,Calendar.JULY,26,16,52,50);
        Date expectedDateModified = cal.getTime();
        assertEquals(expectedDateModified, profile.getDateModified());

    }

    @Test
    public void testTransform_Error() throws Exception {
        logger.debug("ProfileJsonTransformerTest.testTransform_Error()");

        try {
            Profile profile = profileJsonReader.createObjectFromJson(errorJson);
            fail("Exception should be thrown, but it's not. Fail.");
        } catch(CodsResponseFormatException e) {
            logger.debug("Expected exception catched:" + e.getMessage());
        }

    }

}
