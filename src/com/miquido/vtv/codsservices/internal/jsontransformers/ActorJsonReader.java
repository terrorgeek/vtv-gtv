/**
 * Created by raho on 10/10/12.
 *
 * Copyright 2012 MiQUiDO <http://www.miquido.com/>. All rights reserved.
 */
package com.miquido.vtv.codsservices.internal.jsontransformers;

import com.miquido.vtv.bo.Actor;
import com.miquido.vtv.bo.Id;
import com.miquido.vtv.codsservices.exceptions.CodsResponseFormatException;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;

public class ActorJsonReader extends BaseJsonReader<Actor> {
  @Override
  protected Class<Actor> getType() {
    return Actor.class;
  }

  @Override
  public boolean isCorrectType(JSONObject jsonObject) {
    return true;
  }

  @Override
  public Actor createObjectFromJson(JSONObject jsonObject) {
    Actor result = new Actor();
    try {
      result.setId(Id.severeValueOf(jsonObject.getString("id")));
      result.setName(jsonObject.getString("name"));
      result.setPhotoId(Id.severeValueOf(jsonObject.getString("photo_id")));
      result.setDescription(jsonObject.getString("description"));
      result.setNickname(jsonObject.getString("nickname"));
      result.setBio(jsonObject.getString("bio"));
    } catch (JSONException e) {
      throw new CodsResponseFormatException("Incorrect structure of Actor in JSON formatted response.", e);
    } catch (ParseException e) {
      throw new CodsResponseFormatException("Incorrect format of ID in actor structure.", e);
    }
    return result;
  }
}
