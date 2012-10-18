package com.miquido.vtv.domainservices.internal.relationtranslation;

import com.miquido.vtv.bo.Friendship;
import com.miquido.vtv.bo.Profile;
import com.miquido.vtv.codsservices.dataobjects.Relationship;
import com.miquido.vtv.codsservices.util.relationtranslation.RelationTranslator;

/**
 * Created with IntelliJ IDEA.
 * User: ljazgar
 * Date: 31.08.12
 * Time: 17:45
 * To change this template use File | Settings | File Templates.
 */
public class FriendshipRelationTranslator implements RelationTranslator<Friendship, Profile> {

    @Override
    public Friendship translate(Profile friendProfile, Relationship relationship) {
        Friendship friendship = new Friendship();
        friendship.setId( relationship.getId() );
        friendship.setDescription(relationship.getDescription());
        friendship.setProfileId( relationship.getEntryId() );
        friendship.setFriendProfileId( relationship.getRelatedEntryId() );
        friendship.setStatus(Friendship.Status.forName( relationship.getStatus() ));

        friendship.setFriend(friendProfile);

        return friendship;
    }
}
