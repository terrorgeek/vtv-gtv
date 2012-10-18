package com.miquido.vtv.codsservices.util.relationtranslation;

import com.miquido.vtv.bo.Entity;
import com.miquido.vtv.codsservices.dataobjects.Relationship;

/**
 * Created with IntelliJ IDEA.
 * User: ljazgar
 * Date: 31.08.12
 * Time: 17:30
 * To change this template use File | Settings | File Templates.
 */
public interface RelationTranslator<DomainRelation extends Entity, RelationTarget extends Entity> {

    DomainRelation translate(RelationTarget relationTarget, Relationship relationship);
}
