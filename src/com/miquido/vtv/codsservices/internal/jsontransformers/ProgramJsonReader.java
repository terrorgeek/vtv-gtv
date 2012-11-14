/**
 * Created by raho on 10/9/12.
 *
 * Copyright 2012 MiQUiDO <http://www.miquido.com/>. All rights reserved.
 */
package com.miquido.vtv.codsservices.internal.jsontransformers;

import com.miquido.vtv.bo.Id;
import com.miquido.vtv.bo.Program;
import com.miquido.vtv.codsservices.exceptions.CodsResponseFormatException;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;

public class ProgramJsonReader extends BaseJsonReader<Program> {
  @Override
  protected Class<Program> getType() {
    return Program.class;
  }

  @Override
  public boolean isCorrectType(JSONObject jsonObject) {
    return true;
  }

  @Override
  public Program createObjectFromJson(JSONObject jsonObject) {
    Program result = new Program();
    try {
      result.setId(Id.severeValueOf(jsonObject.getString("id")));
      result.setName(jsonObject.getString("name"));
      result.setDescription(jsonObject.getString("description"));
      result.setTagline(jsonObject.optString("tagline"));
      result.setEpisodeName(jsonObject.optString("episode_name"));
      result.setSeasonNo(jsonObject.optString("season_number"));
      result.setEpisodeNo(jsonObject.optString("episode_number"));
    } catch (JSONException e) {
      throw new CodsResponseFormatException("Incorrect structure of Program in JSON formatted response.", e);
    } catch (ParseException e) {
      throw new CodsResponseFormatException("Incorrect format of ID in program structure.", e);
    }
    return result;
  }
}
