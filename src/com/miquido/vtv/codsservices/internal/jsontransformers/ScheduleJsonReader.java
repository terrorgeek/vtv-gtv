package com.miquido.vtv.codsservices.internal.jsontransformers;

import com.miquido.vtv.bo.Id;
import com.miquido.vtv.bo.ScheduleEntry;
import com.miquido.vtv.codsservices.exceptions.CodsResponseFormatException;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;

public class ScheduleJsonReader extends BaseJsonReader<ScheduleEntry> {

  @Override
  protected Class<ScheduleEntry> getType() {
    return ScheduleEntry.class;
  }

  public boolean isCorrectType(JSONObject jsonObject) {
    return jsonObject.has("id");
  }

  public ScheduleEntry createObjectFromJson(JSONObject jsonObject) {

    ScheduleEntry scheduleEntry = new ScheduleEntry();

    try {
      scheduleEntry.setId(Id.severeValueOf(jsonObject.getString("id")));
      scheduleEntry.setDateModified(getDate(jsonObject, "date_modified"));
      scheduleEntry.setDateEntered(getDate(jsonObject, "date_entered"));
      scheduleEntry.setGuideId(Id.valueOf(jsonObject.getString("guide_id")));
      return scheduleEntry;
    } catch (JSONException e) {
      throw new CodsResponseFormatException("Incorrect structure of Profile in JSON formatted response.", e);
    } catch (ParseException e) {
      throw new CodsResponseFormatException("Incorrect format of ID in profile structure.", e);
    }
  }

}
