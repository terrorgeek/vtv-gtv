package com.miquido.vtv.codsservices.internal.jsontransformers;

import com.miquido.vtv.bo.Channel;
import com.miquido.vtv.bo.Id;
import com.miquido.vtv.bo.Profile;
import com.miquido.vtv.codsservices.exceptions.CodsResponseFormatException;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;

/**
 * Created with IntelliJ IDEA.
 * User: ljazgar
 * Date: 29.07.12
 * Time: 11:13
 * To change this template use File | Settings | File Templates.
 */

public class ChannelJsonReader extends BaseJsonReader<Channel> {

    @Override
    protected Class<Channel> getType() {
        return Channel.class;
    }

    public boolean isCorrectType(JSONObject jsonObject) {
        return (jsonObject.has("id") && jsonObject.has("name"));
    }

    /**
     *
     * @return
     */
    public Channel createObjectFromJson(JSONObject jsonObject) {

        Channel channel = new Channel();

        try {
            channel.setId(Id.severeValueOf(jsonObject.getString("id")));
            channel.setDateModified(getDate(jsonObject, "date_modified"));
            channel.setDateEntered(getDate(jsonObject, "date_entered"));
            channel.setName(jsonObject.getString("name"));
            channel.setLogoId(Id.valueOf(jsonObject.optString("logo_id")));
            channel.setCallSign( jsonObject.optString("callsign"));

            return channel;
        } catch (JSONException e) {
            throw new CodsResponseFormatException("Incorrect structure of Channel in JSON formatted response.", e);
        } catch (ParseException e) {
            throw new CodsResponseFormatException("Incorrect format of ID in channel structure.", e);
        }
    }

}
