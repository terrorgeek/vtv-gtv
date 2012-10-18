package com.miquido.vtv.codsservices.internal.jsontransformers;

import com.miquido.vtv.bo.Id;
import com.miquido.vtv.bo.Profile;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created with IntelliJ IDEA.
 * User: ljazgar
 * Date: 07.09.12
 * Time: 19:06
 * To change this template use File | Settings | File Templates.
 */
public class ProfileJsonWriter extends BaseJsonWriter implements JsonWriter<Profile> {

//    private JsonObjectMapping jsonObjectMapping;
//    public ProfileJsonWriter() {
//        jsonObjectMapping = JsonObjectMapping.newInstance();
//        jsonObjectMapping
//                .add("id",            "id",           true, JsonAttributeMapping.MappingDirection.ONLY_FROM_JSON)
//                .add("date_modified", "dateModified", true, JsonAttributeMapping.MappingDirection.ONLY_FROM_JSON)
//                .add("date_entered",  "dateEntered",  true, JsonAttributeMapping.MappingDirection.ONLY_FROM_JSON)
//                .add("name",          "name")
//                .add("description",   "description")
//                .add("first_name",    "firstName")
//                .add("last_name",     "lastName")
//                .add("facebook_id",   "facebookId")
//                .add("avatar_id",     "avatarId")
//                .add("current_channel_id", "currentChannelId");
//    }


    @Override
    public JSONObject createJsonFromObject(Profile profile) {
        return createJsonFromObject(profile, profile);
    }

    @Override
    public JSONObject createJsonFromObject(Profile profile, Profile dataToUpdateIndicator) {

        try {
            final JSONObject jsonObject = new JSONObject();

            if (hasValue(dataToUpdateIndicator.getId()))
                put(jsonObject, "id", profile.getId());
            if (hasValue(dataToUpdateIndicator.getName()))
                put(jsonObject, "name", profile.getName());
            if (hasValue(dataToUpdateIndicator.getDescription()))
                put(jsonObject, "description", profile.getDescription());
            if (hasValue(dataToUpdateIndicator.getFirstName()))
                put(jsonObject, "first_name", profile.getFirstName());
            if (hasValue(dataToUpdateIndicator.getLastName()))
                put(jsonObject, "last_name", profile.getLastName());
            if (hasValue(dataToUpdateIndicator.getFacebookId()))
                put(jsonObject, "facebook_id", profile.getFacebookId());
            if (hasValue(dataToUpdateIndicator.getAvatarId()))
                put(jsonObject, "avatar_id", profile.getAvatarId());
            if (hasValue(dataToUpdateIndicator.isOnline()))
                put(jsonObject, "sip_status", (profile.isOnline())?"online":"offline");
            if (hasValue(dataToUpdateIndicator.getCurrentChannelId()))
                put(jsonObject, "current_channel_id", profile.getCurrentChannelId());
            if (hasValue(dataToUpdateIndicator.getRequestedChannelId()))
                put(jsonObject, "requested_channel_id", profile.getRequestedChannelId());


            return jsonObject;
        } catch (JSONException e) {
            throw new RuntimeException("Error while creating JsonObject from LoginInput.", e);
        }
    }

}
