package com.miquido.vtv.codsservices.internal.jsontransformers;

import com.miquido.vtv.bo.Id;
import com.miquido.vtv.bo.Profile;
import com.miquido.vtv.codsservices.dataobjects.Relationship;
import com.miquido.vtv.codsservices.exceptions.CodsResponseFormatException;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;

/**
 * Created with IntelliJ IDEA.
 * User: ljazgar
 * Date: 31.08.12
 * Time: 11:38
 * To change this template use File | Settings | File Templates.
 */
public class RelationshipJsonReader extends BaseJsonReader<Relationship> {

    @Override
    protected Class<Relationship> getType() {
        return Relationship.class;
    }

    @Override
    public boolean isCorrectType(JSONObject jsonObject) {
        return (jsonObject.has("id") && jsonObject.has("entry_id") && jsonObject.has("related_id") && jsonObject.has("type"));
    }

    @Override
    public Relationship createObjectFromJson(JSONObject jsonObject) {
        Relationship relationship = new Relationship();

        try {
            relationship.setId(Id.severeValueOf(jsonObject.getString("id")));
            relationship.setDateModified(optDate(jsonObject, "date_modified")); // Should be mandatory, but API sometimes returns empty value
            relationship.setDateEntered(optDate(jsonObject, "date_entered"));   // Should be mandatory, but API sometimes returns empty value
            relationship.setDescription(jsonObject.optString("description"));
            relationship.setEntryId(Id.severeValueOf(jsonObject.getString("entry_id")));
            relationship.setRelatedEntryId(Id.severeValueOf(jsonObject.getString("related_id")));
            relationship.setStatus(jsonObject.optString("status"));

            String typeString = jsonObject.optString("type");
            if (typeString!=null && !typeString.isEmpty()) {
                Relationship.Type type = Relationship.Type.forName( typeString );
                if (type==null) {
                    throw new CodsResponseFormatException("Unknown type of Relationship: "+typeString);
                }
                relationship.setType(type);
            }

            return relationship;
        } catch (JSONException e) {
            throw new CodsResponseFormatException("Incorrect structure of Profile in JSON formatted response.", e);
        } catch (ParseException e) {
            throw new CodsResponseFormatException("Incorrect format of ID in Relationship structure.", e);
        }
    }


}
