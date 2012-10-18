package com.miquido.vtv.codsservices.internal.jsontransformers;

import com.google.inject.Inject;
import com.google.inject.Module;
import com.miquido.test.guice.GuiceTest;
import com.miquido.vtv.bo.Profile;
import com.miquido.vtv.codsservices.dataobjects.Subcollection;
import com.miquido.vtv.codsservices.exceptions.CodsResponseFormatException;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.*;

/**
 * Created with IntelliJ IDEA.
 * User: ljazgar
 * Date: 29.07.12
 * Time: 22:56
 * To change this template use File | Settings | File Templates.
 */
public class SubcollectionJsonTransformerTest extends GuiceTest {
    private static final Logger logger = LoggerFactory.getLogger(ProfileJsonReaderTest.class);

    @Inject
    SubcollectionJsonReader<Profile> profileSubcollectionJsonTransformer;

    @Override
    protected Iterable<? extends Module> getModules() {
        return Arrays.asList(new JsonTransformersModule());
    }

    private JSONObject profilePageListJson;
    private JSONObject emptyProfilePageListJson;
    private JSONObject otherTypePageListJson;
    private JSONObject errorJson;

    @Before
    public void before() throws JSONException {
        String profilePageListJsonString = "{\n" +
                "  \"requested_count\": 2,\n" +
                "  \"result_count\": 2,\n" +
                "  \"next_offset\": 5,\n" +
                "  \"total_count\": 617,\n" +
                "  \"entry_list\": [\n" +
                "    {\n" +
                "      \"id\": \"1217a51c-b9fc-3cfa-e0bd-4fff6350250b\",\n" +
                "      \"name\": \"Enrique Yu\",\n" +
                "      \"first_name\": \"Enrique\",\n" +
                "      \"last_name\": \"Yu\",\n" +
                "      \"facebook_id\": \"\",\n" +
                "      \"sip_status\": \"offline\",\n" +
                "      \"date_entered\": \"2012-07-12 23:55:38\",\n" +
                "      \"date_modified\": \"2012-07-12 23:55:38\"\n" +
                "    },\n" +
                "    {\n" +
                "      \"id\": \"12a50f50-487c-5e39-615c-4fff638fe9d9\",\n" +
                "      \"name\": \"Francisco French\",\n" +
                "      \"first_name\": \"Francisco\",\n" +
                "      \"last_name\": \"French\",\n" +
                "      \"facebook_id\": \"\",\n" +
                "      \"sip_status\": \"offline\",\n" +
                "      \"date_entered\": \"2012-07-12 23:55:38\",\n" +
                "      \"date_modified\": \"2012-07-12 23:55:38\"\n" +
                "    }\n" +
                "  ]\n" +
                "}";
        profilePageListJson = (JSONObject) new JSONTokener(profilePageListJsonString).nextValue();

        String emptyProfilePageListJsonString = "{\"result_count\":0,\"total_count\":0,\"next_offset\":0,\"entry_list\":[],\"relationship_list\":[]}";
        emptyProfilePageListJson = (JSONObject) new JSONTokener(emptyProfilePageListJsonString).nextValue();

        String otherTypePageListJsonString = "{\"result_count\":1,\"total_count\":\"2\",\"next_offset\":1,\"entry_list\":[{\"id\":\"91d6ff72-9eef-9b12-f2f1-501172d05ca8\",\"name\":\"My Asus Tablet\",\"date_modified\":\"2012-07-26 16:39:19\",\"description\":\"\"}],\"relationship_list\":[]}";
        otherTypePageListJson = (JSONObject) new JSONTokener(otherTypePageListJsonString).nextValue();

        String errorJsonString = "{\"status\":\"error\",\"error\":\"Access denied.\"}";
        errorJson = (JSONObject) new JSONTokener(errorJsonString).nextValue();

    }

    @Test
    public void testIsCorrectType() throws Exception {
        assertTrue( profileSubcollectionJsonTransformer.isCorrectType(profilePageListJson) );
    }

    @Test
    public void testIsCorrectType_empty() throws Exception {
        assertTrue( profileSubcollectionJsonTransformer.isCorrectType(emptyProfilePageListJson) );
    }

    @Test
    public void testIsCorrectType_notPageList() throws Exception {
        assertFalse(profileSubcollectionJsonTransformer.isCorrectType(errorJson));
    }

    @Test
    public void testIsCorrectType_wrongEntryType() throws Exception {
        assertFalse( profileSubcollectionJsonTransformer.isCorrectType(otherTypePageListJson) );
    }




    @Test
    public void testTransform() throws Exception {
        Subcollection<Profile> profilesSubcollection = profileSubcollectionJsonTransformer.createObjectFromJson(profilePageListJson);

        assertNotNull(profilesSubcollection);
        logger.debug(profilesSubcollection.toString());
        assertEquals(2, profilesSubcollection.getResultCount());
        assertEquals(617, profilesSubcollection.getTotalCount());
        assertEquals(5, profilesSubcollection.getNextOffset());
        List<Profile> profiles = profilesSubcollection.getEntries();
        assertNotNull(profiles);
        assertEquals(2, profiles.size());
        Profile profile0 = profiles.get(0);
        Profile profile1 = profiles.get(1);
        assertNotNull(profile0);
        assertNotNull(profile1);
        assertEquals("Enrique Yu", profile0.getName());
        assertEquals("Francisco French", profile1.getName());
    }

    @Test
    public void testTransform_empty() throws Exception {
        Subcollection<Profile> profilesSubcollection = profileSubcollectionJsonTransformer.createObjectFromJson(emptyProfilePageListJson);

        assertNotNull(profilesSubcollection);
        logger.debug(profilesSubcollection.toString());
        assertEquals(0, profilesSubcollection.getResultCount());
        assertEquals(0, profilesSubcollection.getTotalCount());
        assertEquals(0, profilesSubcollection.getNextOffset());
        List<Profile> profiles = profilesSubcollection.getEntries();
        assertNotNull(profiles);
        assertEquals(0, profiles.size());

    }

    @Test
    public void testTransform_notPageList() throws Exception {
        try {
            profileSubcollectionJsonTransformer.createObjectFromJson(errorJson);
            fail("Exception should be thrown, but it's not. Fail.");
        } catch(CodsResponseFormatException e) {
            logger.debug("Expected exception catched:" + e.getMessage());
        }
    }

    @Test
    public void testTransform_wrongEntryType() throws Exception {
        try {
            profileSubcollectionJsonTransformer.createObjectFromJson(otherTypePageListJson);
            fail("Exception should be thrown, but it's not. Fail.");
        } catch(CodsResponseFormatException e) {
            logger.debug("Expected exception catched:" + e.getMessage());
        }
    }

}
