package com.miquido.vtv.codsservices.internal.jsontransformers;

import com.google.inject.Inject;
import com.google.inject.Module;
import com.miquido.test.guice.GuiceTest;
import com.miquido.vtv.bo.Profile;
import com.miquido.vtv.codsservices.dataobjects.PageList;
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
public class PageListJsonTransformerTest extends GuiceTest {
    private static final Logger logger = LoggerFactory.getLogger(ProfileJsonTransformerTest.class);

    @Inject
    PageListJsonReader<Profile> profilePageListJsonTransformer;

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
        String profilePageListJsonString = "{\"result_count\":2,\"total_count\":\"53\",\"next_offset\":2," +
                "\"entry_list\":" +
                "[{\"id\":\"103782b6-aa79-f369-412a-4fff634bdd8b\",\"name\":\"Oscar Mcknight\",\"date_modified\":\"2012-07-12 23:55:38\",\"description\":\"\",\"first_name\":\"Oscar\",\"last_name\":\"Mcknight\",\"full_name\":\"Oscar Mcknight\",\"email\":\"\",\"avatar_id\":\"f171e97a-05d9-d464-80b8-4ff741476e0a\",\"facebook_id\":\"\",\"avatar_url\":\"http:\\/\\/api.cods.pyctex.net\\/images\\/f171e97a-05d9-d464-80b8-4ff741476e0a\"}" +
                ",{\"id\":\"10c54e7d-e76e-69d9-824d-5003f8a0803a\",\"name\":\"Gilbert Coffey\",\"date_modified\":\"2012-07-16 11:19:35\",\"description\":\"\",\"first_name\":\"Gilbert\",\"last_name\":\"Coffey\",\"full_name\":\"Gilbert Coffey\",\"email\":\"\",\"avatar_id\":\"817d0ca5-f623-3c5b-9873-4ff74bef025a\",\"facebook_id\":\"\",\"avatar_url\":\"http:\\/\\/api.cods.pyctex.net\\/images\\/817d0ca5-f623-3c5b-9873-4ff74bef025a\"}]" +
                ",\"relationship_list\":[]}";
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
        assertTrue( profilePageListJsonTransformer.isCorrectType(profilePageListJson) );
    }

    @Test
    public void testIsCorrectType_empty() throws Exception {
        assertTrue( profilePageListJsonTransformer.isCorrectType(emptyProfilePageListJson) );
    }

    @Test
    public void testIsCorrectType_notPageList() throws Exception {
        assertFalse(profilePageListJsonTransformer.isCorrectType(errorJson));
    }

    @Test
    public void testIsCorrectType_wrongEntryType() throws Exception {
        assertFalse( profilePageListJsonTransformer.isCorrectType(otherTypePageListJson) );
    }




    @Test
    public void testTransform() throws Exception {
        PageList<Profile> profilesPageList = profilePageListJsonTransformer.createObjectFromJson(profilePageListJson);

        assertNotNull(profilesPageList);
        logger.debug(profilesPageList.toString());
        assertEquals(2, profilesPageList.getResultCount());
        assertEquals(53, profilesPageList.getTotalCount());
        assertEquals(2, profilesPageList.getNextOffset());
        List<Profile> profiles = profilesPageList.getEntries();
        assertNotNull(profiles);
        assertEquals(2, profiles.size());
        Profile profile0 = profiles.get(0);
        Profile profile1 = profiles.get(1);
        assertNotNull(profile0);
        assertNotNull(profile1);
        assertEquals("Oscar Mcknight", profile0.getName());
        assertEquals("Gilbert Coffey", profile1.getName());
    }

    @Test
    public void testTransform_empty() throws Exception {
        PageList<Profile> profilesPageList = profilePageListJsonTransformer.createObjectFromJson(emptyProfilePageListJson);

        assertNotNull(profilesPageList);
        logger.debug(profilesPageList.toString());
        assertEquals(0, profilesPageList.getResultCount());
        assertEquals(0, profilesPageList.getTotalCount());
        assertEquals(0, profilesPageList.getNextOffset());
        List<Profile> profiles = profilesPageList.getEntries();
        assertNotNull(profiles);
        assertEquals(0, profiles.size());

    }

    @Test
    public void testTransform_notPageList() throws Exception {
        try {
            profilePageListJsonTransformer.createObjectFromJson(errorJson);
            fail("Exception should be thrown, but it's not. Fail.");
        } catch(CodsResponseFormatException e) {
            logger.debug("Expected exception catched:" + e.getMessage());
        }
    }

    @Test
    public void testTransform_wrongEntryType() throws Exception {
        try {
            profilePageListJsonTransformer.createObjectFromJson(otherTypePageListJson);
            fail("Exception should be thrown, but it's not. Fail.");
        } catch(CodsResponseFormatException e) {
            logger.debug("Expected exception catched:" + e.getMessage());
        }
    }

}
