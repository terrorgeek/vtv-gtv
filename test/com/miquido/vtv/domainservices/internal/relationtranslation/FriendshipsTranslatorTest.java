package com.miquido.vtv.domainservices.internal.relationtranslation;

import com.google.inject.Inject;
import com.google.inject.Module;
import com.miquido.test.guice.GuiceTest;
import com.miquido.vtv.bo.Friendship;
import com.miquido.vtv.bo.Profile;
import com.miquido.vtv.codsservices.dataobjects.RelatedDataCollection;
import com.miquido.vtv.codsservices.internal.jsontransformers.JsonTransformersModule;
import com.miquido.vtv.codsservices.internal.jsontransformers.RelatedDataCollectionJsonReader;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 * Created with IntelliJ IDEA.
 * User: ljazgar
 * Date: 31.08.12
 * Time: 18:16
 * To change this template use File | Settings | File Templates.
 */
public class FriendshipsTranslatorTest extends GuiceTest {

    private static final Logger logger = LoggerFactory.getLogger(FriendshipsTranslatorTest.class);

    @Override
    protected Iterable<? extends Module> getModules() {
        return Arrays.asList(new JsonTransformersModule(), new RelationTranslationModule());
    }

    @Inject
    private FriendshipsTranslator friendshipsTranslator;
    @Inject
    private RelatedDataCollectionJsonReader<Profile> relatedDataCollectionJsonReader;

    private RelatedDataCollection<Profile> profileRelatedDataCollection;

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
        JSONObject relatedDataCollectionJson = (JSONObject) new JSONTokener(relatedDataCollectionJsonString).nextValue();
        profileRelatedDataCollection = relatedDataCollectionJsonReader.createObjectFromJson(relatedDataCollectionJson);
    }



    @Test
    public void test() throws Exception {
        logger.debug("FriendshipsTranslatorTest.test()");

        List<Friendship> friendships = friendshipsTranslator.translate(profileRelatedDataCollection);
        assertNotNull(friendships);
        logger.debug("Friendships:" + friendships);
        assertEquals(2, friendships.size());

        Friendship friendship1 = friendships.get(0);
        assertEquals("69113744-d096-fc00-0cf2-503625b1d938", friendship1.getId().toString());
        assertEquals("12bb9aa9-fbdd-1e0a-c53a-4fff6358f941", friendship1.getProfileId().toString());
        assertEquals("938690c3-dd06-0282-ec57-50193b78f3be", friendship1.getFriendProfileId().toString());
        assertEquals(Friendship.Status.Accepted, friendship1.getStatus());
        assertEquals("Added as test friend", friendship1.getDescription());
        assertNotNull(friendship1.getFriend());
        assertEquals("938690c3-dd06-0282-ec57-50193b78f3be", friendship1.getFriend().getId().toString());
        assertEquals("Fan", friendship1.getFriend().getFirstName());

        Friendship friendship2 = friendships.get(1);
        assertEquals("7c63688c-a4c7-76f4-127b-503e7181c9e7", friendship2.getId().toString());
        assertEquals("12bb9aa9-fbdd-1e0a-c53a-4fff6358f941", friendship2.getProfileId().toString());
        assertEquals("65672ac9-70a9-c149-cce8-4fff63ea9803", friendship2.getFriendProfileId().toString());
        assertEquals(Friendship.Status.New, friendship2.getStatus());
        assertNotNull(friendship2.getFriend());
        assertEquals("65672ac9-70a9-c149-cce8-4fff63ea9803", friendship2.getFriend().getId().toString());
    }

}
