package com.miquido.vtv.codsservices.internal.jsontransformers;

import com.google.inject.Inject;
import com.miquido.vtv.codsservices.dataobjects.RelatedDataCollection;
import com.miquido.vtv.codsservices.dataobjects.RelationshipList;
import com.miquido.vtv.codsservices.dataobjects.Subcollection;
import com.miquido.vtv.codsservices.exceptions.CodsResponseFormatException;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created with IntelliJ IDEA.
 * User: ljazgar
 * Date: 31.08.12
 * Time: 13:52
 * To change this template use File | Settings | File Templates.
 */
public class RelatedDataCollectionJsonReader<EntryType> implements JsonReader<RelatedDataCollection>{

    private final RelationshipListJsonReader relationshipListJsonReader;
    private final SubcollectionJsonReader<EntryType> subcollectionJsonReader;

    @Inject
    public RelatedDataCollectionJsonReader( SubcollectionJsonReader<EntryType> subcollectionJsonReader,
                                            RelationshipListJsonReader relationshipListJsonReader) {
        this.subcollectionJsonReader = subcollectionJsonReader;
        this.relationshipListJsonReader = relationshipListJsonReader;
    }

    @Override
    public boolean isCorrectType(JSONObject jsonObject) {
        if (!subcollectionJsonReader.isCorrectType(jsonObject))
            return false;

        try {
            int resultCount = jsonObject.getInt("result_count");
            JSONObject relationshipListJson = jsonObject.optJSONObject("relationship_list");
            // LJ: Mandatory attribute relationship_list (according to documentation) sometimes doesnt exist.
            return (resultCount==0 || (relationshipListJson!=null && relationshipListJsonReader.isCorrectType(relationshipListJson)));
        } catch (JSONException e) {
            return false;
        }
    }

    @Override
    public RelatedDataCollection<EntryType> createObjectFromJson(JSONObject jsonObject) {
        RelatedDataCollection<EntryType> relatedDataCollection = new RelatedDataCollection<EntryType>();
        subcollectionJsonReader.readSubcollectionData(jsonObject, relatedDataCollection);

        if (relatedDataCollection.getResultCount()==0)
            return relatedDataCollection;

        try {
            JSONObject relationshipListJson = jsonObject.getJSONObject("relationship_list");
            RelationshipList relationshipList = relationshipListJsonReader.createObjectFromJson(relationshipListJson);
            relatedDataCollection.setRelationshipList(relationshipList);
        } catch (JSONException e) {
            throw new CodsResponseFormatException("Incorrect structure of Related data collection JSON formatted response.", e);
        }
        return relatedDataCollection;
    }
}
