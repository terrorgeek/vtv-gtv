package com.miquido.vtv.codsservices.internal.jsontransformers;

import com.miquido.vtv.bo.Notification;
import com.miquido.vtv.bo.Profile;
import com.miquido.vtv.bo.WatchWithMeNotification;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created with IntelliJ IDEA.
 * User: ljazgar
 * Date: 07.09.12
 * Time: 19:06
 * To change this template use File | Settings | File Templates.
 */
public class NotificationJsonWriter extends BaseJsonWriter implements JsonWriter<Notification> {

    @Override
    public JSONObject createJsonFromObject(Notification notification) {
        return createJsonFromObject(notification, notification);
    }

    @Override
    public JSONObject createJsonFromObject(Notification notification, Notification dataToUpdateIndicator) {

        try {
            final JSONObject jsonObject = new JSONObject();

            if (hasValue(dataToUpdateIndicator.getId()))
                put(jsonObject, "id", notification.getId());
            if (hasValue(dataToUpdateIndicator.getType()))
                put(jsonObject, "type", notification.getType().toString());
            if (hasValue(dataToUpdateIndicator.getSubjectId()))
                put(jsonObject, "subject_id", notification.getSubjectId());
            if (hasValue(dataToUpdateIndicator.getFromProfileId()))
                put(jsonObject, "from_id", notification.getFromProfileId());
            if (hasValue(dataToUpdateIndicator.getFromProfileId()))
                put(jsonObject, "to_id", notification.getToProfileId());
            if (notification instanceof WatchWithMeNotification && dataToUpdateIndicator instanceof WatchWithMeNotification) {
                WatchWithMeNotification watchWithMeNotification = (WatchWithMeNotification)notification;
                WatchWithMeNotification wWMDateToUpdateIndicator = (WatchWithMeNotification)dataToUpdateIndicator;
                if (hasValue(wWMDateToUpdateIndicator.getStatus()))
                    put(jsonObject, "status", watchWithMeNotification.getStatus().toString());
            }

            return jsonObject;
        } catch (JSONException e) {
            throw new RuntimeException("Error while creating JsonObject from LoginInput.", e);
        }
    }

}
