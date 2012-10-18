package com.miquido.vtv.codsservices.internal.jsontransformers;

import com.google.inject.Inject;
import com.miquido.test.guice.GuiceTest;
import com.miquido.vtv.bo.Id;
import com.miquido.vtv.bo.Profile;
import com.miquido.vtv.codsservices.dataobjects.Relationship;
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
import static org.junit.Assert.assertEquals;

/**
 * Created with IntelliJ IDEA.
 * User: ljazgar
 * Date: 31.08.12
 * Time: 12:07
 * To change this template use File | Settings | File Templates.
 */
public class RelationshipJsonReaderTest extends GuiceTest {

    private static final Logger logger = LoggerFactory.getLogger(ProfileJsonReaderTest.class);

    private JSONObject relationshipJson;
    private JSONObject errorJson;

    @Inject
    private RelationshipJsonReader relationshipJsonReader;

    @Before
    public void before() throws JSONException {

        relationshipJson = getRelationshipJson("friend");

        String errorJsonString = "{\"status\":\"error\",\"error\":\"Access denied.\"}";
        errorJson = (JSONObject) new JSONTokener(errorJsonString).nextValue();

    }

    private String getRelationshipString(String type) {
        String relationshipJsonStringTemplate = "{\n" +
                "      \"id\": \"69113744-d096-fc00-0cf2-503625b1d938\",\n" +
                "      \"name\": \"\",\n" +
                "      \"description\": \"Added as test friend\",\n" +
                "      \"type\": \"%s\",\n" +
                "      \"status\": \"accepted\",\n" +
                "      \"entry_id\": \"12bb9aa9-fbdd-1e0a-c53a-4fff6358f941\",\n" +
                "      \"related_id\": \"938690c3-dd06-0282-ec57-50193b78f3be\",\n" +
                "      \"date_entered\": \"2012-08-23 12:44:45\",\n" +
                "      \"date_modified\": \"2012-08-23 13:40:35\"\n" +
                "    }";
        return String.format(relationshipJsonStringTemplate, type);
    }

    private JSONObject getRelationshipJson(String type) throws JSONException {
        return (JSONObject) new JSONTokener(getRelationshipString(type)).nextValue();
    }

    @Test
    public void testIsCorrectType() throws Exception {
        logger.debug("RelationshipJsonReaderTest.testIsCorrectType()");

        boolean result = relationshipJsonReader.isCorrectType(relationshipJson);
        assertTrue(result);
    }

    @Test
    public void testIsCorrectType_Error() throws Exception {
        logger.debug("RelationshipJsonReaderTest.testIsProfile_Error()");

        boolean result = relationshipJsonReader.isCorrectType(errorJson);
        assertFalse(result);
    }

    @Test
    public void testCreateObjectFromJson() throws Exception {
        logger.debug("RelationshipJsonReaderTest.testCreateObjectFromJson()");

        Relationship relationship = relationshipJsonReader.createObjectFromJson(relationshipJson);

        logger.debug("Relationship: " + relationship);

        assertNotNull(relationship);
        assertEquals(Id.valueOf("69113744-d096-fc00-0cf2-503625b1d938"), relationship.getId());
        assertEquals("Added as test friend", relationship.getDescription());
        assertEquals(Id.valueOf("12bb9aa9-fbdd-1e0a-c53a-4fff6358f941"), relationship.getEntryId());
        assertEquals(Id.valueOf("938690c3-dd06-0282-ec57-50193b78f3be"), relationship.getRelatedEntryId());
        assertEquals("accepted", relationship.getStatus());
        assertEquals(Relationship.Type.Friend, relationship.getType());

//    2012-08-23 13:40:35
        Calendar cal = Calendar.getInstance();
        cal.clear();
        cal.set(2012,Calendar.AUGUST,23,13,40,35);
        Date expectedDateModified = cal.getTime();
        assertEquals(expectedDateModified, relationship.getDateModified());
    }

    @Test
    public void testCreateObjectFromJson_Error() throws Exception {
        logger.debug("RelationshipJsonReaderTest.testCreateObjectFromJson_Error()");

        try {
            Relationship relationship = relationshipJsonReader.createObjectFromJson(errorJson);
            fail("Exception should be thrown, but it's not. Fail.");
        } catch(CodsResponseFormatException e) {
            logger.debug("Expected exception catched:" + e.getMessage());
        }
    }

    @Test
    public void testCreateObjectFromJson_UnknownType() throws Exception {
        logger.debug("RelationshipJsonReaderTest.testCreateObjectFromJson_UnknownType()");

        try {
            JSONObject rJson = getRelationshipJson("unknown");
            Relationship relationship = relationshipJsonReader.createObjectFromJson(rJson);
            fail("Exception should be thrown, but it's not. Fail.");
        } catch(CodsResponseFormatException e) {
            logger.debug("Expected exception catched:" + e.getMessage());
        }
    }

    @Test
    public void testCreateObjectFromJson_EmptyType() throws Exception {
        logger.debug("RelationshipJsonReaderTest.testCreateObjectFromJson_EmptyType()");

        JSONObject rJson = getRelationshipJson("");
        Relationship relationship = relationshipJsonReader.createObjectFromJson(rJson);
        assertNotNull(relationship);
        assertNull(relationship.getType());
    }

}
