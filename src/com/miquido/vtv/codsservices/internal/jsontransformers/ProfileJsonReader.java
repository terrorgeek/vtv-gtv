package com.miquido.vtv.codsservices.internal.jsontransformers;

import com.miquido.vtv.bo.Id;
import com.miquido.vtv.bo.Profile;
import com.miquido.vtv.codsservices.exceptions.CodsResponseFormatException;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;

/**
 * Created with IntelliJ IDEA.
 * User: ljazgar
 * Date: 29.07.12
 * Time: 11:13
 * To change this template use File | Settings | File Templates.
 */

public class ProfileJsonReader extends BaseJsonReader<Profile> {

    @Override
    protected Class<Profile> getType() {
        return Profile.class;
    }

    public boolean isCorrectType(JSONObject jsonObject) {
        return (jsonObject.has("id") && jsonObject.has("first_name") && jsonObject.has("last_name") && jsonObject.has("facebook_id"));
    }

    /**
     *
     * @return
     */
    public Profile createObjectFromJson(JSONObject jsonObject) {

        Profile profile = new Profile();

        try {
            profile.setId(Id.severeValueOf(jsonObject.getString("id")));
            profile.setDateModified(getDate(jsonObject, "date_modified"));
            profile.setDateEntered(getDate(jsonObject, "date_entered"));
            profile.setName(jsonObject.optString("name"));
            profile.setDescription(jsonObject.optString("description"));
            profile.setFirstName(jsonObject.getString("first_name"));
            profile.setLastName(jsonObject.getString("last_name"));
            profile.setFacebookId(jsonObject.optString("facebook_id"));
            profile.setAvatarId(Id.valueOf(jsonObject.optString("avatar_id")));
            profile.setOnline("online".equals(jsonObject.optString("sip_status")));
            profile.setCurrentChannelId(Id.valueOf(jsonObject.optString("current_channel_id")));
            profile.setRequestedChannelId(Id.valueOf(jsonObject.optString("requested_channel_id")));

            return profile;
        } catch (JSONException e) {
            throw new CodsResponseFormatException("Incorrect structure of Profile in JSON formatted response.", e);
        } catch (ParseException e) {
            throw new CodsResponseFormatException("Incorrect format of ID in profile structure.", e);
        }
    }

}
