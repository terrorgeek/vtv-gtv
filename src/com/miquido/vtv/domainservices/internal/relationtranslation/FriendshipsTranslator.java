package com.miquido.vtv.domainservices.internal.relationtranslation;

import com.google.inject.Inject;
import com.miquido.vtv.bo.Friendship;
import com.miquido.vtv.bo.Profile;
import com.miquido.vtv.codsservices.util.relationtranslation.RelatedDataCollectionTranslator;
import com.miquido.vtv.codsservices.util.relationtranslation.RelationTranslator;

/**
 * Created with IntelliJ IDEA.
 * User: ljazgar
 * Date: 31.08.12
 * Time: 18:13
 * To change this template use File | Settings | File Templates.
 */
public class FriendshipsTranslator extends RelatedDataCollectionTranslator<Friendship, Profile> {

    @Inject
    public FriendshipsTranslator(RelationTranslator<Friendship, Profile> relationTranslator) {
        super(relationTranslator);
    }

}
