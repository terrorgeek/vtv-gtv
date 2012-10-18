package com.miquido.vtv.codsservices.internal.jsontransformers;

import com.miquido.vtv.bo.Id;
import com.miquido.vtv.bo.Notification;
import com.miquido.vtv.bo.ReminderNotification;
import com.miquido.vtv.bo.WatchWithMeNotification;
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

public class NotificationJsonReader extends BaseJsonReader<Notification> {

  @Override
  protected Class<Notification> getType() {
    return Notification.class;
  }

  public boolean isCorrectType(JSONObject jsonObject) {
    return (jsonObject.has("id") && jsonObject.has("type") && jsonObject.has("from_id") && jsonObject.has("to_id")
        && jsonObject.has("status"));
  }

  /**
   * @return
   */
  public Notification createObjectFromJson(JSONObject jsonObject) {

    Notification notification = null;
    String typeString = null;
    try {
      typeString = jsonObject.getString("type");
      Notification.Type type = Notification.Type.valueOf(typeString);
      String statusString = jsonObject.optString("status");
      if (type == Notification.Type.watchWithMe) {
        WatchWithMeNotification watchWithMeNotification = new WatchWithMeNotification();
        setStatus(watchWithMeNotification, statusString);
        notification = watchWithMeNotification;
      } /*else if (type == Notification.Type.reminder) {
        ReminderNotification reminderNotification = new ReminderNotification();
        setStatus(reminderNotification, statusString);
        notification = reminderNotification;
      }*/ else {
        throw new CodsResponseFormatException("Unknown type of Notification:" + type);
      }

      notification.setId(Id.severeValueOf(jsonObject.getString("id")));
      notification.setDateModified(getDate(jsonObject, "date_modified"));
      notification.setDateEntered(getDate(jsonObject, "date_entered"));
      notification.setSubjectId(Id.valueOf(jsonObject.optString("subject_id")));
      notification.setFromProfileId(Id.valueOf(jsonObject.getString("from_id")));
      notification.setToProfileId(Id.valueOf(jsonObject.getString("to_id")));

      return notification;
    } catch (IllegalArgumentException e) {
      throw new CodsResponseFormatException("Unknown type of Notification:" + typeString, e);
    } catch (JSONException e) {
      throw new CodsResponseFormatException("Incorrect structure of Notification in JSON formatted response.", e);
    } catch (ParseException e) {
      throw new CodsResponseFormatException("Incorrect format of ID in notification structure.", e);
    }
  }

  private void setStatus(WatchWithMeNotification watchWithMeNotification, String statusString) {
    WatchWithMeNotification.Status status = WatchWithMeNotification.Status.forName(statusString);
    if (status == null)
      throw new CodsResponseFormatException("Incorrect status of WatchWithMeNotification:" + statusString);
    watchWithMeNotification.setStatus(status);
  }

  private void setStatus(ReminderNotification reminderNotification, String statusString) {
    ReminderNotification.Status status = ReminderNotification.Status.forName(statusString);
    if (status == null)
      throw new CodsResponseFormatException("Incorrect status of WatchWithMeNotification:" + statusString);
    reminderNotification.setStatus(status);
  }

}
