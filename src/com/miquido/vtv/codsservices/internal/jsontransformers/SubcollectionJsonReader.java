package com.miquido.vtv.codsservices.internal.jsontransformers;

import com.google.inject.Inject;
import com.miquido.vtv.codsservices.dataobjects.Subcollection;
import com.miquido.vtv.codsservices.exceptions.CodsResponseFormatException;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: ljazgar
 * Date: 29.07.12
 * Time: 19:00
 * To change this template use File | Settings | File Templates.
 */
public class SubcollectionJsonReader<EntryType> implements JsonReader<Subcollection<EntryType>> {

  private static final Logger log = LoggerFactory.getLogger(SubcollectionJsonReader.class);

  protected final JsonReader<EntryType> entryJsonReader;

  @Inject
  public SubcollectionJsonReader(JsonReader<EntryType> entryJsonReader) {
    this.entryJsonReader = entryJsonReader;
  }

  public boolean isCorrectType(JSONObject jsonObject) {
    if (jsonObject.has("result_count") && jsonObject.has("total_count") && jsonObject.has("next_offset")
        && jsonObject.has("entry_list")) {
      JSONArray entryList = jsonObject.optJSONArray("entry_list");
      if (entryList == null)
        return false;

      if (entryList.length() == 0)
        return true;

      JSONObject entry = entryList.optJSONObject(0);
      if (entry == null)
        return false;

      return entryJsonReader.isCorrectType(entry);

    } else
      return false;
  }

  public Subcollection<EntryType> createObjectFromJson(JSONObject jsonObject) {
    Subcollection<EntryType> subcollection = new Subcollection<EntryType>();
    readSubcollectionData(jsonObject, subcollection);
    return subcollection;
  }

  protected void readSubcollectionData(JSONObject jsonObject, Subcollection<EntryType> subcollectionToFill) {
    try {
      subcollectionToFill.setResultCount(jsonObject.getInt("result_count"));
      subcollectionToFill.setTotalCount(jsonObject.getInt("total_count"));
      subcollectionToFill.setNextOffset(jsonObject.getInt("next_offset"));

      JSONArray jsonEntryList = jsonObject.optJSONArray("entry_list");
      List<EntryType> entryList = new ArrayList<EntryType>(jsonEntryList.length());
      for (int i = 0; i < jsonEntryList.length(); i++) {
        JSONObject jsonEntry = jsonEntryList.getJSONObject(i);
        try {
          EntryType entry = entryJsonReader.createObjectFromJson(jsonEntry);
          entryList.add(entry);
        } catch (Exception e) {
          log.warn("Skipping invalid collection entry due to exception: {}", e.getMessage());
        }
      }
      subcollectionToFill.setEntries(entryList);
    } catch (JSONException e) {
      throw new CodsResponseFormatException("Incorrect structure of list of profiles in JSON formatted response.", e);
    }
  }

}