package com.miquido.vtv.codsservices.internal.jsontransformers;

import com.miquido.vtv.bo.GuideEntry;
import com.miquido.vtv.bo.Id;
import com.miquido.vtv.bo.ScheduleEntry;
import com.miquido.vtv.codsservices.exceptions.CodsResponseFormatException;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;

public class GuideEntryJsonReader extends BaseJsonReader<GuideEntry> {

  @Override
  protected Class<GuideEntry> getType() {
    return GuideEntry.class;
  }

  public boolean isCorrectType(JSONObject jsonObject) {
    return jsonObject.has("id");
  }

  public GuideEntry createObjectFromJson(JSONObject jsonObject) {

    GuideEntry guideEntry = new GuideEntry();

    try {
      guideEntry.setId(Id.severeValueOf(jsonObject.getString("id")));
      guideEntry.setDateModified(getDate(jsonObject, "date_modified"));
      guideEntry.setDateEntered(getDate(jsonObject, "date_entered"));
      guideEntry.setName(jsonObject.optString("name"));
      guideEntry.setDescription(jsonObject.optString("description"));
      guideEntry.setStartTime(getDate(jsonObject, "start_time"));
      guideEntry.setEndTime(getDate(jsonObject, "end_time"));
      guideEntry.setChannelId(Id.valueOf(jsonObject.getString("channel_id")));
      guideEntry.setProgramId(Id.valueOf(jsonObject.getString("program_id")));
      return guideEntry;
    } catch (JSONException e) {
      throw new CodsResponseFormatException("Incorrect structure of Profile in JSON formatted response.", e);
    } catch (ParseException e) {
      throw new CodsResponseFormatException("Incorrect format of ID in profile structure.", e);
    }
  }

}
