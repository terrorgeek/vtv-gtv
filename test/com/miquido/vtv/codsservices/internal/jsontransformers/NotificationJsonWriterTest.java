package com.miquido.vtv.codsservices.internal.jsontransformers;

import com.miquido.vtv.bo.Id;
import com.miquido.vtv.bo.Notification;
import com.miquido.vtv.bo.Profile;
import com.miquido.vtv.bo.WatchWithMeNotification;
import com.miquido.vtv.codsservices.internal.impl.mocks.IdRandomizer;
import org.json.JSONObject;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Created with IntelliJ IDEA.
 * User: ljazgar
 * Date: 12.09.12
 * Time: 12:06
 * To change this template use File | Settings | File Templates.
 */
public class NotificationJsonWriterTest {

    private static final Logger logger = LoggerFactory.getLogger(NotificationJsonWriterTest.class);

    IdRandomizer idRandomizer = new IdRandomizer();
    NotificationJsonWriter notificationJsonWriter = new NotificationJsonWriter();


    @Test
    public void testCreateJsonFromObject() throws Exception {
        logger.debug("NotificationJsonWriterTest.testCreateJsonFromObject");

        WatchWithMeNotification notification = new WatchWithMeNotification();
        notification.setFromProfileId(idRandomizer.getNewId());
        notification.setToProfileId(idRandomizer.getNewId());
        notification.setChannelId(idRandomizer.getNewId());
        notification.setStatus(WatchWithMeNotification.Status.New);

        JSONObject jsonObject = notificationJsonWriter.createJsonFromObject(notification);
        logger.debug("JSON:\n" + jsonObject);
        assertEquals(notification.getFromProfileId().toString(), jsonObject.get("from_id"));
        assertEquals(notification.getToProfileId().toString(), jsonObject.get("to_id"));
        assertEquals(notification.getChannelId().toString(), jsonObject.get("subject_id"));
        assertEquals("new", jsonObject.get("status"));
        assertEquals("watchWithMe", jsonObject.get("type"));
    }

    @Test
    public void testCreateJsonFromObject2() throws Exception {
        logger.debug("NotificationJsonWriterTest.testCreateJsonFromObject2");

        WatchWithMeNotification notification = new WatchWithMeNotification();
        notification.setStatus(WatchWithMeNotification.Status.Accepted);

        JSONObject jsonObject = notificationJsonWriter.createJsonFromObject(notification);
        logger.debug("JSON:\n" + jsonObject);
        assertEquals("accepted", jsonObject.get("status"));
        assertEquals("watchWithMe", jsonObject.get("type"));
        assertEquals(2, jsonObject.length());
    }
}
