package com.miquido.vtv.codsservices.internal.jsontransformers;

import com.google.inject.Inject;
import com.miquido.vtv.codsservices.dataobjects.PageList;
import com.miquido.vtv.codsservices.exceptions.CodsResponseFormatException;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: ljazgar
 * Date: 29.07.12
 * Time: 19:00
 * To change this template use File | Settings | File Templates.
 */
public class PageListJsonReader<EntryType> implements JsonReader<PageList<EntryType>> {

    private final JsonReader<EntryType> entryJsonReader;

    @Inject
    public PageListJsonReader(JsonReader<EntryType> entryJsonReader) {
        this.entryJsonReader = entryJsonReader;
    }

    public boolean isCorrectType(JSONObject jsonObject) {
        if (jsonObject.has("result_count") && jsonObject.has("total_count") && jsonObject.has("next_offset")
                && jsonObject.has("entry_list")) {
            JSONArray entryList = jsonObject.optJSONArray("entry_list");
            if (entryList==null)
                return false;

            if (entryList.length()==0)
                return true;

            JSONObject entry = entryList.optJSONObject(0);
            if (entry==null)
                return false;

            return entryJsonReader.isCorrectType(entry);

        } else
            return false;
    }


    public PageList<EntryType> createObjectFromJson(JSONObject jsonObject) {
        PageList<EntryType> pageList = new PageList<EntryType>();
        try {
            pageList.setResultCount( jsonObject.getInt("result_count") );
            pageList.setTotalCount(jsonObject.getInt("total_count"));
            pageList.setNextOffset(jsonObject.getInt("next_offset"));

            JSONArray jsonEntryList = jsonObject.optJSONArray("entry_list");
            List<EntryType> entryList = new ArrayList<EntryType>(jsonEntryList.length());
            for ( int i=0 ; i<jsonEntryList.length(); i++ ) {
                JSONObject jsonEntry = jsonEntryList.getJSONObject(i);
                EntryType entry = entryJsonReader.createObjectFromJson(jsonEntry);
                entryList.add(entry);
            }
            pageList.setEntries(entryList);

            return pageList;
        } catch (JSONException e) {
            throw new CodsResponseFormatException("Incorrect structure of list of profiles in JSON formatted response.", e);
        }
    }

}