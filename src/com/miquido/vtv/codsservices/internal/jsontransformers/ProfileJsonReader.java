package com.miquido.vtv.codsservices.internal.jsontransformers;

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

public class ProfileJsonReader implements JsonReader<Profile> {

    private final static DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");


    public boolean isCorrectType(JSONObject jsonObject) {
        return (jsonObject.has("id") && jsonObject.has("name") && jsonObject.has("full_name"));
    }

    /**
     *
     * @return
     */
    public Profile createObjectFromJson(JSONObject jsonObject) {

        Profile profile = new Profile();

        try {
            profile.setId( jsonObject.getString("id"));
            profile.setDateModified(dateFormat.parse(jsonObject.getString("date_modified")));
            profile.setName(jsonObject.getString("name"));
            profile.setDescription(jsonObject.getString("description"));
            profile.setFirstName(jsonObject.getString("first_name"));
            profile.setLastName(jsonObject.getString("last_name"));
            profile.setFullName(jsonObject.getString("full_name"));
            profile.setEmail(jsonObject.getString("email"));
            profile.setAvatarId(jsonObject.getString("avatar_id"));
            profile.setAvatarURL(jsonObject.optString("avatar_url"));
            profile.setFacebookId(jsonObject.getString("facebook_id"));

            return profile;
        } catch (JSONException e) {
            throw new CodsResponseFormatException("Incorrect structure of Profile in JSON formatted response.", e);
        } catch (ParseException e) {
            throw new CodsResponseFormatException("Incorrect format of data in field date_modified.", e);
        }
    }

}
