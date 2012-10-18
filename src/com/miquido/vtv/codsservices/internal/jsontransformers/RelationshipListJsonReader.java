package com.miquido.vtv.codsservices.internal.jsontransformers;

import com.google.inject.Inject;
import com.miquido.vtv.bo.Id;
import com.miquido.vtv.codsservices.dataobjects.Relationship;
import com.miquido.vtv.codsservices.dataobjects.RelationshipList;
import com.miquido.vtv.codsservices.exceptions.CodsResponseFormatException;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Iterator;

/**
 * Created with IntelliJ IDEA.
 * User: ljazgar
 * Date: 31.08.12
 * Time: 12:31
 * To change this template use File | Settings | File Templates.
 */
public class RelationshipListJsonReader implements JsonReader<RelationshipList> {

    private final RelationshipJsonReader relationshipJsonReader;

    @Inject
    public RelationshipListJsonReader(RelationshipJsonReader relationshipJsonReader) {
        this.relationshipJsonReader = relationshipJsonReader;
    }

    @Override
    public boolean isCorrectType(JSONObject jsonObject) {
        if (jsonObject.length()==0)
            return true;

        String firstKey = (String)jsonObject.keys().next();
        if (Id.valueOf(firstKey)==null)
            return false;
        JSONObject firstValue = jsonObject.optJSONObject(firstKey);
        return (relationshipJsonReader.isCorrectType(firstValue));
    }

    @Override
    public RelationshipList createObjectFromJson(JSONObject jsonObject) {

        RelationshipList relationshipList = new RelationshipList();

        Iterator<String> keysIt = jsonObject.keys();
        while (keysIt.hasNext()) {
            Id relatedObjectId = Id.valueOf(keysIt.next());
            if (relatedObjectId==null)
                throw new CodsResponseFormatException("Key in Relationship List map is not Id.");

            JSONObject valueJsonObject = null;
            try {
                valueJsonObject = jsonObject.getJSONObject(relatedObjectId.toString());
            } catch (JSONException e) {
                throw new CodsResponseFormatException("Incorrect structure of Relationship List. Map value is not an json object.", e);
            }
            Relationship relationship = relationshipJsonReader.createObjectFromJson(valueJsonObject);

            relationshipList.put(relatedObjectId, relationship);
        }

        return relationshipList;
    }

}
