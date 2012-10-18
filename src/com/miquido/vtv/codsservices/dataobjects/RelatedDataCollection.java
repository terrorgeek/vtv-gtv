package com.miquido.vtv.codsservices.dataobjects;

import lombok.Data;
import lombok.ToString;

/**
 * Created with IntelliJ IDEA.
 * User: ljazgar
 * Date: 31.08.12
 * Time: 10:53
 * To change this template use File | Settings | File Templates.
 */
@Data @ToString(callSuper = true)
public class RelatedDataCollection<EntryType> extends Subcollection<EntryType> {

    RelationshipList relationshipList = new RelationshipList();

}
