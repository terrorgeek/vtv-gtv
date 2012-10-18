package com.miquido.vtv.codsservices.util;

import com.miquido.vtv.bo.Entity;
import com.miquido.vtv.codsservices.dataobjects.PageParams;
import com.miquido.vtv.codsservices.dataobjects.RelatedDataCollection;
import com.miquido.vtv.codsservices.dataobjects.Subcollection;
import com.miquido.vtv.codsservices.util.relationtranslation.RelatedDataCollectionTranslator;

import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: ljazgar
 * Date: 31.08.12
 * Time: 17:17
 * To change this template use File | Settings | File Templates.
 */
public class PageByPageLoader {

    private int pageSize = 100;

    public PageByPageLoader() {
    }

    public PageByPageLoader(int pageSize) {
        this.pageSize = pageSize;
    }


    public <EntryType> List<EntryType> getWholeCollection(SingleSubcollectionLoader<EntryType> singleSubcollectionLoader) {
        Subcollection<EntryType> subcollection = singleSubcollectionLoader.load(PageParams.createForPageNum(pageSize, 1));
        List<EntryType> collectedEntries = new ArrayList<EntryType>(subcollection.getTotalCount());
        collectedEntries.addAll(subcollection.getEntries());
        for (int pageNo=2; subcollection.getNextOffset()< subcollection.getTotalCount(); pageNo++) {
            subcollection = singleSubcollectionLoader.load( PageParams.createForPageNum(pageSize, pageNo));
            collectedEntries.addAll(subcollection.getEntries());
        }
        return collectedEntries;
    }

    public
    <DomainRelation extends Entity, EntryType extends Entity>
    List<DomainRelation> getWholeRelationCollection(
            SingleRelatedDataCollectionLoader<EntryType> singleCollectionLoader,
            RelatedDataCollectionTranslator<DomainRelation, EntryType> translator) {

        RelatedDataCollection<EntryType> subcollection = singleCollectionLoader.load(PageParams.createForPageNum(pageSize, 1));
        List<DomainRelation> collectedRelations = new ArrayList<DomainRelation>(subcollection.getTotalCount());
        collectedRelations.addAll( translator.translate(subcollection) );
        for (int pageNo=2; subcollection.getNextOffset()< subcollection.getTotalCount(); pageNo++) {
            subcollection = singleCollectionLoader.load( PageParams.createForPageNum(pageSize, pageNo));
            collectedRelations.addAll(translator.translate(subcollection));
        }
        return collectedRelations;
    }

    public static interface SingleSubcollectionLoader<E> {
        Subcollection<E> load(PageParams pageParams);
    }

    public static interface SingleRelatedDataCollectionLoader<E> {
        RelatedDataCollection<E> load(PageParams pageParams);
    }

}
