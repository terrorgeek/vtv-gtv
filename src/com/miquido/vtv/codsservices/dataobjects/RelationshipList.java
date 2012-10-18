package com.miquido.vtv.codsservices.dataobjects;

import com.miquido.vtv.bo.Id;
import lombok.ToString;

import java.util.HashMap;

/**
 * Created with IntelliJ IDEA.
 * User: ljazgar
 * Date: 31.08.12
 * Time: 10:58
 * To change this template use File | Settings | File Templates.
 */
@ToString
public class RelationshipList {

    HashMap<Id, Relationship> idToRelationshipMap = new HashMap<Id, Relationship>();


    public void put(Id relatedObjectId, Relationship relationship) {
        idToRelationshipMap.put(relatedObjectId, relationship);
    }

    public Relationship get(Id relatedObjectId) {
        return idToRelationshipMap.get(relatedObjectId);
    }

    public int size() {
        return idToRelationshipMap.size();
    }

}
