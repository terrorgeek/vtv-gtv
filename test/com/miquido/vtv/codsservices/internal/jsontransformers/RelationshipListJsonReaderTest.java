package com.miquido.vtv.codsservices.internal.jsontransformers;

import com.google.inject.Inject;
import com.miquido.test.guice.GuiceTest;
import com.miquido.vtv.bo.Id;
import com.miquido.vtv.codsservices.dataobjects.Relationship;
import com.miquido.vtv.codsservices.dataobjects.RelationshipList;
import com.miquido.vtv.codsservices.exceptions.CodsResponseFormatException;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.junit.Assert.*;

/**
 * Created with IntelliJ IDEA.
 * User: ljazgar
 * Date: 31.08.12
 * Time: 13:22
 * To change this template use File | Settings | File Templates.
 */
public class RelationshipListJsonReaderTest  extends GuiceTest {
    private static final Logger logger = LoggerFactory.getLogger(RelationshipListJsonReaderTest.class);

    @Inject
    private RelationshipListJsonReader relationshipListJsonReader;

    private JSONObject relationshipListJson;
    private JSONObject emptyRelationshipListJson;
    private JSONObject notRelationRelationshipListJson;
    private JSONObject errorJson;

    @Before
    public void before() throws JSONException {
        String relationshipListJsonString = "{\n" +
                "    \"938690c3-dd06-0282-ec57-50193b78f3be\": {\n" +
                "      \"id\": \"69113744-d096-fc00-0cf2-503625b1d938\",\n" +
                "      \"name\": \"\",\n" +
                "      \"description\": \"Added as test friend\",\n" +
                "      \"type\": \"friend\",\n" +
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
                "    } }";
        relationshipListJson = (JSONObject) new JSONTokener(relationshipListJsonString).nextValue();

        String emptyRelationshipListJsonString = "{}";
        emptyRelationshipListJson = (JSONObject) new JSONTokener(emptyRelationshipListJsonString).nextValue();

        String notRelationRelationshipListJsonString = "{\n" +
                "    \"938690c3-dd06-0282-ec57-50193b78f3be\": {\n" +
                "      \"id\": \"69113744-d096-fc00-0cf2-503625b1d938\",\n" +
                "    } }";
        notRelationRelationshipListJson = (JSONObject) new JSONTokener(notRelationRelationshipListJsonString).nextValue();

        String errorJsonString = "{\"status\":\"error\",\"error\":\"Access denied.\"}";
        errorJson = (JSONObject) new JSONTokener(errorJsonString).nextValue();

    }


    @Test
    public void testIsCorrectType_Positive() throws Exception {
        logger.debug("RelationshipListJsonReaderTest.testIsCorrectType_Positive()");
        assertTrue(relationshipListJsonReader.isCorrectType(relationshipListJson));
    }

    @Test
    public void testCreateObjectFromJson_Positive() throws Exception {
        logger.debug("RelationshipListJsonReaderTest.testCreateObjectFromJson_Positive()");

        RelationshipList relationshipList = relationshipListJsonReader.createObjectFromJson(relationshipListJson);
        assertNotNull(relationshipList);
        logger.debug("RelationshipList: " + relationshipList);
        assertEquals(2, relationshipList.size());

        Relationship relationship1 = relationshipList.get(Id.valueOf("938690c3-dd06-0282-ec57-50193b78f3be"));
        assertNotNull(relationship1);
        assertEquals(Id.valueOf("69113744-d096-fc00-0cf2-503625b1d938"), relationship1.getId());

        Relationship relationship2 = relationshipList.get(Id.valueOf("65672ac9-70a9-c149-cce8-4fff63ea9803"));
        assertNotNull(relationship2);
        assertEquals(Id.valueOf("7c63688c-a4c7-76f4-127b-503e7181c9e7"), relationship2.getId());
    }


    @Test
    public void testIsCorrectType_PositiveEmpty() throws Exception {
        logger.debug("RelationshipListJsonReaderTest.testIsCorrectType_PositiveEmpty()");
        assertTrue( relationshipListJsonReader.isCorrectType(emptyRelationshipListJson) );
    }

    @Test
    public void testCreateObjectFromJson_PositiveEmpty() throws Exception {
        logger.debug("RelationshipListJsonReaderTest.testCreateObjectFromJson_PositiveEmpty()");

        RelationshipList relationshipList = relationshipListJsonReader.createObjectFromJson(emptyRelationshipListJson);
        assertNotNull(relationshipList);
        logger.debug("RelationshipList: " + relationshipList);
        assertEquals(0, relationshipList.size());
    }


    @Test
    public void testIsCorrectType_WrongValue() throws Exception {
        logger.debug("RelationshipListJsonReaderTest.testIsCorrectType_WrongValue()");
        assertFalse(relationshipListJsonReader.isCorrectType(notRelationRelationshipListJson));
    }

    @Test
    public void testCreateObjectFromJson_WrongValue() throws Exception {
        logger.debug("RelationshipListJsonReaderTest.testCreateObjectFromJson_WrongValue()");

        try {
            RelationshipList relationshipList = relationshipListJsonReader.createObjectFromJson(notRelationRelationshipListJson);
            fail("Exception should be thrown, but it's not. Fail.");
        } catch(CodsResponseFormatException e) {
            logger.debug("Expected exception catched:" + e.getMessage());
        }
    }

    @Test
    public void testIsCorrectType_Error() throws Exception {
        logger.debug("RelationshipListJsonReaderTest.testIsCorrectType_Error()");
        assertFalse(relationshipListJsonReader.isCorrectType(errorJson));
    }

    @Test
    public void testCreateObjectFromJson_Error() throws Exception {
        logger.debug("RelationshipListJsonReaderTest.testIsCorrectType_Error()");

        try {
            RelationshipList relationshipList = relationshipListJsonReader.createObjectFromJson(errorJson);
            fail("Exception should be thrown, but it's not. Fail.");
        } catch(CodsResponseFormatException e) {
            logger.debug("Expected exception catched:" + e.getMessage());
        }
    }

}
