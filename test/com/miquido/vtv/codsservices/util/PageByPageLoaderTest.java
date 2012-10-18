package com.miquido.vtv.codsservices.util;

import com.miquido.vtv.bo.Entity;
import com.miquido.vtv.bo.Id;
import com.miquido.vtv.codsservices.dataobjects.PageParams;
import com.miquido.vtv.codsservices.dataobjects.RelatedDataCollection;
import com.miquido.vtv.codsservices.dataobjects.Relationship;
import com.miquido.vtv.codsservices.dataobjects.Subcollection;
import com.miquido.vtv.codsservices.util.relationtranslation.RelatedDataCollectionTranslator;
import com.miquido.vtv.codsservices.util.relationtranslation.RelationTranslator;
import lombok.Data;
import lombok.ToString;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.ParseException;
import java.util.List;

import static org.junit.Assert.*;
import static org.junit.Assert.assertEquals;

/**
 * Created with IntelliJ IDEA.
 * User: ljazgar
 * Date: 31.08.12
 * Time: 19:02
 * To change this template use File | Settings | File Templates.
 */
public class PageByPageLoaderTest {
    private static final Logger logger = LoggerFactory.getLogger(PageByPageLoaderTest.class);

    @Data
    class Entry extends Entity {
        public String name;
        Entry(Id id, String name) {
            setId( id );
            this.name = name;
        }
    }

    @Data @ToString(callSuper = true)
    class LikeRelation extends Entity {
        public Entry entry;
        LikeRelation(Id id, Entry entry) {
            setId(id);
            this.entry = entry;
        }
    }

    class LikeRelTranslator implements RelationTranslator<LikeRelation, Entry> {
        @Override
        public LikeRelation translate(Entry entry, Relationship relationship) {
            return new LikeRelation(relationship.getId(), entry);
        }
    }

    private Subcollection<Entry> generateSubcollection(int pageSize, int page, int total) throws ParseException {
        return generateRelatedDataSubcollection(pageSize, page, total);
    }

    private RelatedDataCollection<Entry> generateRelatedDataSubcollection(int pageSize, int page, int total) throws ParseException {
        RelatedDataCollection<Entry> subcollection = new RelatedDataCollection<Entry>();
        int resultSize = (pageSize*page<=total) ? pageSize : (total % pageSize);
        for (int i=0; i<resultSize; i++ ) {
            int idInt = (page-1)*pageSize+i;
            Id id = Id.severeValueOf(String.format("11111111-1111-1111-1111-111111111%03d", idInt));
            subcollection.getEntries().add( new Entry(id, "name-"+idInt));

            Relationship relationship = new Relationship();
            relationship.setId(Id.severeValueOf( String.format("00000000-1111-1111-1111-111111111%03d", idInt) ));
            relationship.setEntryId(Id.severeValueOf("00000000-0000-0000-0000-000000000000"));
            relationship.setRelatedEntryId(id);
            subcollection.getRelationshipList().put(id, relationship);
        }
        subcollection.setTotalCount( total );
        subcollection.setResultCount(resultSize);
        subcollection.setNextOffset( (page-1)*pageSize+resultSize);

        logger.debug("Generating Subcollection: " + subcollection);
        return subcollection;
    }


    @Test
    public void testGetWholeCollection() throws Exception {
        PageByPageLoader pageByPageLoader = new PageByPageLoader(2);

        List<Entry> entries = pageByPageLoader.getWholeCollection(new PageByPageLoader.SingleSubcollectionLoader<Entry>() {
            @Override
            public Subcollection<Entry> load(PageParams pageParams) {
                try {
                    return generateSubcollection(pageParams.getNumberOfEntries(), pageParams.getPageNum(), 11);
                } catch (ParseException e) {
                    throw new RuntimeException(e);
                }
            }
        });

        assertNotNull(entries);
        assertEquals( 11, entries.size());

        for (int i=0; i<entries.size(); i++) {
            Entry entry = entries.get(i);
            logger.debug("Entry " + i +" : " + entry.toString());
            assertEquals(String.format("11111111-1111-1111-1111-111111111%03d", i), entry.getId().toString());
            assertEquals("name-"+i, entry.getName());
        }
    }


    @Test
    public void testGetWholeRelationCollection() throws Exception {
        PageByPageLoader pageByPageLoader = new PageByPageLoader(2);

        List<LikeRelation> likes = pageByPageLoader.getWholeRelationCollection(
                new PageByPageLoader.SingleRelatedDataCollectionLoader<Entry>() {
                    @Override
                    public RelatedDataCollection<Entry> load(PageParams pageParams) {
                        try {
                            return generateRelatedDataSubcollection(pageParams.getNumberOfEntries(), pageParams.getPageNum(), 11);
                        } catch (ParseException e) {
                            throw new RuntimeException(e);
                        }
                    }
                }, new RelatedDataCollectionTranslator<LikeRelation, Entry>(new LikeRelTranslator()));

        assertNotNull(likes);
        assertEquals( 11, likes.size());

        for (int i=0; i<likes.size(); i++) {
            LikeRelation like = likes.get(i);
            logger.debug("Like " + i +" : " + like.toString());
            assertEquals(String.format("00000000-1111-1111-1111-111111111%03d", i), like.getId().toString());
            assertEquals(String.format("11111111-1111-1111-1111-111111111%03d", i), like.entry.getId().toString());
            assertEquals("name-"+i, like.entry.getName());
        }

    }
}
