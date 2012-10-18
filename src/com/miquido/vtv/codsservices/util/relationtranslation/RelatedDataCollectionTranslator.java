package com.miquido.vtv.codsservices.util.relationtranslation;

import com.miquido.vtv.bo.Entity;
import com.miquido.vtv.codsservices.dataobjects.RelatedDataCollection;
import com.miquido.vtv.codsservices.dataobjects.Relationship;
import com.miquido.vtv.codsservices.dataobjects.RelationshipList;
import com.miquido.vtv.codsservices.util.relationtranslation.RelationTranslator;

import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: ljazgar
 * Date: 31.08.12
 * Time: 17:52
 * To change this template use File | Settings | File Templates.
 */
public class RelatedDataCollectionTranslator<DomainRelation extends Entity, RelationTarget extends Entity> {

    private final RelationTranslator<DomainRelation, RelationTarget> relationTranslator;

    public RelatedDataCollectionTranslator(RelationTranslator<DomainRelation, RelationTarget> relationTranslator) {
        this.relationTranslator = relationTranslator;
    }

    public List<DomainRelation> translate(RelatedDataCollection<RelationTarget> relationDataCollection) {
        List<DomainRelation> domainRelations = new ArrayList<DomainRelation>();
        RelationshipList relationshipList = relationDataCollection.getRelationshipList();
        for( RelationTarget target : relationDataCollection.getEntries() ) {
            Relationship relationship = relationshipList.get(target.getId());
            if (relationship!=null) {
                DomainRelation domainRelation = relationTranslator.translate(target, relationship);
                domainRelations.add(domainRelation);
            }
        }
        return domainRelations;
    }

}
