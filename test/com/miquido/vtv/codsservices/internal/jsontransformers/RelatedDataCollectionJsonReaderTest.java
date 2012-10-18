package com.miquido.vtv.codsservices.internal.jsontransformers;

import com.google.inject.Inject;
import com.google.inject.Module;
import com.miquido.test.guice.GuiceTest;
import com.miquido.vtv.bo.Profile;
import com.miquido.vtv.codsservices.dataobjects.RelatedDataCollection;
import com.miquido.vtv.codsservices.dataobjects.RelationshipList;
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
 * Date: 31.08.12
 * Time: 14:19
 * To change this template use File | Settings | File Templates.
 */
public class RelatedDataCollectionJsonReaderTest extends GuiceTest {
    private static final Logger logger = LoggerFactory.getLogger(RelatedDataCollectionJsonReaderTest.class);

    @Override
    protected Iterable<? extends Module> getModules() {
        return Arrays.asList(new JsonTransformersModule());
    }

    @Inject
    private RelatedDataCollectionJsonReader<Profile> profileRelatedDataCollectionJsonReader;

    private JSONObject relatedDataCollectionJson;
    private JSONObject errorJson;

    @Before
    public void before() throws JSONException {
        String relatedDataCollectionJsonString = "{\n" +
                "  \"requested_count\": 5,\n" +
                "  \"result_count\": 2,\n" +
                "  \"next_offset\": 2,\n" +
                "  \"total_count\": 2,\n" +
                "  \"entry_list\": [\n" +
                "    {\n" +
                "      \"id\": \"938690c3-dd06-0282-ec57-50193b78f3be\",\n" +
                "      \"name\": \"Fan Zhang\",\n" +
                "      \"first_name\": \"Fan\",\n" +
                "      \"last_name\": \"Zhang\",\n" +
                "      \"facebook_id\": \"100002969431238\",\n" +
                "      \"sip_status\": \"offline\",\n" +
                "      \"date_entered\": \"2012-08-01 14:20:52\",\n" +
                "      \"date_modified\": \"2012-08-01 14:20:52\"\n" +
                "    },\n" +
                "    {\n" +
                "      \"id\": \"65672ac9-70a9-c149-cce8-4fff63ea9803\",\n" +
                "      \"name\": \"Bob Fuentes\",\n" +
                "      \"first_name\": \"Bob\",\n" +
                "      \"last_name\": \"Fuentes\",\n" +
                "      \"facebook_id\": \"\",\n" +
                "      \"sip_status\": \"offline\",\n" +
                "      \"date_entered\": \"2012-07-12 23:55:38\",\n" +
                "      \"date_modified\": \"2012-07-12 23:55:38\"\n" +
                "    }\n" +
                "  ],\n" +
                "  \"relationship_list\": {\n" +
                "    \"938690c3-dd06-0282-ec57-50193b78f3be\": {\n" +
                "      \"id\": \"69113744-d096-fc00-0cf2-503625b1d938\",\n" +
                "      \"name\": \"\",\n" +
                "      \"description\": \"Added as test friend\",\n" +
                "      \"type\": \"\",\n" +
                "      \"status\": \"accepted\",\n" +
                "      \"entry_id\": \"12bb9aa9-fbdd-1e0a-c53a-4fff6358f941\",\n" +
                "      \"related_id\": \"938690c3-dd06-0282-ec57-50193b78f3be\",\n" +
                "      \"date_entered\": \"2012-08-23 12:44:45\",\n" +
                "      \"date_modified\": \"2012-08-23 13:40:35\"\n" +
                "    },\n" +
                "    \"65672ac9-70a9-c149-cce8-4fff63ea9803\": {\n" +
                "      \"id\": \"7c63688c-a4c7-76f4-127b-503e7181c9e7\",\n" +
                "      \"name\": \"\",\n" +
                "      \"description\": \"Friend Bob\",\n" +
                "      \"type\": \"\",\n" +
                "      \"status\": \"new\",\n" +
                "      \"entry_id\": \"12bb9aa9-fbdd-1e0a-c53a-4fff6358f941\",\n" +
                "      \"related_id\": \"65672ac9-70a9-c149-cce8-4fff63ea9803\",\n" +
                "      \"date_entered\": \"2012-08-29 19:45:52\",\n" +
                "      \"date_modified\": \"2012-08-29 19:46:45\"\n" +
                "    }\n" +
                "  }\n" +
                "}";
        relatedDataCollectionJson = (JSONObject) new JSONTokener(relatedDataCollectionJsonString).nextValue();

        String errorJsonString = "{\"status\":\"error\",\"error\":\"Access denied.\"}";
        errorJson = (JSONObject) new JSONTokener(errorJsonString).nextValue();

    }



    @Test
    public void testIsCorrectType() throws Exception {
        logger.debug("RelatedDataCollectionJsonReaderTest.testIsCorrectType()");

        assertTrue(profileRelatedDataCollectionJsonReader.isCorrectType(relatedDataCollectionJson));
    }

    @Test
    public void testCreateObjectFromJson() throws Exception {
        logger.debug("RelatedDataCollectionJsonReaderTest.testCreateObjectFromJson()");

        RelatedDataCollection<Profile> profileRelatedDataCollection
            = profileRelatedDataCollectionJsonReader.createObjectFromJson(relatedDataCollectionJson);

        assertNotNull(profileRelatedDataCollection);
        logger.debug("RelatedDataCollection: " + profileRelatedDataCollection);

        assertEquals(2, profileRelatedDataCollection.getResultCount());

        List<Profile> profiles = profileRelatedDataCollection.getEntries();
        assertNotNull(profiles);
        assertEquals(2, profiles.size());

        RelationshipList relationshipList = profileRelatedDataCollection.getRelationshipList();
        assertNotNull(relationshipList);
        assertEquals(2, relationshipList.size());
    }


    @Test
    public void testIsCorrectType_NoRelations() throws Exception {
        logger.debug("RelatedDataCollectionJsonReaderTest.testIsCorrectType_NoRelations()");
        relatedDataCollectionJson.remove("relationship_list");
        assertFalse(profileRelatedDataCollectionJsonReader.isCorrectType(relatedDataCollectionJson));
    }

    @Test
    public void testCreateObjectFromJson_NoRelations() throws Exception {
        logger.debug("RelatedDataCollectionJsonReaderTest.testCreateObjectFromJson_NoRelations()");
        relatedDataCollectionJson.remove("relationship_list");
        try {
            profileRelatedDataCollectionJsonReader.createObjectFromJson(relatedDataCollectionJson);
            fail("Exception should be thrown, but it's not. Fail.");
        } catch(CodsResponseFormatException e) {
            logger.debug("Expected exception catched:" + e.getMessage());
        }
    }

    @Test
    public void testIsCorrectType_Error() throws Exception {
        logger.debug("RelatedDataCollectionJsonReaderTest.testIsCorrectType_Error()");
        assertFalse(profileRelatedDataCollectionJsonReader.isCorrectType(errorJson));
    }

    @Test
    public void testCreateObjectFromJson_Error() throws Exception {
        logger.debug("RelatedDataCollectionJsonReaderTest.testIsCorrectType_Error()");

        try {
            profileRelatedDataCollectionJsonReader.createObjectFromJson(errorJson);
            fail("Exception should be thrown, but it's not. Fail.");
        } catch(CodsResponseFormatException e) {
            logger.debug("Expected exception catched:" + e.getMessage());
        }
    }

}
